package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.dto.FriendInformationDTO;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.FriendInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/information")
public class FriendInformationController {
    @Resource
    private FriendInformationService friendInformationService;

    /**
     * 查询聊天记录
     *
     * @param id 用户id
     * @return 聊天记录列表
     */
    @GetMapping("{id}")
    public ResponseEntity<Result> getInformation(@PathVariable("id") Long id) {
        List<FriendInformationDTO> information = friendInformationService.getInformation(id);
        return Result.success(information);
    }
}

