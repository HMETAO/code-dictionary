package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 查询角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     */
    List<RoleDTO> getRoleList(Long userId);
}
