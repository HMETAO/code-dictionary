package com.hmetao.code_dictionary.websocket.interceptor;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.constants.SSHConstants;
import com.hmetao.code_dictionary.constants.WebSocketConstants;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
            URI uri = request.getURI();
            String query = uri.getQuery();
            if (query == null) return false;
            // 保存登录用户到websocket的session
            SaSession tokenSession = StpUtil.getTokenSessionByToken(query.replaceAll("token=", ""));
            if (tokenSession == null) return false;
            map.put(WebSocketConstants.WEBSOCKET_USERINFO_SESSION_KEY, tokenSession.get(BaseConstants.LOGIN_USERINFO_SESSION_KEY));
            if (uri.getPath().contains("ssh"))
                map.put(SSHConstants.SSH_DATA_KEY, tokenSession.get(SSHConstants.SSH_DATA_KEY));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
