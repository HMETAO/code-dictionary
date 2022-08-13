package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.BaseTreeDTO;
import com.hmetao.code_dictionary.dto.CategorySnippetMenusDTO;
import com.hmetao.code_dictionary.entity.Category;
import com.hmetao.code_dictionary.entity.SnippetCategory;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.mapper.CategoryMapper;
import com.hmetao.code_dictionary.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.service.SnippetCategoryService;
import com.hmetao.code_dictionary.utils.MapUtils;
import com.hmetao.code_dictionary.utils.SaTokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章标签表 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private SnippetCategoryService snippetCategoryService;

    @SuppressWarnings("all")
    @Override
    public List<CategorySnippetMenusDTO> getCategorySnippetMenus() {
        // 获取登录用户
        User sysUser = SaTokenUtils.getLoginUserInfo();

        // 查询出该用户所有的category
        ArrayList<Category> categories = new ArrayList<>(baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, sysUser.getId())));
        // 转换成DTO后续构造为树形结构
        List<CategorySnippetMenusDTO> categorySnippetMenusDTOS = categories.stream()
                .map(category -> {
                    CategorySnippetMenusDTO categorySnippetMenusDTO =
                            new CategorySnippetMenusDTO(String.valueOf(category.getId()),
                                    category.getName(),
                                    String.valueOf(category.getParentId()));
                    return categorySnippetMenusDTO;
                }).collect(Collectors.toList());

        // 查出category下的所有snippet
        List<SnippetCategory> snippets = getSnippetCategories(categories);

        // 按category分组找出每个category下的snippet(类似每个文件夹下的文件 category就是文件夹 snippet就是文件)
        Map<String, List<CategorySnippetMenusDTO>> categorySnippetMap = snippets.stream().map(snippet -> {
            CategorySnippetMenusDTO categorySnippetMenusDTO =
                    new CategorySnippetMenusDTO("sn-" + snippet.getSnippetId(),
                            snippet.getSnippetTitle(),
                            String.valueOf(snippet.getCategoryId()),
                            true);
            return categorySnippetMenusDTO;
        }).collect(Collectors.groupingBy(CategorySnippetMenusDTO::getParentId));


        // 构建树状结构
        return (List<CategorySnippetMenusDTO>) BaseTreeDTO.buildTree(categorySnippetMenusDTOS, "0", node -> {
            String categoryId = node.getId();
            if (categorySnippetMap.containsKey(categoryId)) {
                List<CategorySnippetMenusDTO> nodeChildren = (List<CategorySnippetMenusDTO>) node.getChildren();
                if (nodeChildren != null) {
                    nodeChildren.addAll(categorySnippetMap.get(categoryId));
                } else {
                    node.setChildren(categorySnippetMap.get(categoryId));
                }
            }
        });
    }

    private List<SnippetCategory> getSnippetCategories(ArrayList<Category> categories) {
        List<Long> categoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());
        // 查询所有snippet
        return snippetCategoryService.list(new QueryWrapper<SnippetCategory>()
                .in("category_id", categoryIds));
    }
}
