package com.hmetao.code_dictionary.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.entity.User;

public class SaTokenUtils {
    public static User getLoginUserInfo() {
        return (User) StpUtil.getSession().get(BaseConstants.LOGIN_USERINFO_SESSION_KEY);
    }
}
