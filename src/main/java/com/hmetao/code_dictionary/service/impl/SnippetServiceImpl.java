package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmetao.code_dictionary.dto.SnippetDTO;
import com.hmetao.code_dictionary.entity.Snippet;
import com.hmetao.code_dictionary.entity.SnippetCategory;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.form.SnippetForm;
import com.hmetao.code_dictionary.mapper.SnippetMapper;
import com.hmetao.code_dictionary.service.SnippetCategoryService;
import com.hmetao.code_dictionary.service.SnippetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.utils.MapUtils;
import com.hmetao.code_dictionary.utils.SaTokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Service
public class SnippetServiceImpl extends ServiceImpl<SnippetMapper, Snippet> implements SnippetService {

    @Resource
    private SnippetCategoryService snippetCategoryService;

    @Override
    public SnippetDTO getSnippet(Integer id) {
        // 获取登录用户
        User sysUser = SaTokenUtils.getLoginUserInfo();
        // 获取该用户的snippet
        Snippet snippet = baseMapper.selectOne(new LambdaQueryWrapper<Snippet>()
                .eq(Snippet::getUid, sysUser.getId())
                .eq(Snippet::getId, id));
        Assert.notNull(snippet, "snippet参数错误");
        // 映射成DTO
        return MapUtils.beanMap(snippet, SnippetDTO.class);
    }

    @Override
    @Transactional
    public void insertSnippet(SnippetForm snippetForm) {
        // 获取登录用户
        User sysUser = SaTokenUtils.getLoginUserInfo();

        SnippetCategory exit = snippetCategoryService.getOne(new LambdaQueryWrapper<SnippetCategory>()
                .eq(SnippetCategory::getCategoryId,
                        snippetForm.getCategoryId()).eq(SnippetCategory::getSnippetTitle, snippetForm.getTitle()), true);
        if (exit != null) {
            throw new RuntimeException("在改分组下已存在此 Title 的 Snippet");
        }

        // 写入snippet
        Snippet snippet = MapUtils.beanMap(snippetForm, Snippet.class);
        snippet.setUid(sysUser.getId());
        baseMapper.insert(snippet);

        // 写入中间表
        SnippetCategory snippetCategory = MapUtils.beanMap(snippetForm, SnippetCategory.class);
        snippetCategory.setSnippetId(snippet.getId());
        snippetCategory.setSnippetTitle(snippet.getTitle());
        snippetCategoryService.save(snippetCategory);
    }

    @Override
    @Transactional
    public void deleteSnippet(Long snippetId) {
        // 获取登录用户
        User sysUser = SaTokenUtils.getLoginUserInfo();

        baseMapper.delete(new LambdaQueryWrapper<Snippet>()
                .eq(Snippet::getUid, sysUser.getId())
                .eq(Snippet::getId, snippetId));

        snippetCategoryService.remove(new LambdaQueryWrapper<SnippetCategory>()
                .eq(SnippetCategory::getSnippetId, snippetId));
    }

    @Override
    public void updateSnippet(SnippetForm snippetForm) {
        Snippet snippet = MapUtils.beanMap(snippetForm, Snippet.class);
        baseMapper.updateById(snippet);
    }
}
