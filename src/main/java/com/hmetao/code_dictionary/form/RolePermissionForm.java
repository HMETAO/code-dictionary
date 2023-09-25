package com.hmetao.code_dictionary.form;


import com.hmetao.code_dictionary.dto.RoleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionForm extends RoleDTO implements Serializable {
    List<Long> perms;
}
