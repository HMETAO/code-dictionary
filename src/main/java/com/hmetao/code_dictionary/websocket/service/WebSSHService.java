package com.hmetao.code_dictionary.websocket.service;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface WebSSHService {
    /**
     * 初始化ssh连接
     *
     * @param session
     */
    public void initConnection(WebSocketSession session) throws IOException;

    /**
     * 处理客户段发的数据
     *
     * @param buffer
     * @param session
     */
    public void recvHandle(String buffer, WebSocketSession session);

    /**
     * 数据写回前端 for websocket
     *
     * @param session
     * @param buffer
     * @throws IOException
     */
    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException;

    /**
     * 关闭连接
     *
     * @param session
     */
    public void close(WebSocketSession session);
}
