package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.BaseTreeDTO;
import com.hmetao.code_dictionary.dto.CategorySnippetMenusDTO;
import com.hmetao.code_dictionary.entity.Category;
import com.hmetao.code_dictionary.entity.SnippetCategory;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.form.CategoryForm;
import com.hmetao.code_dictionary.mapper.CategoryMapper;
import com.hmetao.code_dictionary.service.CategoryService;
import com.hmetao.code_dictionary.service.SnippetCategoryService;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.SaTokenUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 分类表 服务实现类
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
    public List<CategorySnippetMenusDTO> getCategorySnippetMenus(Boolean snippet) {
        // 获取登录用户
        User sysUser = SaTokenUtil.getLoginUserInfo();

        // 查询出该用户所有的category
        List<Category> categories = new ArrayList<>(baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, sysUser.getId())));

        // 如果没有分类生成一个默认分类
        if (CollectionUtils.isEmpty(categories) && snippet) {
            categories = generateInitialCategory(sysUser);
        }

        // 转换成DTO后续构造为树形结构
        List<CategorySnippetMenusDTO> categorySnippetMenusDTOS = categories.stream()
                .map(category -> {
                    CategorySnippetMenusDTO categorySnippetMenusDTO =
                            new CategorySnippetMenusDTO(String.valueOf(category.getId()),
                                    category.getName(),
                                    String.valueOf(category.getParentId()),
                                    false);
                    return categorySnippetMenusDTO;
                }).collect(Collectors.toList());


        // 按category分组的snippetMap
        Map<String, List<CategorySnippetMenusDTO>> categorySnippetMap = getMapOfSnippetGroupedByCategory(categories);

        // 构建树状结构
        return (List<CategorySnippetMenusDTO>) BaseTreeDTO.buildTree(categorySnippetMenusDTOS, "0", !snippet ? null : node -> {
            String categoryId = node.getId();
            if (categorySnippetMap.containsKey(categoryId)) {
                // 将snippet放入对应的category下
                List<CategorySnippetMenusDTO> nodeChildren = (List<CategorySnippetMenusDTO>) node.getChildren();
                if (nodeChildren != null) {
                    nodeChildren.addAll(categorySnippetMap.get(categoryId));
                } else {
                    node.setChildren(categorySnippetMap.get(categoryId));
                }
            }
        });
    }

    @Override
    public CategorySnippetMenusDTO insertCategory(CategoryForm categoryForm) {
        // 获取登录用户
        User sysUser = SaTokenUtil.getLoginUserInfo();
        Category category = MapUtil.beanMap(categoryForm, Category.class);
        category.setUserId(sysUser.getId());
        baseMapper.insert(category);
        return new CategorySnippetMenusDTO(String.valueOf(category.getId()),
                category.getName(),
                String.valueOf(category.getParentId())
                , false);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        // 获取用户信息
        User sysUser = SaTokenUtil.getLoginUserInfo();
        Long userId = sysUser.getId();

        // 查询category
        Category category = baseMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, userId)
                .eq(Category::getId, categoryId));

        // 系统目录禁删
        Assert.isTrue(!category.getIsSystem(), "通用分组禁止删除");

        // 查询系统目录
        Category systemCategory = baseMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getIsSystem, true)
                .eq(Category::getUserId, userId));

        // 转移分组信息
        transferCategoryInfo(categoryId, category, systemCategory);

        // 删除该分组
        baseMapper.delete(new LambdaQueryWrapper<Category>()
                .eq(Category::getUserId, userId)
                .eq(Category::getId, categoryId));


    }

    private void transferCategoryInfo(Long categoryId, Category category, Category transferCategory) {
        // 查询子目录
        List<Category> childrenCategory = baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, categoryId));

        childrenCategory.forEach(item -> item.setParentId(transferCategory.getId()));

        if (!CollectionUtils.isEmpty(childrenCategory))
            this.updateBatchById(childrenCategory);

        // 转移分组下的snippet
        List<SnippetCategory> snippetCategory = snippetCategoryService.list(new LambdaQueryWrapper<SnippetCategory>()
                .eq(SnippetCategory::getCategoryId, category.getId()));

        // todo 暂时修复 需要优化为在for外批量更新
        snippetCategory.forEach(item -> {
            item.setCategoryId(transferCategory.getId());
            snippetCategoryService.update(item, null);
        });

    }

    private Map<String, List<CategorySnippetMenusDTO>> getMapOfSnippetGroupedByCategory(List<Category> categories) {
        // 查出category下的所有snippet
        List<Long> categoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());
        List<SnippetCategory> snippets = getSnippetCategoriesByIds(categoryIds);

        // 按category分组找出每个category下的snippet(类似每个文件夹下的文件 category就是文件夹 snippet就是文件)
        return snippets.stream().map(snippet -> {
                    // 为了区分snippet与category所以加上'sn-'的前缀
                    return new CategorySnippetMenusDTO("sn-" + snippet.getSnippetId(),
                            snippet.getSnippetTitle(),
                            String.valueOf(snippet.getCategoryId()),
                            true, snippet.getType());
                })      // 按字典序排序
                .sorted(Comparator.comparing(CategorySnippetMenusDTO::getLabel))
                .collect(Collectors.groupingBy(CategorySnippetMenusDTO::getParentId));
    }

    private List<Category> generateInitialCategory(User sysUser) {
        Category category = new Category();
        category.setUserId(sysUser.getId());
        category.setName(BaseConstants.BASE_GROUP);
        category.setIsSystem(true);
        baseMapper.insert(category);
        return Collections.singletonList(category);
    }

    private List<SnippetCategory> getSnippetCategoriesByIds(List<Long> categoryIds) {
        // 查询所有snippet
        return snippetCategoryService.list(new QueryWrapper<SnippetCategory>()
                .in("category_id", categoryIds));
    }
}
