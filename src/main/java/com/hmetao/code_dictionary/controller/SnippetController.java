package com.hmetao.code_dictionary.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.hmetao.code_dictionary.dto.SnippetDTO;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.SnippetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/snippet")
public class SnippetController {


    @Resource
    @Autowired
    private SnippetService snippetService;

    /**
     * 查询具体snippet
     *
     * @param id snippetId
     * @return snippetDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result> getSnippet(@PathVariable Integer id) {
        SnippetDTO snippetDTO = snippetService.getSnippet(id);
        return Result.success(snippetDTO);
    }



}

