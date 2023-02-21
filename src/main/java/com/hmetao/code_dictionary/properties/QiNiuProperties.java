package com.hmetao.code_dictionary.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "qiniu")
public class QiNiuProperties {

    private String AK;

    private String SK;

    private String BT;

    private String url;
}
