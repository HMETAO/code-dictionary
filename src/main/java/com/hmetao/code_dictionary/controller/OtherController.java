package com.hmetao.code_dictionary.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.hmetao.code_dictionary.form.TrendForm;
import com.hmetao.code_dictionary.form.WebSSHForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.GithubTrendService;
import com.hmetao.code_dictionary.service.OtherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/code_dictionary/api/v1/other")
public class OtherController {

    @Resource
    private GithubTrendService githubTrendService;

    @Resource
    private OtherService otherService;

    /**
     * 获取GitHub trending 列表
     *
     * @param trendForm 请求参数
     * @return trending列表
     */
    @GetMapping("github")
    public ResponseEntity<Result> githubTrend(TrendForm trendForm) throws JsonProcessingException {
        return Result.success(githubTrendService.trending(trendForm));
    }

    @PostMapping("ssh")
    public ResponseEntity<Result> ssh(@RequestBody WebSSHForm webSSHForm) {
        otherService.ssh(webSSHForm);
        return Result.success(HttpStatus.CREATED);
    }
}

