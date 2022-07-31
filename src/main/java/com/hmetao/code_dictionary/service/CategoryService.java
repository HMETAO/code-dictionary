package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.CategoryDTO;
import com.hmetao.code_dictionary.dto.CategorySnippetMenusDTO;
import com.hmetao.code_dictionary.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 文章标签表 服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
public interface CategoryService extends IService<Category> {

    List<CategorySnippetMenusDTO> getCategorySnippetMenus();
}
