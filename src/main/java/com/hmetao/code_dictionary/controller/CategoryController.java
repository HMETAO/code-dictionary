package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.form.CategoryForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    /**
     * 新建category
     *
     * @param categoryForm category数据
     * @return 统一返回
     */
    @PostMapping
    public ResponseEntity<Result> insertCategory(@RequestBody CategoryForm categoryForm) {
        return Result.success(categoryService.insertCategory(categoryForm), HttpStatus.CREATED);
    }

    /**
     * 删除category
     *
     * @param categoryId categoryId
     * @return 统一返回
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Result> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success(HttpStatus.NO_CONTENT);
    }
}

