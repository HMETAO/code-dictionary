package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.form.QueryForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Result> getUsers(QueryForm queryForm) {
        return Result.success(userService.getUsers(queryForm));
    }


}

