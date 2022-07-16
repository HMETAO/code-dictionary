package com.hmetao.code_dictionary.service.impl;

import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.entity.RolePermission;
import com.hmetao.code_dictionary.mapper.RolePermissionMapper;
import com.hmetao.code_dictionary.service.RolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 角色权限 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

    @Override
    public List<PermissionDTO> getPermissionList(List<Long> roleIds) {
        return baseMapper.getPermissionList(roleIds);
    }
}
