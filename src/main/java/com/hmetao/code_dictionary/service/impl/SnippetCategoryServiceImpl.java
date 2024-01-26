package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.entity.Category;
import com.hmetao.code_dictionary.entity.SnippetCategory;
import com.hmetao.code_dictionary.form.SnippetCategoryMenusChangeForm;
import com.hmetao.code_dictionary.mapper.CategoryMapper;
import com.hmetao.code_dictionary.mapper.SnippetCategoryMapper;
import com.hmetao.code_dictionary.service.SnippetCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 文章标签关系表 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Service
public class SnippetCategoryServiceImpl extends ServiceImpl<SnippetCategoryMapper, SnippetCategory> implements SnippetCategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public void updateSnippetCategory(SnippetCategoryMenusChangeForm snippetCategoryMenusChangeForm) {
        // 判断是否是分类对分类的操作
        if (snippetCategoryMenusChangeForm.getCategoryToCategory()) {
            Category category = new Category();
            category.setId(snippetCategoryMenusChangeForm.getCurrentId());
            category.setParentId(snippetCategoryMenusChangeForm.getPid());
            categoryMapper.updateById(category);
        } else {
            // 否则是分类对文章的操作
            SnippetCategory snippetCategory = new SnippetCategory();
            if (snippetCategoryMenusChangeForm.getPid() == 0) {
                throw new RuntimeException("不能将snippet挂到最外层");
            }
            snippetCategory.setCategoryId(snippetCategoryMenusChangeForm.getPid());
            baseMapper.update(snippetCategory, Wrappers.lambdaUpdate(SnippetCategory.class)
                    .eq(SnippetCategory::getSnippetId, snippetCategoryMenusChangeForm.getCurrentId()));
        }
    }
}
