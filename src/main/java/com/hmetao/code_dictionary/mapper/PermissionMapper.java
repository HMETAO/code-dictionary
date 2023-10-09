package com.hmetao.code_dictionary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmetao.code_dictionary.entity.Permission;

import java.util.List;

/**
 * <p>
 * 权限 Mapper 接口
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    List<Long> getUserIdsByPermissionId(Long id);
}
