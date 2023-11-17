package com.hmetao.code_dictionary;

import com.hmetao.code_dictionary.properties.*;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.hmetao.code_dictionary.mapper")
@EnableConfigurationProperties({SparkDeskProperties.class, AliOSSProperties.class, QiNiuProperties.class, EnvProperties.class, TencentImProperties.class})
public class CodeDictionaryApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeDictionaryApplication.class, args);
    }
    /**
     * http重定向到https
     *
     */
    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    @Bean
    public Connector httpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        //Connector监听的http的默认端口号
        connector.setPort(9011);
        connector.setSecure(false);
        //监听到http的端口号后转向到的https的端口号,也就是项目配置的port
        connector.setRedirectPort(8972);
        return connector;
    }
}
