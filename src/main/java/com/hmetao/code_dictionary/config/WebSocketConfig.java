package com.hmetao.code_dictionary.config;

import com.hmetao.code_dictionary.websocket.handler.SparkDeskHandler;
import com.hmetao.code_dictionary.websocket.handler.WebSSHWebSocketHandler;
import com.hmetao.code_dictionary.websocket.interceptor.WebSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private WebSSHWebSocketHandler webSSHWebSocketHandler;

    @Resource
    private SparkDeskHandler sparkDeskHandler;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //socket通道
        //指定处理器和路径，并设置跨域
        webSocketHandlerRegistry.addHandler(webSSHWebSocketHandler, "/api/v1/ssh")
                .addHandler(sparkDeskHandler, "/api/v1/gpt")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*");
    }
}
