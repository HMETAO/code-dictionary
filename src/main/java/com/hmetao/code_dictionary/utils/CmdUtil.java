package com.hmetao.code_dictionary.utils;

import com.hmetao.code_dictionary.enums.CodeEnum;

public class CmdUtil {

    /**
     * 获取编译脚本命令
     *
     * @param type
     * @param dir
     * @return
     */
    public static String compileCmd(CodeEnum type, String dir) {

        if (CodeEnum.Java.equals(type)) {
            if (dir != null && !"".equals(dir)) {
                return "javac " + dir + "/Main.java";
            } else {
                return "javac Main.java";
            }
        } else if (CodeEnum.Cpp.equals(type)) {
            if (dir != null && !"".equals(dir)) {
                return "g++ " + dir + "/Main.cpp -o " + dir + "/C++.out -std=c++17";
            } else {
                return "g++ Main.cpp -o C++.out -std=c++11";
            }
        } else {
            return null;
        }
    }


    /**
     * 获取运行脚本实例ProcessBuilder
     *
     * @param type
     * @param dir
     * @return
     */
    public static ProcessBuilder executeCmd(CodeEnum type, String dir) {
        ProcessBuilder builder = null;

        if (CodeEnum.Java.equals(type)) {
            if (dir != null && !"".equals(dir)) {
                builder = new ProcessBuilder("java", "-classpath", dir, "Main");
            } else {
                builder = new ProcessBuilder("java", "Main");
            }
        } else if (CodeEnum.Cpp.equals(type)) {
            if (dir != null && !"".equals(dir)) {
                builder = new ProcessBuilder(dir + "/C++.out");
            } else {
                builder = new ProcessBuilder("C++.out");
            }
        } else if (CodeEnum.Python.equals(type)) {
            if (dir != null && !"".equals(dir)) {
                builder = new ProcessBuilder("python", dir + "/Main.py");
            } else {
                builder = new ProcessBuilder("python", "Main.py");
            }
        }

        //设置自动清空流
        builder.redirectErrorStream(true);
        return builder;
    }

}
