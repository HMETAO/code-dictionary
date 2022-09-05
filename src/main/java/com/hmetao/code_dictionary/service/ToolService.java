package com.hmetao.code_dictionary.service;

import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.ToolDTO;
import com.hmetao.code_dictionary.entity.Tool;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-08-26
 */
public interface ToolService extends IService<Tool> {

    PageInfo<ToolDTO> getTools(Integer pageSize, Integer pageNum);

    void upload(List<MultipartFile> files);
}
