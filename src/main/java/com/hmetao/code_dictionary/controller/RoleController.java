package com.hmetao.code_dictionary.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.hmetao.code_dictionary.form.QueryForm;
import com.hmetao.code_dictionary.form.RolePermissionForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/role")
public class RoleController {

    @Resource
    private RoleService roleService;


    /**
     * 查询角色列表
     *
     * @return 统一返回
     */
    @GetMapping
    public ResponseEntity<Result> getRoles() {
        return Result.success(roleService.getRoles());
    }

    /**
     * 查询角色列表
     *
     * @param queryForm 分页
     * @return 分页对象
     */
    @GetMapping("page")
    @SaCheckPermission("role-select")
    public ResponseEntity<Result> getRolesPage(QueryForm queryForm) {
        return Result.success(roleService.getRolesPage(queryForm));
    }


    /**
     * 查询单个角色信息
     *
     * @param roleId 角色ID
     * @return 统一返回
     */
    @GetMapping("{roleId}")
    @SaCheckPermission("role-select")
    public ResponseEntity<Result> getRole(@PathVariable Long roleId) {
        return Result.success(roleService.getRole(roleId));
    }


    /**
     * 插入角色
     *
     * @param rolePermissionForm 角色信息
     * @return 统一返回
     */
    @PostMapping
    @SaCheckPermission("role-insert")
    public ResponseEntity<Result> insertRole(@RequestBody RolePermissionForm rolePermissionForm) {
        roleService.insertRole(rolePermissionForm);
        return Result.success(HttpStatus.CREATED);
    }

    /**
     * 删除角色
     *
     * @param roleId 角色Id
     * @return 统一返回
     */
    @DeleteMapping("{roleId}")
    @SaCheckRole("role-delete")
    public ResponseEntity<Result> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return Result.success(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Result> updateRole(@Valid @RequestBody RolePermissionForm rolePermissionForm) {
        roleService.updateRole(rolePermissionForm);
        return Result.success(HttpStatus.CREATED);
    }
}

