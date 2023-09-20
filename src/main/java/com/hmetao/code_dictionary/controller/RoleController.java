package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
}

