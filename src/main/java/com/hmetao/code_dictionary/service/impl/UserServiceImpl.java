package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.exception.AccessErrorException;
import com.hmetao.code_dictionary.exception.HMETAOException;
import com.hmetao.code_dictionary.form.LoginForm;
import com.hmetao.code_dictionary.mapper.UserMapper;
import com.hmetao.code_dictionary.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.utils.MapUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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

    @Resource
    private HttpServletResponse httpServletResponse;

    @Override
    public UserDTO login(LoginForm loginForm) {
        User userEntity = baseMapper.selectOne(new QueryWrapper<User>().eq("username", loginForm.getUsername()));
        String password = loginForm.getPassword();
        // 判断密码是否相同
        if (Objects.equals(userEntity.getPassword(),
                SaSecureUtil.md5BySalt(password, BaseConstants.SALT_PASSWORD))) {
            // 登录成功
            StpUtil.login(userEntity.getId());
            // 存储用户信息
            StpUtil.getSession().set("userInfo", userEntity);
            // 获取token信息
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            // 将token写入
            httpServletResponse.setHeader(tokenInfo.getTokenName(), tokenInfo.getTokenValue());
            return MapUtils.beanMap(userEntity, UserDTO.class);
        } else {
            throw new AccessErrorException("登录失败：请检查用户名或密码");
        }
    }
}
