package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.exception.AccessErrorException;
import com.hmetao.code_dictionary.exception.HMETAOException;
import com.hmetao.code_dictionary.form.LoginForm;
import com.hmetao.code_dictionary.mapper.UserMapper;
import com.hmetao.code_dictionary.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public void login(LoginForm loginForm) {
        User useEntity = baseMapper.selectOne(new QueryWrapper<User>().eq("username", loginForm.getUsername()));
        String password = loginForm.getPassword();
        // 判断密码是否相同
        if (Objects.equals(useEntity.getPassword(),
                SaSecureUtil.md5BySalt(password, BaseConstants.SALT_PASSWORD))) {
            StpUtil.login(useEntity.getId());
        } else {
            throw new AccessErrorException("登录失败：请检查用户名或密码");
        }
    }
}
