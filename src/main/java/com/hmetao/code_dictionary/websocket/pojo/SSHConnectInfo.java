package com.hmetao.code_dictionary.websocket.pojo;

import com.hmetao.code_dictionary.entity.User;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SSHConnectInfo {
    private JSch jsch;

    private User user;

    private WebSocketSession session;
    private Channel channel;
}
