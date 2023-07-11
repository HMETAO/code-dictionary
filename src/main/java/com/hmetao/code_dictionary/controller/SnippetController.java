package com.hmetao.code_dictionary.controller;


import com.hmetao.code_dictionary.dto.SnippetDTO;
import com.hmetao.code_dictionary.dto.SnippetUploadImageDTO;
import com.hmetao.code_dictionary.form.ReceiveSnippetForm;
import com.hmetao.code_dictionary.form.RunCodeForm;
import com.hmetao.code_dictionary.form.SnippetForm;
import com.hmetao.code_dictionary.form.SnippetUploadImageForm;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.SnippetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

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
    private SnippetService snippetService;


    @PostMapping("/run")
    public ResponseEntity<Result> runCode(@RequestBody RunCodeForm runCodeForm) {
        return Result.success(snippetService.runCode(runCodeForm));
    }


    /**
     * 上传markdown图片
     *
     * @param snippetUploadImageForm files
     * @return 返回上传后的url
     */
    @PostMapping("/img/upload")
    public ResponseEntity<Result> uploadImage(SnippetUploadImageForm snippetUploadImageForm) {
        SnippetUploadImageDTO snippetUploadImageDTO = snippetService.uploadImage(snippetUploadImageForm);
        return Result.success(snippetUploadImageDTO);
    }

    /**
     * 查询具体snippet
     *
     * @param snippetId snippetId
     * @return snippetDTO
     */
    @GetMapping("/{snippetId}")
    public ResponseEntity<Result> getSnippet(@PathVariable Integer snippetId) {
        SnippetDTO snippetDTO = snippetService.getSnippet(snippetId);
        return Result.success(snippetDTO);
    }

    /**
     * 插入新的snippet
     *
     * @param snippetForm snippet信息
     * @return 统一返回
     */
    @PostMapping
    public ResponseEntity<Result> insertSnippet(@Valid @RequestBody SnippetForm snippetForm) {
        try {
            return Result.success(snippetService.insertSnippet(snippetForm), HttpStatus.CREATED);
        } catch (Exception e) {
            return Result.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 删除snippet
     *
     * @param snippetId snippetId
     * @return 统一返回
     */
    @DeleteMapping("/{snippetId}")
    public ResponseEntity<Result> deleteSnippet(@PathVariable Long snippetId) {
        snippetService.deleteSnippet(snippetId);
        return Result.success(HttpStatus.NO_CONTENT);
    }

    /**
     * 更新snippet
     *
     * @param snippetForm snippet信息
     * @return 统一返回
     */
    @PutMapping
    public ResponseEntity<Result> updateSnippet(@RequestBody SnippetForm snippetForm) {
        snippetService.updateSnippet(snippetForm);
        return Result.success(HttpStatus.CREATED);
    }

    /**
     * 接收用户的Snippet
     *
     * @param receiveSnippetForm receiveSnippet信息
     * @return 统一返回
     */
    @PostMapping("receive")
    public ResponseEntity<Result> receiveSnippet(@RequestBody ReceiveSnippetForm receiveSnippetForm) {
        snippetService.receiveSnippet(receiveSnippetForm);
        return Result.success(HttpStatus.CREATED);
    }


}