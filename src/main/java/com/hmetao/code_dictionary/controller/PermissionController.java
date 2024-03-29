package com.hmetao.code_dictionary.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hmetao.code_dictionary.entity.Permission;
import com.hmetao.code_dictionary.form.PermissionUpdateForm;
import com.hmetao.code_dictionary.form.QueryForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

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

    /**
     * 查询所有权限
     *
     * @return 统一返回
     */
    @GetMapping
    public ResponseEntity<Result> getPermissions(QueryForm queryForm) throws JsonProcessingException {
        return Result.success(permissionService.getPermissions(queryForm));
    }

    /**
     * 更新权限
     *
     * @param permissionUpdateForm 权限信息
     * @return 统一返回
     */
    @PutMapping
    @SaCheckPermission("permission-update")
    public ResponseEntity<Result> updatePermission(@RequestBody @Valid PermissionUpdateForm permissionUpdateForm) {
        permissionService.updatePermission(permissionUpdateForm);
        return Result.success(HttpStatus.NO_CONTENT);
    }

    /**
     * 查询当个permission
     *
     * @param permissionId permissionId
     * @return 统一返回
     */
    @GetMapping("{permissionId}")
    @SaCheckPermission("permission-select")
    public ResponseEntity<Result> getPermission(@PathVariable("permissionId") Long permissionId) {
        return Result.success(permissionService.getPermission(permissionId));
    }

    /**
     * 删除permission
     *
     * @param permissionId permissionId
     * @return 统一返回
     */
    @DeleteMapping("{permissionId}")
    public ResponseEntity<Result> deletePermission(@PathVariable("permissionId") Long permissionId) {
        permissionService.deletePermission(permissionId);
        return Result.success(HttpStatus.NO_CONTENT);
    }

    /**
     * 插入permission
     *
     * @param permission permission信息
     * @return 统一返回
     */
    @PostMapping
    public ResponseEntity<Result> insertPermission(@RequestBody @Valid Permission permission) {
        permissionService.insertPermission(permission);
        return Result.success(HttpStatus.CREATED);
    }
}

