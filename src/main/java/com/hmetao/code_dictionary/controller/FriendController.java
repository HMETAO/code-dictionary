package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.dto.FriendDTO;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.FriendService;
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
 * @since 2023-05-25
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/friend")
public class FriendController {
    @Resource
    private FriendService friendService;

    /**
     * 获取朋友列表
     *
     * @return 朋友列表
     */
    @GetMapping
    public ResponseEntity<Result> getFriends() {
        List<FriendDTO> friends = friendService.getFriends();
        return Result.success(friends, HttpStatus.OK);
    }
}

