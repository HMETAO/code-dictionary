package com.hmetao.code_dictionary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.dto.RolePermissionDTO;
import com.hmetao.code_dictionary.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hmetao.code_dictionary.form.QueryForm;

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

    List<RoleDTO> getRoles() throws JsonProcessingException;

    PageInfo<RolePermissionDTO> getRolesPage(QueryForm queryForm);
}
