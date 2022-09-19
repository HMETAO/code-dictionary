package com.hmetao.code_dictionary.controller;


import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.ToolDTO;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.service.ToolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author HMETAO
 * @since 2022-08-26
 */
@RestController
@RequestMapping("/code_dictionary/api/v1/tool")
public class ToolController {

    @Resource
    private ToolService toolService;


    /**
     * 查询tools
     *
     * @param pageSize 条数
     * @param pageNum  页数
     * @return tools列表
     */
    @GetMapping
    public ResponseEntity<Result> getTools(@RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
                                           @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum) {
        return Result.success(toolService.getTools(pageSize, pageNum));
    }

    /**
     * 上传tools
     *
     * @param files 上传列表
     * @return 统一返回
     */
    @PostMapping("upload")
    public ResponseEntity<Result> upload(@RequestBody List<MultipartFile> files) {
        toolService.upload(files);
        return Result.success(HttpStatus.CREATED);
    }


    /**
     * 下载tools
     *
     * @param ids      ids
     * @param response 响应
     */
    @GetMapping("download")
    public void downloadTools(@RequestParam("ids") List<Long> ids, HttpServletResponse response) throws IOException {
        toolService.download(ids, response);
    }
}

