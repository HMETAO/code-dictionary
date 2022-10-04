package com.hmetao.code_dictionary.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSSHForm {
    private String username;
    private String password;
    private String host;
    //端口号默认为22
    private Integer port = 22;
}
