package com.hmetao.code_dictionary.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "env")
public class EnvProperties {
    private String path;
}
