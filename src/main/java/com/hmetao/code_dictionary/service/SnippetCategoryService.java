package com.hmetao.code_dictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmetao.code_dictionary.entity.SnippetCategory;
import com.hmetao.code_dictionary.form.SnippetCategoryMenusChangeForm;

/**
 * <p>
 * 文章标签关系表 服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
public interface SnippetCategoryService extends IService<SnippetCategory> {

    void updateSnippetCategory(SnippetCategoryMenusChangeForm snippetCategoryMenusChangeForm);
}
