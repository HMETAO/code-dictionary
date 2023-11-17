package com.hmetao.code_dictionary.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.CategorySnippetMenusDTO;
import com.hmetao.code_dictionary.dto.SnippetDTO;
import com.hmetao.code_dictionary.dto.SnippetUploadImageDTO;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.entity.Category;
import com.hmetao.code_dictionary.entity.Snippet;
import com.hmetao.code_dictionary.entity.SnippetCategory;
import com.hmetao.code_dictionary.form.*;
import com.hmetao.code_dictionary.mapper.SnippetMapper;
import com.hmetao.code_dictionary.properties.EnvProperties;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static final String LOG_INFO_KEY = "SnippetServiceImpl === > ";

    @Resource
    private SnippetCategoryService snippetCategoryService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private QiNiuProperties qiNiuProperties;

    @Resource
    private JudgeUtil judgeUtil;

    @Resource
    private EnvProperties evnProperties;

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
                evnProperties.getPath() + "/judge" + user.getId());
    }

    @Override
    public void download(SnippetDownloadForm snippetDownloadForm, HttpServletResponse response) throws IOException {
        Long userId = SaTokenUtil.getLoginUserId();
        List<String> ids = snippetDownloadForm.getIds();
        // 过滤掉非snippet
        ids = ids.stream().filter(id -> id.startsWith("sn-")).collect(Collectors.toList());

        // 查询出snippet
        Set<String> snippetIdsSet = new HashSet<>(ids);

        List<CategorySnippetMenusDTO> categorySnippetMenus = categoryService.getCategorySnippetMenus(true);
        if (CollectionUtils.isEmpty(categorySnippetMenus)) throw new RuntimeException("非正常返回category菜单");

        // 给整个树加个根方便操作
        CategorySnippetMenusDTO root = new CategorySnippetMenusDTO();
        root.setLabel("CodeDictionary");
        root.setSnippet(false);
        root.setChildren(categorySnippetMenus);

        // 剪掉不需要下载的分支
        dfs(root, snippetIdsSet);

        String prefix = FileUtil.getTmpDir() + "/code-dictionary_" + userId;
        // 查询要下载的snippet信息
        List<Long> snippetIds = ids.stream().map(id -> Long.parseLong(id.replaceFirst("sn-", ""))).collect(Collectors.toList());
        log.info(LOG_INFO_KEY + "用户 {} 带出snippet {}", userId, snippetIds);
        List<Snippet> needDownloadSnippet = Collections.emptyList();
        // 非空就查询需要下载的snippet信息
        if (!CollectionUtils.isEmpty(snippetIds))
            needDownloadSnippet = baseMapper.selectList(Wrappers.lambdaQuery(Snippet.class).eq(Snippet::getUid, userId).in(Snippet::getId, snippetIds));

        HashMap<String, String> map = new HashMap<>();
        // 把snippet内容放入map供后续生成文件
        for (Snippet snippet : needDownloadSnippet) {
            map.put("sn-" + snippet.getId(), snippet.getSnippet());
        }
        response.setContentType("application/zip;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename=CodeDictionary.zip");
        // 使用BFS生成文件并压缩到流中
        generateSnippetFileAndCompressItToStream(map, root, response.getOutputStream(), prefix);
        // 删除临时文件
        FileUtil.del(prefix);
    }

    @SuppressWarnings("unchecked")
    private void generateSnippetFileAndCompressItToStream(HashMap<String, String> map, CategorySnippetMenusDTO root, ServletOutputStream out, String prefix) throws IOException {
        class SnippetFilePath {
            final CategorySnippetMenusDTO node;

            final String path;

            public SnippetFilePath(CategorySnippetMenusDTO node, String path) {
                this.node = node;
                this.path = path;
            }
        }
        // 后续的路径拼接都是 pre + /
        prefix += "/";
        Deque<SnippetFilePath> que = new LinkedList<>();
        que.offer(new SnippetFilePath(root, ""));
        StringBuilder sb = new StringBuilder();
        ZipOutputStream zos = new ZipOutputStream(out);
        // BFS
        while (!que.isEmpty()) {
            SnippetFilePath poll = que.poll();
            CategorySnippetMenusDTO node = poll.node;
            // 清空sb
            sb.setLength(0);
            // 拼接当前目录
            sb.append(poll.path).append(node.getLabel());
            if (node.getSnippet()) { // 是snippet就把路径拼接后存在map
                sb.append(".").append(node.getType() == 0 ? "cd" : "md"); // 添加后缀
                zos.putNextEntry(new ZipEntry(sb.toString())); // 压缩的是文件
                // 创建出这个文件
                File file = FileUtil.appendString(map.get(node.getId()), prefix + sb, StandardCharsets.UTF_8);
                // 将这个文件压缩写到流里面
                BufferedInputStream is = FileUtil.getInputStream(file);
                // 将文件写入压缩包内
                zos.write(is.readAllBytes());
                is.close();
            } else {
                sb.append("/");
                // 往压缩包内加文件夹
                zos.putNextEntry(new ZipEntry(sb.toString()));
                // category就把子节点全部丢队列
                for (CategorySnippetMenusDTO next : (List<CategorySnippetMenusDTO>) node.getChildren()) {
                    que.offer(new SnippetFilePath(next, sb.toString()));
                }
            }
            zos.closeEntry();
        }
        zos.finish();
        zos.close();
    }

    @SuppressWarnings("unchecked")
    private boolean dfs(CategorySnippetMenusDTO node, Set<String> set) {
        ListIterator<CategorySnippetMenusDTO> children = (ListIterator<CategorySnippetMenusDTO>) node.getChildren().listIterator();
        boolean ans = false;

        while (children.hasNext()) {
            CategorySnippetMenusDTO next = children.next();
            boolean current = false;
            // 判断是否是snippet，不存在的snippet需要删除
            if (next.getSnippet()) current |= set.contains(next.getId());
            else current |= dfs(next, set); // 如果是category判断子分支是否需要存在
            if (!current) children.remove(); // 如果不需要存在删除
            ans |= current;
        }
        return ans;
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
