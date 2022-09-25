package com.hmetao.code_dictionary.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.hmetao.code_dictionary.form.TrendForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.GithubTrendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author HMETAO
 * @since 2022-09-24
 */
@RestController
@RequestMapping("/code_dictionary/github")
public class GithubTrendController {

    @Resource
    private GithubTrendService githubTrendService;

    /**
     * 获取GitHub trending 列表
     *
     * @param trendForm 请求参数
     * @return trending列表
     */
    @GetMapping
    public ResponseEntity<Result> trending(TrendForm trendForm) throws JsonProcessingException {
        return Result.success(githubTrendService.trending(trendForm));

    }
}

