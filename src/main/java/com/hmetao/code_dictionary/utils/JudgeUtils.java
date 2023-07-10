package com.hmetao.code_dictionary.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.hmetao.code_dictionary.enums.CodeEnum;
import com.hmetao.code_dictionary.exception.HMETAOException;
import com.hmetao.code_dictionary.properties.JudgeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JudgeUtils {

    @Resource
    private JudgeProperties judgeProperties;

    private static final Runtime runtime = Runtime.getRuntime();

    public String runCode(String code, CodeEnum codeEnum) {
        String path = judgeProperties.getSave();
        // 生成源代码文件返回目录file
        generateCodeSources(code, codeEnum);
        // 编译代码
        compileCode(codeEnum);
        ProcessBuilder builder = CmdUtils.executeCmd(codeEnum, path);
        try {
            Process process = builder.start();
            if (!process.waitFor(500, TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                throw new HMETAOException("运行超时", "JudgeUtils");
            }
            return IoUtil.read(process.getInputStream()).toString();
        } catch (Exception e) {
            throw new HMETAOException("运行失败", "JudgeUtils");
        } finally {
            log.info("JudgeUtils === > 删除CodeSources目录");
            FileUtil.del(path);
        }
    }

    private void compileCode(CodeEnum codeEnum) {
        String path = judgeProperties.getSave();
        String error;
        try {
            Process process = runtime.exec(CmdUtils.compileCmd(codeEnum, path));
            // 获取编译结果
            error = IoUtil.read(process.getErrorStream()).toString();
        } catch (IOException e) {
            throw new HMETAOException("执行编译指令失败", "JudgeUtils");
        }
        // 编译出现错误
        if (!StringUtils.isEmpty(error)) {
            FileUtil.del(path);
            throw new HMETAOException(error, "JudgeUtils");
        }

    }


    private void generateCodeSources(String code, CodeEnum codeEnum) {
        // 生成文件并返回文件地址
        String path = judgeProperties.getSave();
        FileUtil.mkdir(path);
        try {
            // 写入源码文件
            FileUtil.writeUtf8String(code, FileUtil.file(path, "Main." + codeEnum.getExt()));
        } catch (Exception e) {
            throw new HMETAOException("生成源代码文件失败", "JudgeUtils");
        }
    }


}
