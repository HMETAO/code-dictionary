package com.hmetao.code_dictionary.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.form.LoginForm;
import com.hmetao.code_dictionary.form.UserRegistryForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        UserDTO userDTO = userService.login(loginForm);
        return Result.success(userDTO, HttpStatus.OK, "登录成功");
    }

    @GetMapping("logout")
    public ResponseEntity<Result> logout() {
        StpUtil.logout();
        return Result.success(HttpStatus.OK, "登出成功");
    }

    @PostMapping("registry")
    public ResponseEntity<Result> registry(@Validated @RequestBody UserRegistryForm userRegistryForm) {
        userService.registry(userRegistryForm);
        return Result.success(HttpStatus.CREATED);
    }
}

