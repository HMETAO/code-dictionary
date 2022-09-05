package com.hmetao.code_dictionary;

import com.hmetao.code_dictionary.properties.AliOSSProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.hmetao.code_dictionary.mapper")
@EnableConfigurationProperties(AliOSSProperties.class)
public class CodeDictionaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeDictionaryApplication.class, args);
    }
}
