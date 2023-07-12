package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.ToolDTO;
import com.hmetao.code_dictionary.entity.Tool;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.mapper.ToolMapper;
import com.hmetao.code_dictionary.properties.AliOSSProperties;
import com.hmetao.code_dictionary.service.ToolService;
import com.hmetao.code_dictionary.utils.AliOssUtil;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.SaTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-08-26
 */
@Slf4j
@Service
public class ToolServiceImpl extends ServiceImpl<ToolMapper, Tool> implements ToolService {


    @Resource
    private AliOSSProperties aliOSSProperties;


    @Override
    public PageInfo<ToolDTO> getTools(Integer pageSize, Integer pageNum) {
        User userInfo = SaTokenUtil.getLoginUserInfo();
        PageHelper.startPage(pageNum, pageSize);
        List<Tool> tools = baseMapper.selectList(new LambdaQueryWrapper<Tool>().eq(Tool::getUid, userInfo.getId()));
        return MapUtil.PageInfoCopy(tools,
                tools.stream().map(tool -> MapUtil.beanMap(tool, ToolDTO.class)).collect(Collectors.toList()));
    }

    @Override
    public void upload(List<MultipartFile> files) {
        User userInfo = SaTokenUtil.getLoginUserInfo();
        Long userId = userInfo.getId();
        HashMap<String, InputStream> uploadFileMap = new HashMap<>();
        // 构造tools对象批量插入数据库表
        List<Tool> tools = files.stream().map(file -> {
            log.info("ToolServiceImpl === > fileName：" + file.getOriginalFilename() + " fileSize: " + file.getSize());
            // 用户传过来的文件信息
            String fileNameInfo = Objects.requireNonNullElse(file.getOriginalFilename(), "NONE.warren");
            int startTypeIndex = file.getOriginalFilename().lastIndexOf(".");
            String fileName = fileNameInfo.substring(0, startTypeIndex);
            String fileType = fileNameInfo.substring(startTypeIndex + 1);

            // 生成上传到云端的文件名
            String uploadFileName = buildUploadFileName(String.valueOf(userId), fileType);
            // 构建entity对象
            Tool tool = buildToolEntity(userId, file, new String[]{fileName, fileType}, uploadFileName);
            try {
                uploadFileMap.put(uploadFileName, file.getInputStream());
            } catch (IOException e) {
                log.error("ToolServiceImpl === > " + e.getMessage(), e);
            }
            return tool;
        }).collect(Collectors.toList());

        this.saveBatch(tools);

        // 上传至OSS
        AliOssUtil.upload(uploadFileMap, aliOSSProperties);

    }

    @Override
    public void download(List<Long> ids, HttpServletResponse response) throws IOException {
        User user = SaTokenUtil.getLoginUserInfo();
        Long userId = user.getId();
        response.setContentType("application/zip;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename=" + buildDownloadName());
        // 查出该用户下需要下载的文件url列表
        List<Tool> downloadToolInfo = baseMapper.selectList(new LambdaQueryWrapper<Tool>().eq(Tool::getUid, userId)
                .in(Tool::getId, ids)
                .select(Tool::getUrl, Tool::getToolName, Tool::getToolType));

        // 映射成Map<fileName,fileURL>
        Map<String, String> toolsMap = buildToolsMap(downloadToolInfo);
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        toolsMap.forEach((fileName, downloadUrl) -> {
            try {
                zos.putNextEntry(new ZipEntry(fileName));
                // 将文件写入输出流
                zos.write(AliOssUtil.download(aliOSSProperties, downloadUrl));
            } catch (IOException e) {
                log.error("ToolServiceImpl === > " + e.getMessage(), e);
            } finally {
                try {
                    zos.closeEntry();
                } catch (IOException e) {
                    log.error("ToolServiceImpl === > " + e.getMessage(), e);
                }
            }
        });
        zos.finish();
        zos.close();

    }

    @Override
    public void deleteTool(Long toolId) {
        User userInfo = SaTokenUtil.getLoginUserInfo();
        Tool sysTool = baseMapper.selectOne(new LambdaQueryWrapper<Tool>()
                .eq(Tool::getUid, userInfo.getId())
                .eq(Tool::getId, toolId));
        if (sysTool == null) throw new ValidationException("未找到要删除的tool");
        String urlStr = sysTool.getUrl();
        AliOssUtil.delete(urlStr.substring(urlStr.indexOf(BaseConstants.ALI_OSS_TOOL_UPLOAD_PREFIX)), aliOSSProperties);
        baseMapper.deleteById(sysTool);
    }

    private Map<String, String> buildToolsMap(List<Tool> downloadToolInfo) {
        return downloadToolInfo.stream()
                .collect(Collectors.toMap(tool -> tool.getToolName() + "." + tool.getToolType(),
                        tool -> {
                            String urlStr = tool.getUrl();
                            return urlStr.substring(urlStr.indexOf(BaseConstants.ALI_OSS_TOOL_UPLOAD_PREFIX));
                        }));
    }

    private String buildDownloadName() {
        return "code-dictionary_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_")) + "tools.zip";
    }

    private Tool buildToolEntity(Long userId, MultipartFile file, String[] fileNameInfo, String fileName) {
        Tool tool = new Tool();
        tool.setToolName(fileNameInfo[0]);
        tool.setToolType(fileNameInfo[1]);
        tool.setToolSize(String.valueOf(file.getSize()));
        tool.setUid(userId);
        tool.setUrl(buildDownloadUrl(fileName));
        return tool;
    }

    private String buildDownloadUrl(String fileName) {
        // 将bucketName插入到https://后
        return aliOSSProperties.getEndpoint()
                .replaceAll("https://",
                        "https://" + aliOSSProperties.getBucketName() + ".") + "/" + fileName;
    }

    private String buildUploadFileName(String userId, String fileType) {
        // tool/2022/09/05/userId/UUID.fileType
        return BaseConstants.ALI_OSS_TOOL_UPLOAD_PREFIX + LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd/")) + userId + "/" + UUID.randomUUID()
                .toString().replaceAll("-", "") + "." + fileType;
    }
}
