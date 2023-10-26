package com.hmetao.code_dictionary.websocket.service.impl;

import com.hmetao.code_dictionary.constants.SparkDeskConstants;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.properties.SparkDeskProperties;
import com.hmetao.code_dictionary.websocket.service.SparkDeskService;
import com.unfbx.sparkdesk.SparkDeskClient;
import com.unfbx.sparkdesk.entity.*;
import com.unfbx.sparkdesk.listener.ChatListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SparkDeskServiceImpl implements SparkDeskService {

    public static final String LOG_INFO_KEY = "SparkDeskServiceImpl === > ";
    @Resource
    private SparkDeskClient sparkDeskClient;

    @Resource
    private SparkDeskProperties sparkDeskProperties;

    @Override
    public void chat(WebSocketSession session, TextMessage textMessage, UserDTO user) {
        AIChatRequest aiChatRequest = buildChatRequest(textMessage, user);
        StringBuilder sb = new StringBuilder();
        sparkDeskClient.chat(new ChatListener(aiChatRequest) {
            //异常回调
            @SneakyThrows
            @Override
            public void onChatError(AIChatResponse aiChatResponse) {
                close(session, aiChatResponse.getHeader().getMessage());
            }

            //输出回调
            @Override
            public void onChatOutput(AIChatResponse aiChatResponse) {
                List<Text> res = aiChatResponse.getPayload().getChoices().getText();
                res.forEach(item -> sb.append(item.getContent()));
            }

            //会话结束回调
            @Override
            @SneakyThrows
            public void onChatEnd() {
                try {
                    session.sendMessage(new TextMessage(sb.toString().getBytes()));
                } catch (IOException e) {
                    close(session, e.getMessage());
                }
            }

            //会话结束 获取token使用信息回调
            @Override
            public void onChatToken(Usage usage) {
                log.info(LOG_INFO_KEY + "用户 {} 消耗 token：{}", user.getId(), usage.getText());
            }
        });
    }

    private AIChatRequest buildChatRequest(TextMessage textMessage, UserDTO user) {
        //构建请求参数
        InHeader header = InHeader.builder().uid(String.valueOf(user.getId())).appid(sparkDeskProperties.getAppId()).build();
        // domain版本需与client生成的斑版本对应
        Parameter parameter = Parameter.builder().chat(Chat.builder().domain(SparkDeskConstants.SPARK_V2).maxTokens(2048).temperature(0.3).build()).build();
        // 放入问题
        List<Text> text = new ArrayList<>();
        text.add(Text.builder().role(Text.Role.USER.getName()).content(textMessage.getPayload()).build());
        InPayload payload = InPayload.builder().message(Message.builder().text(text).build()).build();
        return AIChatRequest.builder().header(header).parameter(parameter).payload(payload).build();
    }

    @SneakyThrows
    public static void close(WebSocketSession session, String message) {
        if (!StringUtils.isEmpty(message)) {
            log.error(LOG_INFO_KEY + "连接发生异常 {}", message);
            session.close(new CloseStatus(CloseStatus.REQUIRED_EXTENSION.getCode(), message));
        } else session.close();
    }
}
