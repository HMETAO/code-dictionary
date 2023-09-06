package com.hmetao.code_dictionary.mapper;

import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.dto.UserInfoDTO;
import com.hmetao.code_dictionary.dto.UserRoleDTO;
import com.hmetao.code_dictionary.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmetao.code_dictionary.po.UserRolePO;
import org.apache.ibatis.annotations.MapKey;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    List<RoleDTO> getRoleList(Long userId);

    List<UserRolePO> getRolesByUserIds(List<Long> userIds);
}
