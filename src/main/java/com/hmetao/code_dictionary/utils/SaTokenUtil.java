package com.hmetao.code_dictionary.utils;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.entity.User;

public class SaTokenUtil {

    public static UserDTO getLoginUserInfo() {
        return (UserDTO) StpUtil.getSession().get(BaseConstants.LOGIN_USERINFO_SESSION_KEY);
    }

    public static UserDTO getLoginUserInfo(SaSession session) {
        return (UserDTO) session.get(BaseConstants.LOGIN_USERINFO_SESSION_KEY);
    }

    public static UserDTO getUserInfoByToken(String token) {
        return getLoginUserInfo(StpUtil.getTokenSessionByToken(token));
    }

}
