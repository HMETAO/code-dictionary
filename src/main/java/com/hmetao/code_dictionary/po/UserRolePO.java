package com.hmetao.code_dictionary.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRolePO {
    private Long id;
    private Long rid;
    private String roleName;
    private String roleSign;
}
