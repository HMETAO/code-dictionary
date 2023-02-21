package com.hmetao.code_dictionary.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * <p>
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserRegistryForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "用户名禁止为空")
    @ApiModelProperty(value = "用户名")
    private String username;

    @NotNull(message = "手机号禁止为空")
    @Pattern(message = "手机号格式错误", regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @NotNull
    @Pattern(message = "长度至少为8，至少含有一个字母和一个数字", regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    @ApiModelProperty(value = "密码")
    private String password;


    @NotNull
    @Email(message = "Email 格式错误")
    private String email;


    private MultipartFile file;
}
