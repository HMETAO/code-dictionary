package com.hmetao.code_dictionary.websocket.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmetao.code_dictionary.constants.RedisConstants;
import com.hmetao.code_dictionary.constants.SparkDeskConstants;
import com.hmetao.code_dictionary.constants.WebSocketConstants;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.properties.SparkDeskProperties;
import com.hmetao.code_dictionary.utils.RedisUtil;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SparkDeskServiceImpl implements SparkDeskService {

    public static final String LOG_INFO_KEY = "SparkDeskServiceImpl === > ";
    @Resource
    private SparkDeskClient sparkDeskClient;

    @Resource
    private SparkDeskProperties sparkDeskProperties;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void chat(WebSocketSession session, TextMessage textMessage, UserDTO user) throws Exception {

        AIChatRequest aiChatRequest = buildChatRequest(textMessage, user, session);

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
                // 获取返回数据并合并拼接
                List<Text> res = aiChatResponse.getPayload().getChoices().getText();
                res.forEach(item -> sb.append(item.getContent()));
            }

            //会话结束回调
            @Override
            @SneakyThrows
            public void onChatEnd() {
                if (sb.length() > 0) {
                    try {
                        // 将响应信息放入redis供后续上下文反馈
                        saveCommunicationMessages(sb.toString(), Text.Role.ASSISTANT, session);
                        // 将响应信息发送回去
                        session.sendMessage(new TextMessage(sb.toString().getBytes()));
                    } catch (IOException e) {
                        close(session, e.getMessage());
                    }
                }
            }

            //会话结束 获取token使用信息回调
            @Override
            public void onChatToken(Usage usage) {
                log.info(LOG_INFO_KEY + "用户 {} 消耗 token：{}", user.getId(), usage.getText());
            }
        });
    }

    private List<Text> saveCommunicationMessages(String payload, Text.Role role, WebSocketSession session) throws Exception {
        Map<String, Object> map = session.getAttributes();
        UserDTO userDTO = (UserDTO) map.get(WebSocketConstants.WEBSOCKET_USERINFO_SESSION_KEY);
        if (userDTO == null) throw new ValidationException("未查询到登录用户");

        List<Text> texts;
        // 查询是否存在交流信息
        String sparkJsonStr = redisUtil.getCacheObject(RedisConstants.SPARK_DESK_KEY + userDTO.getId());
        // 不存在给个默认空
        if (sparkJsonStr == null) texts = new ArrayList<>();
        else texts = objectMapper.readValue(sparkJsonStr, new TypeReference<>() {
        });
        // 放入当前发送问题
        texts.add(Text.builder().role(role.getName()).content(payload).build());
        // 交流信息缓存到redis过期时间一天
        redisUtil.setCacheObject(RedisConstants.SPARK_DESK_KEY + userDTO.getId(), objectMapper.writeValueAsString(texts), 1, TimeUnit.DAYS);
        return texts;
    }

    private AIChatRequest buildChatRequest(TextMessage textMessage, UserDTO user, WebSocketSession session) throws Exception {
        //构建请求参数
        InHeader header = InHeader.builder().uid(String.valueOf(user.getId())).appid(sparkDeskProperties.getAppId()).build();
        // domain版本需与client生成的斑版本对应
        Parameter parameter = Parameter.builder().chat(Chat.builder().domain(SparkDeskConstants.SPARK_V3).maxTokens(2048).temperature(0.3).build()).build();
        List<Text> texts = saveCommunicationMessages(textMessage.getPayload(), Text.Role.USER, session);
        // 放入问题
        InPayload payload = InPayload.builder().message(Message.builder().text(texts).build()).build();
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
