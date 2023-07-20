package com.hmetao.code_dictionary.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tencent-im")
@NoArgsConstructor
@AllArgsConstructor
public class TencentImProperties {
    private Long SDKAppID;
    private String SecretKey;
}
