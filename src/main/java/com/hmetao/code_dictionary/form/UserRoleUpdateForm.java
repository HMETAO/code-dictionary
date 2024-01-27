package com.hmetao.code_dictionary.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRoleUpdateForm extends BaseUserInfoForm implements Serializable {
    ArrayList<Long> roles;

    private MultipartFile file;

    @Pattern(message = "长度至少为8，至少含有一个字母和一个数字", regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    @ApiModelProperty(value = "密码")
    private String password;
}
