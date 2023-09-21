package com.hmetao.code_dictionary.mapper;

import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmetao.code_dictionary.po.RolePermissionPO;

import java.util.List;

/**
 * <p>
 * 角色权限 Mapper 接口
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    List<PermissionDTO> getPermissionList(List<Long> roleIds);

    List<RolePermissionPO> getPermissionByRoleIds(List<Long> roleIds);

}
