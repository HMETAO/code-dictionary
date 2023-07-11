package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.entity.UserRole;
import com.hmetao.code_dictionary.exception.AccessErrorException;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.form.LoginForm;
import com.hmetao.code_dictionary.form.UserRegistryForm;
import com.hmetao.code_dictionary.mapper.UserMapper;
import com.hmetao.code_dictionary.properties.QiNiuProperties;
import com.hmetao.code_dictionary.service.UserRoleService;
import com.hmetao.code_dictionary.service.UserService;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.QiniuUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

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
            // 用户信息放入token-session
            StpUtil.getTokenSession().set(BaseConstants.LOGIN_USERINFO_SESSION_KEY, userEntity);
            // 用户信息放入user-session
            StpUtil.getSession().set(BaseConstants.LOGIN_USERINFO_SESSION_KEY, userEntity);
            UserDTO userDTO = MapUtil.beanMap(userEntity, UserDTO.class);
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
            // 分配普通用户权限
            UserRole userRole = new UserRole(BaseConstants.BASE_ROLE_USER, user.getId(), BaseConstants.BASE_ADMIN_USER);
            userRoleService.save(userRole);
        } catch (IOException e) {
            log.error("UserServiceImpl === > 注册用户接口 ERROR ", e);
            throw new RuntimeException(e);
        }
    }


    private boolean checkPassword(User userEntity, String password) {
        return Objects.equals(userEntity.getPassword(),
                SaSecureUtil.md5BySalt(password, BaseConstants.SALT_PASSWORD));
    }
}
