package com.hmetao.code_dictionary.config;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import com.hmetao.code_dictionary.config.access.MySaRouteFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        //初始化cors配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.setAllowCredentials(true);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        //初始化cors配置源对象
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", configuration);
        //返回corsFilter实例，参数cors配置源对象
        return new CorsFilter(corsConfigurationSource);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册注解拦截器，并排除不需要注解鉴权的接口地址
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");
        // 注册 Sa-Token 的路由拦截器，自定义认证规则
        registry.addInterceptor(new SaRouteInterceptor(new MySaRouteFunction()))
                .addPathPatterns("/**")
                .excludePathPatterns("/code_dictionary/api/v1/user/login")
                .excludePathPatterns("/code_dictionary/api/v1/user/registry")
                .excludePathPatterns("/swagger-ui.html")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/v2/api-docs")
                .excludePathPatterns("configuration/ui")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("configuration/security");
    }

}