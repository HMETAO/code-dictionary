package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.form.LoginForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/user")
public class UserController {

    @Resource
    private UserService userService;


    @PostMapping("login")
    public ResponseEntity<Result> login(@RequestBody LoginForm loginForm) {
        userService.login(loginForm);
        return Result.success(HttpStatus.OK, "登录成功");
    }
}

