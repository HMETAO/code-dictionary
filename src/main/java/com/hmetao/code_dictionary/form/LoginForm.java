package com.hmetao.code_dictionary.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm {

    @NotNull(message = "用户名禁止为空")
    @ApiModelProperty(value = "用户名")
    private String username;

    @NotNull
    @ApiModelProperty(value = "密码")
    private String password;
}
