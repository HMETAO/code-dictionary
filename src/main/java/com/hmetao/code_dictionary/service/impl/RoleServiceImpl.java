package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.entity.Role;
import com.hmetao.code_dictionary.mapper.RoleMapper;
import com.hmetao.code_dictionary.mapper.UserRoleMapper;
import com.hmetao.code_dictionary.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.service.UserRoleService;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.SaTokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Override
    public List<RoleDTO> getRoles() {
        try {
            // 检查是否有查询role的权限
            StpUtil.checkPermission("role-select");
        } catch (Exception e) {
            // 返回空角色列表
            return new ArrayList<>();
        }
        List<Role> roles = baseMapper.selectList(Wrappers.emptyWrapper());
        return roles.stream().map(r -> MapUtil.beanMap(r, RoleDTO.class)).collect(Collectors.toList());
    }
}
