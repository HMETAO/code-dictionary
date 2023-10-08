package com.hmetao.code_dictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.dto.RolePermissionDTO;
import com.hmetao.code_dictionary.entity.Role;
import com.hmetao.code_dictionary.form.QueryForm;
import com.hmetao.code_dictionary.form.RolePermissionForm;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
public interface RoleService extends IService<Role> {

    List<RoleDTO> getRoles();

    PageInfo<RolePermissionDTO> getRolesPage(QueryForm queryForm);

    RolePermissionDTO getRole(Long roleId);

    void insertRole(RolePermissionForm rolePermissionForm);

    void deleteRole(Long roleId);

    void updateRole(RolePermissionForm rolePermissionForm);
}
