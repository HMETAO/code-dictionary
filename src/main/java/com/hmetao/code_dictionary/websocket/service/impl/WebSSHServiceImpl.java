package com.hmetao.code_dictionary.websocket.service.impl;

import com.hmetao.code_dictionary.constants.SSHConstants;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.websocket.pojo.SSHConnectInfo;
import com.hmetao.code_dictionary.form.WebSSHForm;
import com.hmetao.code_dictionary.websocket.service.WebSSHService;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class WebSSHServiceImpl implements WebSSHService {

    private final ConcurrentHashMap<Long, SSHConnectInfo> sshMap = new ConcurrentHashMap<>();


    @Resource
    ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void initConnection(WebSocketSession session) {
        // 获取user信息
        User user = (User) session.getAttributes().get(SSHConstants.SSH_SESSION_KEY);
        // 缓存
        SSHConnectInfo sshConnectInfo = new SSHConnectInfo(new JSch(), user, session, null);
        sshMap.put(user.getId(), sshConnectInfo);
        threadPoolExecutor.execute(() -> {
            try {
                //连接到终端
                connectToSSH(sshConnectInfo, (WebSSHForm) session.getAttributes().get(SSHConstants.SSH_DATA_KEY), session);
            } catch (JSchException | IOException e) {
                log.error("webssh连接异常");
                log.error("异常信息:{}", e.getMessage());
                close(session, e.getMessage());
            }
        });
    }

    @Override
    public void recvHandle(String buffer, WebSocketSession session) {
        User user = (User) session.getAttributes().get(SSHConstants.SSH_SESSION_KEY);
        SSHConnectInfo sshConnectInfo = sshMap.get(user.getId());
        if (sshConnectInfo != null) {
            try {
                //发送命令到终端
                transToSSH(sshConnectInfo.getChannel(), buffer);
            } catch (IOException e) {
                log.error("webssh连接异常");
                log.error("异常信息:{}", e.getMessage());
                close(session, e.getMessage());
            }
        }
    }

    private void connectToSSH(SSHConnectInfo sshConnectInfo, WebSSHForm webSSHFrom, WebSocketSession webSocketSession) throws JSchException, IOException {
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //获取jsch的会话
        Session session = sshConnectInfo.getJsch().getSession(webSSHFrom.getUsername(), webSSHFrom.getHost(), webSSHFrom.getPort());
        session.setConfig(config);
        //设置密码
        session.setPassword(webSSHFrom.getPassword());
        //连接  超时时间30s
        session.connect(30000);

        //开启shell通道
        Channel channel = session.openChannel("shell");

        //通道连接 超时时间3s
        channel.connect(3000);

        //设置channel
        sshConnectInfo.setChannel(channel);

        //转发消息
        transToSSH(channel, "\r");

        //读取终端返回的信息流
        try (InputStream inputStream = channel.getInputStream()) {
            //循环读取
            byte[] buffer = new byte[1024];
            int i = 0;
            //如果没有数据来，线程会一直阻塞在这个地方等待数据。
            while ((i = inputStream.read(buffer)) != -1) {
                sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }

        } finally {
            //断开连接后关闭会话
            session.disconnect();
            channel.disconnect();
        }

    }

    private void transToSSH(Channel channel, String command) throws IOException {
        if (channel != null) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }

    @Override
    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
        session.sendMessage(new TextMessage(buffer));
    }

    @SneakyThrows
    @Override
    public void close(WebSocketSession session, String message) {
        if (!StringUtils.isEmpty(message))
            session.close(new CloseStatus(CloseStatus.REQUIRED_EXTENSION.getCode(), message));

        User user = (User) session.getAttributes().get(SSHConstants.SSH_SESSION_KEY);
        Long userId = user.getId();
        SSHConnectInfo sshConnectInfo = sshMap.get(userId);
        if (sshConnectInfo != null) {
            //断开连接
            if (sshConnectInfo.getChannel() != null) sshConnectInfo.getChannel().disconnect();
            //map中移除
            sshMap.remove(userId);
        }
    }
}
