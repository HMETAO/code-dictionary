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
public class JudgeUtil {

    @Resource
    private JudgeProperties judgeProperties;

    private static final Runtime runtime = Runtime.getRuntime();


    public String runCode(String code, CodeEnum codeEnum, String args) {
        String path = judgeProperties.getPath();
        try {
            // 生成源代码文件返回目录file
            generateCodeSources(code, codeEnum);
            // 编译代码
            compileCode(codeEnum);
            // 运行code
            ProcessBuilder builder = CmdUtil.executeCmd(codeEnum, path);

            Process process = builder.start();
            // 写入参数
            IoUtil.write(process.getOutputStream(), true, args.getBytes());

            if (!process.waitFor(1500, TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                throw new HMETAOException("运行超时", "JudgeUtils");
            }
            return IoUtil.read(process.getInputStream()).toString();
        } catch (Exception e) {
            throw new HMETAOException(e.getMessage(), "JudgeUtils");
        } finally {
            log.info("JudgeUtils === > 删除CodeSources目录");
            FileUtil.del(path);
        }
    }

    private void compileCode(CodeEnum codeEnum) {
        String path = judgeProperties.getPath();
        String error;
        try {
            Process process = runtime.exec(CmdUtil.compileCmd(codeEnum, path));
            // 获取编译结果
            error = IoUtil.read(process.getErrorStream()).toString();
        } catch (IOException e) {
            throw new HMETAOException("执行编译指令失败：" + e.getMessage(), "JudgeUtils");
        }
        // 编译出现错误
        if (!StringUtils.isEmpty(error)) {
            FileUtil.del(path);
            throw new HMETAOException(error, "JudgeUtils");
        }
    }


    private void generateCodeSources(String code, CodeEnum codeEnum) {
        // 生成文件并返回文件地址
        String path = judgeProperties.getPath();
        FileUtil.mkdir(path);
        try {
            // 写入源码文件
            FileUtil.writeUtf8String(code, FileUtil.file(path, "Main." + codeEnum.getExt()));
        } catch (Exception e) {
            throw new HMETAOException("生成源代码文件失败：" + e.getMessage(), "JudgeUtils");
        }
    }


}
