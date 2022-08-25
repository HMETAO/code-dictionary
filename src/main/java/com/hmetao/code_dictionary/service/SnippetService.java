package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.SnippetDTO;
import com.hmetao.code_dictionary.entity.Snippet;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hmetao.code_dictionary.form.SnippetForm;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
public interface SnippetService extends IService<Snippet> {

    SnippetDTO getSnippet(Integer id);

    void insertSnippet(SnippetForm snippetForm);

    void deleteSnippet(Long snippetId);

    void updateSnippet(SnippetForm snippetForm);

}
