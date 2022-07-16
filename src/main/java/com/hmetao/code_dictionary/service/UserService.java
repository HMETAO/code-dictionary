package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hmetao.code_dictionary.form.LoginForm;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
public interface UserService extends IService<User> {

    void login(LoginForm loginForm);
}
