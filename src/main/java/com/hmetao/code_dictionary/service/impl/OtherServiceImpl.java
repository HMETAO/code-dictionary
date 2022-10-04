package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.hmetao.code_dictionary.constants.SSHConstants;
import com.hmetao.code_dictionary.form.WebSSHForm;
import com.hmetao.code_dictionary.service.OtherService;
import com.hmetao.code_dictionary.utils.SaTokenUtils;
import org.springframework.stereotype.Service;

@Service
public class OtherServiceImpl implements OtherService {
    @Override
    public void ssh(WebSSHForm webSSHForm) {
        StpUtil.getSession().set(SSHConstants.SSH_DATA_KEY, webSSHForm);
        StpUtil.getTokenSession().set(SSHConstants.SSH_DATA_KEY, webSSHForm);
    }
}
