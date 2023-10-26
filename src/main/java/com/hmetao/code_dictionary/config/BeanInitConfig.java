package com.hmetao.code_dictionary.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hmetao.code_dictionary.properties.SparkDeskProperties;
import com.unfbx.sparkdesk.SparkDeskClient;
import com.unfbx.sparkdesk.constant.SparkDesk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Configuration
public class BeanInitConfig {


    @Resource
    private SparkDeskProperties sparkDeskProperties;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SparkDeskClient sparkDeskClient() {
        return SparkDeskClient.builder()
                .host(SparkDesk.SPARK_API_HOST_WS_V2_1)
                .appid(sparkDeskProperties.getAppId())
                .apiKey(sparkDeskProperties.getApiKey())
                .apiSecret(sparkDeskProperties.getApiSecret())
                .build();
    }
}
