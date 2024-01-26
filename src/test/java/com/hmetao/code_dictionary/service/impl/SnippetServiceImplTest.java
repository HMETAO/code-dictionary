package com.hmetao.code_dictionary.service.impl;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SnippetServiceImplTest {

    @Test
    public void upload() throws IOException {
        FileInputStream file = new FileInputStream("C:\\Users\\Lenovo\\Desktop\\csv.zip");
        ZipInputStream zis = new ZipInputStream(file);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            // 如果是文件夹递归处理
            if (entry.isDirectory()) {

                System.out.println("文件夹：==》" + entry.getName());
            } else {
//                System.out.println(new String(zis.readAllBytes()));
                System.out.println(entry.getName());
            }
        }


    }

}