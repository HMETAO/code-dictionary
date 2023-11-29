package com.hmetao.code_dictionary.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.hmetao.code_dictionary.form.QueryForm;
import com.hmetao.code_dictionary.form.UserStatusForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/code_dictionary/api/v1/admin/user")
public class AdminUserController {


    @Resource
    private UserService userService;

    /**
     * 获取用户列表
     *
     * @param queryForm 请求参数
     * @return 统一返回
     */
    @GetMapping
    @SaCheckPermission("user-select")
    public ResponseEntity<Result> getUsers(QueryForm queryForm) {
        return Result.success(userService.getUsers(queryForm));
    }


    /**
     * 更新用户状态
     *
     * @param userStatusForm 请求参数
     * @return 统一返回
     */
    @PutMapping("status")
    @SaCheckPermission("user-update")
    public ResponseEntity<Result> updateStatus(@RequestBody UserStatusForm userStatusForm) {
        userService.updateStatus(userStatusForm);
        return Result.success();
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 统一返回
     */
    @DeleteMapping("{userId}")
    @SaCheckPermission("user-delete")
    public ResponseEntity<Result> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUserId(userId);
        return Result.success();
    }
}

