package com.hmetao.code_dictionary.po;

import lombok.Data;

@Data
public class RolePermissionPO {
    private Long roleId;

    private Long permissionId;

    private String name;

    private String path;

}
