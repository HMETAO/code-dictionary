package com.hmetao.code_dictionary.mapper;

import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    List<RoleDTO> getRoleList(Long userId);
}
