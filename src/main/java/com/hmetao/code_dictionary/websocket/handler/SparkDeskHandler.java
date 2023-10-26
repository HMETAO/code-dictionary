package com.hmetao.code_dictionary.websocket.handler;

import com.hmetao.code_dictionary.constants.WebSocketConstants;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.websocket.service.SparkDeskService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;

@Slf4j
@Component
public class SparkDeskHandler implements WebSocketHandler {

    public static final String LOG_INFO_KEY = "SparkDeskHandler === > ";

    @Resource
    private SparkDeskService sparkDeskService;

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        log.info(LOG_INFO_KEY + "用户:{},连接SparkDesk", webSocketSession.getAttributes().get(WebSocketConstants.WEBSOCKET_USERINFO_SESSION_KEY));
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, @NotNull WebSocketMessage<?> webSocketMessage) {
        TextMessage textMessage = (TextMessage) webSocketMessage;
        UserDTO user = (UserDTO) webSocketSession.getAttributes().get(WebSocketConstants.WEBSOCKET_USERINFO_SESSION_KEY);
        sparkDeskService.chat(webSocketSession, textMessage, user);
        log.info(LOG_INFO_KEY + "用户:{},发送消息至GPT{}", user.getUsername(), textMessage.getPayload());
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession webSocketSession, @NotNull Throwable throwable) {
        log.error(LOG_INFO_KEY + "数据传输错误", throwable);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession webSocketSession, @NotNull CloseStatus closeStatus) {
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
