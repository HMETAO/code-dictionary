package com.hmetao.code_dictionary.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.dto.UserRoleDTO;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.form.LoginForm;
import com.hmetao.code_dictionary.form.UserRegistryForm;
import com.hmetao.code_dictionary.form.UserRoleUpdateForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.UserService;
import com.hmetao.code_dictionary.utils.SaTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Objects;

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


    /**
     * 用户登录
     *
     * @param loginForm 登录表单
     * @return 统一返回
     */
    @PostMapping("login")
    public ResponseEntity<Result> login(@RequestBody @Valid LoginForm loginForm) {
        UserDTO userDTO = userService.login(loginForm);
        return Result.success(userDTO, HttpStatus.OK, "登录成功");
    }

    /**
     * 登出
     *
     * @return 统一返回
     */
    @GetMapping("logout")
    public ResponseEntity<Result> logout() {
        StpUtil.logout();
        return Result.success(HttpStatus.OK, "登出成功");
    }

    /**
     * 查询单个用户
     *
     * @param userId 用户ID
     * @return 统一返回
     */
    @GetMapping("{userId}")
    public ResponseEntity<Result> getUser(@PathVariable("userId") Long userId) {
        UserRoleDTO userRoleDTO = userService.getUser(userId);
        return Result.success(userRoleDTO);
    }

    /**
     * 用户注册
     *
     * @param userRegistryForm 注册表单
     * @return 统一返回
     */
    @PostMapping("registry")
    public ResponseEntity<Result> registry(@Valid UserRegistryForm userRegistryForm) {
        userService.registry(userRegistryForm);
        return Result.success(HttpStatus.CREATED);
    }


    /**
     * 更新单个用户
     *
     * @param userRoleUpdateForm 用户信息
     * @return 统一返回
     */
    @PostMapping("edit")
    public ResponseEntity<Result> updateUser(@Valid  UserRoleUpdateForm userRoleUpdateForm) throws IOException {
        userService.updateUser(userRoleUpdateForm);
        return Result.success(HttpStatus.CREATED);
    }


    /**
     * 校验token是否正确
     *
     * @param token token
     * @return 统一返回
     */
    @GetMapping("check")
    public ResponseEntity<Result> checkUser(@RequestParam(name = "token") String token) {
        // 判断这个token是不是自己的
        if (!Objects.equals(SaTokenUtil.getLoginUserInfo().getToken(), token)) {
            throw new ValidationException("token非本人");
        }
        StpUtil.checkActivityTimeout();
        return Result.success(HttpStatus.OK);
    }
}

