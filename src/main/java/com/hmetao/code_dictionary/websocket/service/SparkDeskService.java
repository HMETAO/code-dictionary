package com.hmetao.code_dictionary.websocket.service;

import com.hmetao.code_dictionary.dto.UserDTO;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public interface SparkDeskService {


    void chat(WebSocketSession webSocketSession, TextMessage textMessage, UserDTO user);
}
