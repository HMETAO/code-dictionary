package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.ToolDTO;
import com.hmetao.code_dictionary.entity.Tool;
import com.baomidou.mybatisplus.extension.service.IService;

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

    List<ToolDTO> getTools(Integer pageSize, Integer pageNum);
}
