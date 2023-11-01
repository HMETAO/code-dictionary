package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.form.SnippetCategoryMenusChangeForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.SnippetCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 文章标签关系表 前端控制器
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/snippet-category")
public class SnippetCategoryController {

    @Resource
    private SnippetCategoryService snippetCategoryService;

    /**
     * 修改目录或者snippet的分组位置
     *
     * @param snippetCategoryMenusChangeForm 分组信息
     * @return 统一返回
     */
    @PutMapping
    public ResponseEntity<Result> updateSnippetCategory(@RequestBody SnippetCategoryMenusChangeForm snippetCategoryMenusChangeForm) {
        snippetCategoryService.updateSnippetCategory(snippetCategoryMenusChangeForm);
        return Result.success(HttpStatus.NO_CONTENT);
    }
}

