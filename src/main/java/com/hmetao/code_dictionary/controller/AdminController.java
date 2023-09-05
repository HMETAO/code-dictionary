package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/code_dictionary/api/v1/admin")
public class AdminController {

    @GetMapping("/test")
    public ResponseEntity<Result> test() {
        return Result.success();
    }

}

