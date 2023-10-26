package com.hmetao.code_dictionary.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spark-desk-gpt")
public class SparkDeskProperties {
    private String appId;

    private String apiSecret;

    private String apiKey;
}
