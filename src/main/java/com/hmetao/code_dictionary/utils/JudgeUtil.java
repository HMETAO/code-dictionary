package com.hmetao.code_dictionary.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.hmetao.code_dictionary.enums.CodeEnum;
import com.hmetao.code_dictionary.exception.HMETAOException;
import com.hmetao.code_dictionary.exception.PrintLogException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JudgeUtil {

    private static final Runtime runtime = Runtime.getRuntime();


    public String runCode(String code, CodeEnum codeEnum, String args, String path) {

        try {
            // 生成源代码文件返回目录file
            generateCodeSources(code, codeEnum, path);
            // 编译代码
            compileCode(codeEnum, path);
            // 运行code
            ProcessBuilder builder = CmdUtil.executeCmd(codeEnum, path);

            Process process = builder.start();
            // 写入参数
            IoUtil.write(process.getOutputStream(), true, args.getBytes());

            if (!process.waitFor(2, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new HMETAOException("JudgeUtils", "运行超时时间大于2s");
            }
            return IoUtil.read(process.getInputStream()).toString();
        } catch (HMETAOException e) {
            return e.getMessage();
        } catch (IOException | InterruptedException e) {
            throw new PrintLogException("JudgeUtils", e.getMessage());
        } finally {
            log.info("JudgeUtils === > 删除CodeSources目录：{}", path);
            FileUtil.del(path);
        }
    }

    private void compileCode(CodeEnum codeEnum, String path) throws IOException {
        String error;
        Process process = runtime.exec(CmdUtil.compileCmd(codeEnum, path));
        // 获取编译结果
        error = IoUtil.read(process.getErrorStream()).toString();
        // 编译出现错误
        if (!StringUtils.isEmpty(error)) {
            throw new HMETAOException("JudgeUtils", error);
        }
    }


    private void generateCodeSources(String code, CodeEnum codeEnum, String path) {
        FileUtil.mkdir(path);
        // 写入源码文件
        FileUtil.writeUtf8String(code, FileUtil.file(path, "Main." + codeEnum.getExt()));
    }
}
