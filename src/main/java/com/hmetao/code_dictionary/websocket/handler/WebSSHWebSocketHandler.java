package com.hmetao.code_dictionary.websocket.handler;

import com.hmetao.code_dictionary.constants.SSHConstants;
import com.hmetao.code_dictionary.websocket.service.WebSSHService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;

@Slf4j
@Component
public class WebSSHWebSocketHandler implements WebSocketHandler {

    @Resource
    private WebSSHService webSSHService;

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        log.info("用户:{},连接WebSSH", webSocketSession.getAttributes().get(SSHConstants.SSH_SESSION_KEY));
        //调用初始化连接
        webSSHService.initConnection(webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage instanceof TextMessage) {
            log.info("用户:{},发送命令:{}", webSocketSession.getAttributes().get(SSHConstants.SSH_SESSION_KEY), webSocketMessage);
            //调用service接收消息
            webSSHService.recvHandle(((TextMessage) webSocketMessage).getPayload(), webSocketSession);
        } else if (webSocketMessage instanceof BinaryMessage) {

        } else if (webSocketMessage instanceof PongMessage) {

        } else {
            System.out.println("Unexpected WebSocket message type: " + webSocketMessage);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        log.error("数据传输错误", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        log.info("用户:{}断开webssh连接", webSocketSession.getAttributes().get(SSHConstants.SSH_SESSION_KEY));
        //调用service关闭连接
        webSSHService.close(webSocketSession, null);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
