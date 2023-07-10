package com.hmetao.code_dictionary.utils;

import com.hmetao.code_dictionary.enums.CodeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JudgeUtilsTest {
    @Resource
    private JudgeUtils judgeUtils;


    @Test
    public void runCodeTest() {
        System.out.println(judgeUtils.runCode("#include<stdio.h>\n" +
                "int main(){\n" +
                "\tint a,b;\n" +
                "\tscanf(\"%d %d\",&a,&b);\n" +
                "\tprintf(\"%d\",a + b);\n" +
                "\treturn 0;\n" +
                "}", CodeEnum.Cpp, "1 2"));
    }
}