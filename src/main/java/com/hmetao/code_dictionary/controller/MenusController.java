package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.dto.MenusDTO;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.MenusService;
import org.springframework.http.HttpStatus;
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
 * @since 2022-07-09
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/menus")
public class MenusController {
    @Resource
    private MenusService menuService;

    /**
     * 获取菜单列表
     *
     * @return 菜单列表
     */
    @GetMapping
    public ResponseEntity<Result> getMenus() {
        List<MenusDTO> menus = menuService.getMenus();
        return Result.success(menus, HttpStatus.OK);
    }
}

