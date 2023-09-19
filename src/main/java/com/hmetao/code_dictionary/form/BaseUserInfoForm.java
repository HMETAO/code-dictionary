package com.hmetao.code_dictionary.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseUserInfoForm implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(message = "用户名禁止为空")
    @ApiModelProperty(value = "用户名")
    private String username;

    @NotNull(message = "手机号禁止为空")
    @Pattern(message = "手机号格式错误", regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @NotNull
    @Email(message = "Email 格式错误")
    private String email;

}
