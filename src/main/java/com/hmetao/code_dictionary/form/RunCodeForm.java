package com.hmetao.code_dictionary.form;

import com.hmetao.code_dictionary.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunCodeForm {

    private String code;

    private CodeEnum codeEnum;

    private String args;
}
