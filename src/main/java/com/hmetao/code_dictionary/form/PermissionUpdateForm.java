package com.hmetao.code_dictionary.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionUpdateForm {
    private Long id;

    private String name;

    @NotNull
    @Pattern(message = "请按规则编写权限标识", regexp = ".*-?(\\*|select|update|delete|insert)")
    private String path;

}
