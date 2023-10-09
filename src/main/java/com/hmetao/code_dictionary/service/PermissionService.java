package com.hmetao.code_dictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.entity.Permission;
import com.hmetao.code_dictionary.form.PermissionUpdateForm;
import com.hmetao.code_dictionary.form.QueryForm;

/**
 * <p>
 * 权限 服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
public interface PermissionService extends IService<Permission> {

    PageInfo<PermissionDTO> getPermissions(QueryForm queryForm) throws JsonProcessingException;

    void updatePermission(PermissionUpdateForm permissionUpdateForm);

}
