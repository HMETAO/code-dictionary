package com.hmetao.code_dictionary.config.access;

import cn.dev33.satoken.stp.StpInterface;
import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.service.RolePermissionService;
import com.hmetao.code_dictionary.service.UserRoleService;
import com.hmetao.code_dictionary.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private UserRoleService userRoleService;

    @Resource
    private RolePermissionService rolePermissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<Long> roleList = userRoleService.getRoleList(Long.valueOf(loginId.toString()))
                .stream()
                .map(RoleDTO::getId)
                .collect(Collectors.toList());

        return rolePermissionService.getPermissionList(roleList)
                .stream()
                .map(PermissionDTO::getPath)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return userRoleService.getRoleList(Long.valueOf(loginId.toString()))
                .stream()
                .map(RoleDTO::getRoleSign)
                .collect(Collectors.toList());
    }
}
