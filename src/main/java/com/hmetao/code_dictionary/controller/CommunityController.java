package com.hmetao.code_dictionary.controller;

import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.CommunityDTO;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.CommunityService;
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
 * @since 2023-07-04
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/community")
public class CommunityController {

    @Resource
    private CommunityService communityService;


    /**
     * 查询所有的communities列表
     *
     * @param pageSize 显示条数
     * @param pageNum  当前页
     * @return 统一返回
     */
    @GetMapping
    public ResponseEntity<Result> getCommunities(@RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
                                                 @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum) {
        PageInfo<CommunityDTO> communities = communityService.getCommunities(pageNum, pageSize);
        return Result.success(communities);
    }
}
