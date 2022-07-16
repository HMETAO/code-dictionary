package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.entity.RolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 角色权限 服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
public interface RolePermissionService extends IService<RolePermission> {

    /**
     * 查询权限列表
     *
     * @param roleIds 角色id
     * @return 权限列表
     */
    List<PermissionDTO> getPermissionList(List<Long> roleIds);
}
