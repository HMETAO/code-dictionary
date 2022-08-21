package com.hmetao.code_dictionary;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.hmetao.code_dictionary.mapper")
public class CodeDictionaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeDictionaryApplication.class, args);
    }
}
