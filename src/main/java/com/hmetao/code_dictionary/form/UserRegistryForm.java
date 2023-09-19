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
public class UserRegistryForm extends BaseUserInfoForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Pattern(message = "长度至少为8，至少含有一个字母和一个数字", regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    @ApiModelProperty(value = "密码")
    private String password;

    private MultipartFile file;
}
