package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.dto.SnippetDTO;
import com.hmetao.code_dictionary.dto.SnippetUploadImageDTO;
import com.hmetao.code_dictionary.entity.Snippet;
import com.hmetao.code_dictionary.entity.SnippetCategory;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.form.SnippetForm;
import com.hmetao.code_dictionary.form.SnippetUploadImageForm;
import com.hmetao.code_dictionary.mapper.SnippetMapper;
import com.hmetao.code_dictionary.properties.QiNiuProperties;
import com.hmetao.code_dictionary.service.SnippetCategoryService;
import com.hmetao.code_dictionary.service.SnippetService;
import com.hmetao.code_dictionary.utils.MapUtils;
import com.hmetao.code_dictionary.utils.QiniuUtils;
import com.hmetao.code_dictionary.utils.SaTokenUtils;
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
    private QiNiuProperties qiNiuProperties;

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

    @Override
    public SnippetUploadImageDTO uploadImage(SnippetUploadImageForm snippetUploadImageForm) {
        User user = SaTokenUtils.getLoginUserInfo();
        ArrayList<MultipartFile> files = snippetUploadImageForm.getFiles();
        LocalDate now = LocalDate.now();
        List<String> urls = new ArrayList<>();
        // 遍历每一个file
        for (MultipartFile file : files) {
            try {
                // 构造filename
                String fileName = getFileName(now, file);
                log.info("SnippetServiceImpl === > 用户：{} 上传了图片 {}", user.getId(), fileName);
                QiniuUtils.upload2qiniu(qiNiuProperties, file.getBytes(), fileName);
                // 构造图片请求URL返回
                urls.add(qiNiuProperties.getUrl() + fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new SnippetUploadImageDTO(urls);
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
