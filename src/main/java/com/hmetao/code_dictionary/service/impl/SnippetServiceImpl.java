package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.SnippetDTO;
import com.hmetao.code_dictionary.dto.SnippetUploadImageDTO;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.entity.Category;
import com.hmetao.code_dictionary.entity.Snippet;
import com.hmetao.code_dictionary.entity.SnippetCategory;
import com.hmetao.code_dictionary.form.ReceiveSnippetForm;
import com.hmetao.code_dictionary.form.RunCodeForm;
import com.hmetao.code_dictionary.form.SnippetForm;
import com.hmetao.code_dictionary.form.SnippetUploadImageForm;
import com.hmetao.code_dictionary.mapper.SnippetMapper;
import com.hmetao.code_dictionary.properties.JudgeProperties;
import com.hmetao.code_dictionary.properties.QiNiuProperties;
import com.hmetao.code_dictionary.service.CategoryService;
import com.hmetao.code_dictionary.service.SnippetCategoryService;
import com.hmetao.code_dictionary.service.SnippetService;
import com.hmetao.code_dictionary.utils.JudgeUtil;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.QiniuUtil;
import com.hmetao.code_dictionary.utils.SaTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.hmetao.code_dictionary.constants.BaseConstants.QINIU_OSS_MARKDOWN_IMAGE_UPLOAD_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Slf4j
@Service
public class SnippetServiceImpl extends ServiceImpl<SnippetMapper, Snippet> implements SnippetService {

    @Resource
    private SnippetCategoryService snippetCategoryService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private QiNiuProperties qiNiuProperties;

    @Resource
    private JudgeUtil judgeUtil;

    @Resource
    private JudgeProperties judgeProperties;

    @Override
    public SnippetDTO getSnippet(Integer id) {
        // 获取登录用户
        UserDTO sysUser = SaTokenUtil.getLoginUserInfo();
        // 获取该用户的snippet
        Snippet snippet = baseMapper.selectOne(new LambdaQueryWrapper<Snippet>()
                .eq(Snippet::getUid, sysUser.getId())
                .eq(Snippet::getId, id));
        Assert.notNull(snippet, "snippet参数错误");
        // 映射成DTO
        return MapUtil.beanMap(snippet, SnippetDTO.class);
    }

    @Override
    @Transactional
    public SnippetDTO insertSnippet(SnippetForm snippetForm) {
        // 获取登录用户
        UserDTO sysUser = SaTokenUtil.getLoginUserInfo();

        SnippetCategory exit = snippetCategoryService.getOne(new LambdaQueryWrapper<SnippetCategory>()
                .eq(SnippetCategory::getCategoryId,
                        snippetForm.getCategoryId()).eq(SnippetCategory::getSnippetTitle, snippetForm.getTitle()), true);
        if (exit != null) {
            throw new RuntimeException("在改分组下已存在此 Title 的 Snippet");
        }

        // 写入snippet
        Snippet snippet = MapUtil.beanMap(snippetForm, Snippet.class);
        snippet.setUid(sysUser.getId());
        baseMapper.insert(snippet);

        // 写入中间表
        SnippetCategory snippetCategory = MapUtil.beanMap(snippetForm, SnippetCategory.class);
        snippetCategory.setSnippetId(snippet.getId());
        snippetCategory.setSnippetTitle(snippet.getTitle());
        snippetCategoryService.save(snippetCategory);
        return MapUtil.beanMap(snippet, SnippetDTO.class);
    }

    @Override
    @Transactional
    public void deleteSnippet(Long snippetId) {
        // 获取登录用户
        UserDTO sysUser = SaTokenUtil.getLoginUserInfo();

        baseMapper.delete(new LambdaQueryWrapper<Snippet>()
                .eq(Snippet::getUid, sysUser.getId())
                .eq(Snippet::getId, snippetId));

        snippetCategoryService.remove(new LambdaQueryWrapper<SnippetCategory>()
                .eq(SnippetCategory::getSnippetId, snippetId));
    }

    @Override
    public void updateSnippet(SnippetForm snippetForm) {
        Snippet snippet = MapUtil.beanMap(snippetForm, Snippet.class);
        baseMapper.updateById(snippet);
    }

    @Override
    public SnippetUploadImageDTO uploadImage(SnippetUploadImageForm snippetUploadImageForm) {
        UserDTO user = SaTokenUtil.getLoginUserInfo();
        ArrayList<MultipartFile> files = snippetUploadImageForm.getFiles();
        LocalDate now = LocalDate.now();
        List<String> urls = new ArrayList<>();
        // 遍历每一个file
        for (MultipartFile file : files) {
            try {
                // 构造filename
                String fileName = getFileName(now, file);
                log.info("SnippetServiceImpl === > 用户：{} 上传了图片 {}", user.getId(), fileName);
                QiniuUtil.upload2qiniu(qiNiuProperties, file.getBytes(), fileName);
                // 构造图片请求URL返回
                urls.add(qiNiuProperties.getUrl() + fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new SnippetUploadImageDTO(urls);
    }

    @Override
    public void receiveSnippet(ReceiveSnippetForm receiveSnippetForm) {
        UserDTO user = SaTokenUtil.getLoginUserInfo();
        // 查询发送人snippet
        Snippet snippet = baseMapper.selectOne(new LambdaQueryWrapper<Snippet>()
                .eq(Snippet::getId, receiveSnippetForm.getSnippetId())
                .eq(Snippet::getUid, receiveSnippetForm.getUid()));

        // copy生成自己的snippet
        Snippet copySnippet = new Snippet(snippet.getTitle(),
                user.getId(),
                snippet.getSnippet());
        baseMapper.insert(copySnippet);
        // 查询通用分组ID
        Category category = categoryService.getOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, BaseConstants.BASE_GROUP)
                .eq(Category::getUserId, user.getId()));
        if (category == null)
            category = categoryService.generateInitialCategory(user.getId()).get(0);
        // 存入中间表
        SnippetCategory snippetCategory = new SnippetCategory(category.getId(),
                copySnippet.getId(),
                snippet.getTitle(),
                receiveSnippetForm.getType());
        snippetCategoryService.save(snippetCategory);
    }

    @Override
    public String runCode(RunCodeForm runCodeForm) {
        // 获取登录用户
        UserDTO user = SaTokenUtil.getLoginUserInfo();
        // 运行代码
        return judgeUtil.runCode(runCodeForm.getCode(),
                runCodeForm.getCodeEnum(), runCodeForm.getArgs(),
                judgeProperties.getPath() + "/" + user.getId());
    }

    private static String getFileName(LocalDate now, MultipartFile file) {
        // markdown/images/yyyy/MM/dd/{UUID}.jpg
        return QINIU_OSS_MARKDOWN_IMAGE_UPLOAD_PREFIX +
                now.format(DateTimeFormatter.ofPattern("/yyyy/MM/dd/")) +
                UUID.randomUUID().toString().replaceAll("-", "") +
                "." +
                Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
    }
}
