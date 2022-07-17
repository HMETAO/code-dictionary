package com.hmetao.code_dictionary.config;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import com.hmetao.code_dictionary.config.access.MySaRouteFunction;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("PUT", "DELETE", "GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("access-control-allow-headers",
                        "access-control-allow-methods",
                        "access-control-allow-origin",
                        "access-control-max-age",
                        "X-Frame-Options")
                .allowCredentials(true).maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 的路由拦截器，自定义认证规则
        registry.addInterceptor(new SaRouteInterceptor(new MySaRouteFunction()))
                .addPathPatterns("/**")
                .excludePathPatterns("/code_dictionary/api/v1/user/login")
                .excludePathPatterns("/swagger-ui.html")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/v2/api-docs")
                .excludePathPatterns("configuration/ui")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("configuration/security");
    }

}