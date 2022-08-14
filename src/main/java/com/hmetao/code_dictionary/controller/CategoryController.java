package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.dto.CategoryDTO;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 标签表 前端控制器
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取分类片段展示菜单
     *
     * @return 菜单
     */
    @GetMapping("/menus")
    public ResponseEntity<Result> getCategorySnippetMenus(@RequestParam(defaultValue = "true") Boolean snippet) {
        return Result.success(categoryService.getCategorySnippetMenus(snippet));
    }
}

