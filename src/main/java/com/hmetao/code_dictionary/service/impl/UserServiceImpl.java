package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.dto.UserInfoDTO;
import com.hmetao.code_dictionary.dto.UserRoleDTO;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.entity.UserRole;
import com.hmetao.code_dictionary.exception.AccessErrorException;
import com.hmetao.code_dictionary.exception.HMETAOException;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.form.*;
import com.hmetao.code_dictionary.mapper.UserMapper;
import com.hmetao.code_dictionary.mapper.UserRoleMapper;
import com.hmetao.code_dictionary.po.UserRolePO;
import com.hmetao.code_dictionary.properties.QiNiuProperties;
import com.hmetao.code_dictionary.properties.TencentImProperties;
import com.hmetao.code_dictionary.service.UserRoleService;
import com.hmetao.code_dictionary.service.UserService;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.QiniuUtil;
import com.hmetao.code_dictionary.utils.SaTokenUtil;
import com.hmetao.code_dictionary.utils.TLSSigAPIv2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String LOG_INFO_CLASS_KEY = "UserServiceImpl === > ";
    @Resource
    private QiNiuProperties qiNiuProperties;

    @Resource
    private UserRoleService userRoleService;

    private final LocalDate localDate = LocalDate.now();

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private TencentImProperties tencentImProperties;

    @Resource
    private UserRoleMapper userRoleMapper;

    private TLSSigAPIv2 tlsSigAPIv2;

    @PostConstruct
    public void initMethod() {
        tlsSigAPIv2 = new TLSSigAPIv2(tencentImProperties.getSDKAppID(), tencentImProperties.getSecretKey());
    }

    @Override
    public UserDTO login(LoginForm loginForm) {
        User userEntity = baseMapper.selectOne(new QueryWrapper<User>().eq("username", loginForm.getUsername().trim()));
        String password = loginForm.getPassword();
        if (userEntity == null)
            throw new AccessErrorException("登录失败：请先完成注册操作");
        // 判断密码是否相同
        if (checkPassword(userEntity, password)) {
            // 登录成功
            StpUtil.login(userEntity.getId(), true);
            UserDTO userDTO = MapUtil.beanMap(userEntity, UserDTO.class);
            String userSig = tlsSigAPIv2.genUserSig(userEntity.getUsername(), 86400);
            userDTO.setUserSig(userSig);
            // 用户信息放入token-session
            StpUtil.getTokenSession().set(BaseConstants.LOGIN_USERINFO_SESSION_KEY, userDTO);
            // 用户信息放入user-session
            StpUtil.getSession().set(BaseConstants.LOGIN_USERINFO_SESSION_KEY, userDTO);
            // 获取token
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            userDTO.setToken(tokenInfo.getTokenValue());
            return userDTO;
        }
        throw new AccessErrorException("登录失败：请检查用户名或密码");
    }

    @Override
    @Transactional
    public void registry(UserRegistryForm userRegistryForm) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userRegistryForm.getUsername())
                .or().eq(User::getMobile, userRegistryForm.getMobile()));
        if (count > 0) {
            throw new ValidationException("注册失败：用户名或手机号重复");
        }

        try {
            User user = MapUtil.beanMap(userRegistryForm, User.class);
            user.setPassword(SaSecureUtil.md5BySalt(user.getPassword(), BaseConstants.SALT_PASSWORD));
            MultipartFile file = userRegistryForm.getFile();
            // 若上传了文件 切 上传的文件并不为空
            if (file != null && !file.isEmpty()) {
                StringBuilder fileName = new StringBuilder(BaseConstants.QINIU_OSS_UPLOAD_PREFIX);
                // filename: avatar/date/UUID.jpg
                fileName.append(localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                        .append(UUID.randomUUID().toString().replaceAll("-", ""))
                        .append(".")
                        .append(Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1]);
                // 上传头像文件
                QiniuUtil.upload2qiniu(qiNiuProperties, file.getBytes(), fileName.toString());
                user.setAvatar(fileName.insert(0, qiNiuProperties.getUrl()).toString());
            }
            // 插入数据库
            baseMapper.insert(user);
            // 将用户导入腾讯IM平台
            registryTencentIMAccount(user.getUsername(), user.getAvatar());

            // 分配普通用户权限
            UserRole userRole = new UserRole(BaseConstants.BASE_ROLE_USER, user.getId(), BaseConstants.BASE_ADMIN_USER);
            userRoleService.save(userRole);
        } catch (IOException e) {
            throw new HMETAOException("UserServiceImpl", " 注册用户接口 ERROR");
        }
    }

    @Override
    public PageInfo<UserRoleDTO> getUsers(QueryForm queryForm) {
        PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        // 分页查询出全部的user
        List<UserInfoDTO> userInfoList = baseMapper.getUserInfoList();

        List<Long> userIds = new ArrayList<>();
        List<UserRoleDTO> userRoleDTOList = userInfoList.stream()
                .map(userInfoDTO -> {
                    // 保存用户的id
                    userIds.add(userInfoDTO.getId());
                    // 转成DTO对象
                    return MapUtil.beanMap(userInfoDTO, UserRoleDTO.class);
                }).collect(Collectors.toList());
        // 查询出所有用户对应的role
        List<UserRolePO> userRolePOS = userRoleMapper.getRolesByUserIds(userIds);

        Map<Long, List<UserRolePO>> userRolePOMap = userRolePOS.stream().collect(Collectors.groupingBy(UserRolePO::getId));
        // 填入对应用户的roles
        for (UserRoleDTO userRoleDTO : userRoleDTOList) {
            userRoleDTO.setRoles(findAndBuildRoleDTO(userRolePOMap, userRoleDTO.getId()));
        }
        return MapUtil.PageInfoCopy(userInfoList, userRoleDTOList);
    }

    @Override
    @Transactional
    public void deleteUserId(Long userId) {
        User user = baseMapper.selectById(userId);
        if (user == null) throw new ValidationException("未找到需要删除的用户");

        // 删除用户与角色之间的联系
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));

        // 删除该用户
        baseMapper.deleteById(userId);

        // 删除腾讯IM账号
        deleteTencentIMAccount(user.getUsername());
    }

    @Override
    public UserRoleDTO getUser(Long userId) {
        UserDTO sysUser = SaTokenUtil.getLoginUserInfo();
        // 判断是否是登录用户，查自己放行
        if (!Objects.equals(sysUser.getId(), userId)) {
            // 判断是否有select权限
            StpUtil.checkPermission("user-select");
        }
        // 查询实际要的用户信息
        User user = baseMapper.selectById(userId);
        if (user == null) throw new ValidationException("未找到需要删除的用户");
        UserRoleDTO userRoleDTO = MapUtil.beanMap(user, UserRoleDTO.class);
        // 查询用户的role
        userRoleDTO.setRoles(userRoleMapper.getRoleList(user.getId()));
        return userRoleDTO;
    }

    @Override
    @Transactional
    public void updateUser(UserRoleUpdateForm baseUserInfoForm) {
        UserDTO sysUser = SaTokenUtil.getLoginUserInfo();
        // 判断更新的是不是自己
        if (!Objects.equals(baseUserInfoForm.getId(), sysUser.getId())) {
            // 检查是否有修改别人信息的权限
            StpUtil.checkPermission("user-update");
        }

        // 查询出要修改用户
        User user = baseMapper.selectById(baseUserInfoForm.getId());
        // 更新用户信息
        baseMapper.updateById(MapUtil.beanMap(baseUserInfoForm, User.class));

        // 修改用户的角色
        ArrayList<Long> roles = baseUserInfoForm.getRoles();
        if (!CollectionUtils.isEmpty(roles))
            coverUserRole(sysUser.getId(), user.getId(), roles);

        // 如果用户修改了用户名需要同步修改腾讯IM的用户名
        if (!user.getUsername().equals(baseUserInfoForm.getUsername())) {
            deleteTencentIMAccount(user.getUsername());
            registryTencentIMAccount(baseUserInfoForm.getUsername(), user.getAvatar());
        }

    }

    private void coverUserRole(Long sysUserId, Long userId, ArrayList<Long> roles) {
        // 覆盖用户角色
        log.info(LOG_INFO_CLASS_KEY + "{} 赋予 {} 权限：{}", sysUserId, userId, roles);
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        // 创建 UserRole 并批量保存
        List<UserRole> userRoles = roles.stream()
                .map(roleId -> new UserRole(roleId, userId, sysUserId))
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoles);
    }


    private void deleteTencentIMAccount(String username) {
        log.info(LOG_INFO_CLASS_KEY + "删除腾讯IM账号 {}", username);
        String url = UriComponentsBuilder.fromUriString("https://console.tim.qq.com/v4/im_open_login_svc/account_delete")
                .queryParam("sdkappid", tencentImProperties.getSDKAppID())
                .queryParam("identifier", "HMETAO")
                .queryParam("usersig", tlsSigAPIv2.genUserSig("HMETAO", 86400))
                .queryParam("random", RandomUtil.randomLong(4294967295L))
                .queryParam("contentType", "json").toUriString();
        HashMap<String, ArrayList<HashMap<String, String>>> map = new HashMap<>();
        // 删除用户的账号（使用用户名标识的ID）
        ArrayList<HashMap<String, String>> accounts = new ArrayList<>();
        HashMap<String, String> obj = new HashMap<>();
        obj.put("UserID", username);
        accounts.add(obj);
        map.put("DeleteItem", accounts);
        tencentIMRequestHandler(url, map);
    }

    private <T> void tencentIMRequestHandler(String url, HashMap<String, T> map) {
        Map<String, Object> body = restTemplate.postForObject(url, map, Map.class);
        Integer code = (Integer) Objects.requireNonNull(body).get("ErrorCode");
        if (code == null || !code.equals(0)) {
            throw new HMETAOException("UserServiceImpl", (String) body.get("ErrorInfo"));
        }
    }


    private static List<RoleDTO> findAndBuildRoleDTO(Map<Long, List<UserRolePO>> userRolePOMap, Long userId) {
        return userRolePOMap.getOrDefault(userId, Collections.emptyList())
                .stream()
                .map(po -> new RoleDTO(po.getId(), po.getRoleName(), po.getRoleSign()))
                .collect(Collectors.toList());
    }

    private void registryTencentIMAccount(String userID, String avatar) {
        log.info(LOG_INFO_CLASS_KEY + "注册腾讯IM账号 {}", userID);
        String url = UriComponentsBuilder.fromUriString("https://console.tim.qq.com/v4/im_open_login_svc/account_import")
                .queryParam("sdkappid", tencentImProperties.getSDKAppID())
                .queryParam("identifier", "HMETAO")
                .queryParam("usersig", tlsSigAPIv2.genUserSig("HMETAO", 86400))
                .queryParam("random", RandomUtil.randomLong(4294967295L))
                .queryParam("contentType", "json").toUriString();
        HashMap<String, String> map = new HashMap<>();
        map.put("UserID", userID);
        if (!StringUtils.isEmpty(avatar))
            map.put("FaceUrl", avatar);
        tencentIMRequestHandler(url, map);
    }


    private boolean checkPassword(User userEntity, String password) {
        return Objects.equals(userEntity.getPassword(),
                SaSecureUtil.md5BySalt(password, BaseConstants.SALT_PASSWORD));
    }
}
