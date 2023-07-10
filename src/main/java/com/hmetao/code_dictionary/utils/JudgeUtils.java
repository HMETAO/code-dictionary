package com.hmetao.code_dictionary.utils;

import cn.hutool.core.io.FileUtil;
import com.hmetao.code_dictionary.enums.CodeEnum;
import com.hmetao.code_dictionary.exception.HMETAOException;
import com.hmetao.code_dictionary.properties.JudgeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JudgeUtils {

    @Resource
    private JudgeProperties judgeProperties;

    private static final Runtime runtime = Runtime.getRuntime();

    public String runCode(String code, CodeEnum codeEnum) {
        // 生成源代码文件返回目录file
        File file = generateCodeSources(code, codeEnum);
        // 编译代码
        compileCode(codeEnum);
        ProcessBuilder builder = CmdUtils.executeCmd(codeEnum, judgeProperties.getSave());
        try {
            Process process = builder.start();
            if (!process.waitFor(500, TimeUnit.MILLISECONDS)) {
                throw new HMETAOException("运行超时", "JudgeUtils");
            }
            return new String(process.getInputStream().readAllBytes());
        } catch (Exception e) {
            throw new HMETAOException("运行失败", "JudgeUtils");
        } finally {
            log.info("JudgeUtils === > 删除CodeSources目录");
            FileUtil.del(file);
        }
    }

    private void compileCode(CodeEnum codeEnum) {
        String error;
        try {
            Process process = runtime.exec(CmdUtils.compileCmd(codeEnum, judgeProperties.getSave()));
            // 获取编译结果
            error = new String(process.getErrorStream().readAllBytes());
        } catch (IOException e) {
            throw new HMETAOException("执行编译指令失败", "JudgeUtils");
        }
        // 编译出现错误
        if (!StringUtils.isEmpty(error)) {
            throw new HMETAOException(error, "JudgeUtils");
        }

    }


    private File generateCodeSources(String code, CodeEnum codeEnum) {
        // 生成文件并返回文件地址
        File file = checkOrGenerateDirectory(judgeProperties.getSave() + "Main." + codeEnum.getExt());
        // 写入源码文件
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
            bf.write(code);
        } catch (Exception e) {
            throw new HMETAOException("生成源代码文件失败", "JudgeUtils");
        }
        // 返回文件目录
        return file.getParentFile();
    }

    private File checkOrGenerateDirectory(String path) {
        log.info("JudgeUtils === > 开始检测CodeSources目录：{}", path);
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            log.info("JudgeUtils === > 不存在CodeSources目录开始创建");
            parentFile.mkdirs();
        }
        return file;
    }


}
