package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.entity.UserRole;
import com.hmetao.code_dictionary.exception.AccessErrorException;
import com.hmetao.code_dictionary.exception.HMETAOException;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.form.LoginForm;
import com.hmetao.code_dictionary.form.UserRegistryForm;
import com.hmetao.code_dictionary.mapper.UserMapper;
import com.hmetao.code_dictionary.properties.QiNiuProperties;
import com.hmetao.code_dictionary.properties.TencentImProperties;
import com.hmetao.code_dictionary.service.UserRoleService;
import com.hmetao.code_dictionary.service.UserService;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.QiniuUtil;
import com.hmetao.code_dictionary.utils.TLSSigAPIv2;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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


    @Resource
    private QiNiuProperties qiNiuProperties;

    @Resource
    private UserRoleService userRoleService;

    private final LocalDate localDate = LocalDate.now();

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private TencentImProperties tencentImProperties;

    private TLSSigAPIv2 tlsSigAPIv2;

    @PostConstruct
    public void initMethod() {
        tlsSigAPIv2 = new TLSSigAPIv2(tencentImProperties.getSDKAppID(), tencentImProperties.getSecretKey());
    }

    @Override
    public UserDTO login(LoginForm loginForm) {
        User userEntity = baseMapper.selectOne(new QueryWrapper<User>().eq("username", loginForm.getUsername()));
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
            registryTencentIM(user.getUsername(), user.getAvatar());

            // 分配普通用户权限
            UserRole userRole = new UserRole(BaseConstants.BASE_ROLE_USER, user.getId(), BaseConstants.BASE_ADMIN_USER);
            userRoleService.save(userRole);
        } catch (IOException e) {
            throw new HMETAOException("UserServiceImpl", " 注册用户接口 ERROR");
        }
    }

    private void registryTencentIM(String userID, String avatar) {
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
        Map<String, Object> body = restTemplate.postForObject(url, map, Map.class);
        Integer code = (Integer) Objects.requireNonNull(body).get("ErrorCode");
        if (code == null || !code.equals(0)) {
            throw new HMETAOException("UserServiceImpl", (String) body.get("ErrorInfo"));
        }
    }


    private boolean checkPassword(User userEntity, String password) {
        return Objects.equals(userEntity.getPassword(),
                SaSecureUtil.md5BySalt(password, BaseConstants.SALT_PASSWORD));
    }
}
