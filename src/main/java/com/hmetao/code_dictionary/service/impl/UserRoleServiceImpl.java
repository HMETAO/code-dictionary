package com.hmetao.code_dictionary.service.impl;

import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.entity.UserRole;
import com.hmetao.code_dictionary.mapper.UserRoleMapper;
import com.hmetao.code_dictionary.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public List<RoleDTO> getRoleList(Long loginId) {
        return baseMapper.getRoleList(loginId);
    }
}
