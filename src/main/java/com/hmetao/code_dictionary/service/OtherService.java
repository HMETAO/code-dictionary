package com.hmetao.code_dictionary.service;


import com.hmetao.code_dictionary.dto.CalendarDTO;
import com.hmetao.code_dictionary.form.WebSSHForm;

import java.util.List;

public interface OtherService {
    void ssh(WebSSHForm webSSHForm);


    List<CalendarDTO> calendar();
}
