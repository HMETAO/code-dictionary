package com.hmetao.code_dictionary.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
public class MySaTokenListener implements SaTokenListener {

    @Resource
    private UserService userService;


    /**
     * 每次登录时触发
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        // 更新上线时间
        updateLastLoginTime((Long) loginId);
    }

    /**
     * 每次注销时触发
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
    }

    private void updateLastLoginTime(Long loginId) {
        // 更新下线时间
        User user = new User();
        user.setId(loginId);
        user.setLastLoginTime(LocalDateTime.now());
        userService.updateById(user);
    }

    /**
     * 每次被踢下线时触发
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        // ...
    }

    /**
     * 每次被顶下线时触发
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        // ...
    }

    /**
     * 每次被封禁时触发
     */
    @Override
    public void doDisable(String loginType, Object loginId, long disableTime) {
        // ...
    }

    /**
     * 每次被解封时触发
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId) {
        // ...
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCreateSession(String id) {
        // ...
    }

    /**
     * 每次注销Session时触发
     */
    @Override
    public void doLogoutSession(String id) {
        // ...
    }
}
