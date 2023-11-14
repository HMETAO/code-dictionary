package com.hmetao.code_dictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmetao.code_dictionary.dto.CategorySnippetMenusDTO;
import com.hmetao.code_dictionary.entity.Category;
import com.hmetao.code_dictionary.form.CategoryForm;

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

    List<CategorySnippetMenusDTO> getCategorySnippetMenus(Boolean snippet);

    CategorySnippetMenusDTO insertCategory(CategoryForm categoryForm);

    void deleteCategory(Long categoryId);

    List<Category> generateInitialCategory(Long userId);
}
