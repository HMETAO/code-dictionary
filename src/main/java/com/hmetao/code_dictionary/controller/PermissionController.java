package com.hmetao.code_dictionary.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 权限 前端控制器
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/permission")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @GetMapping
    public ResponseEntity<Result> getPermission() {
        return Result.success(permissionService.getPermission());
    }

}

