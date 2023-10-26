package com.hmetao.code_dictionary.websocket.handler;

import com.hmetao.code_dictionary.constants.WebSocketConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class SparkDeskHandler implements WebSocketHandler {

    public static final String LOG_INFO_KEY = "SparkDeskHandler === > ";

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        log.info(LOG_INFO_KEY + "用户:{},连接SparkDesk", webSocketSession.getAttributes().get(WebSocketConstants.WEBSOCKET_USERINFO_SESSION_KEY));
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
