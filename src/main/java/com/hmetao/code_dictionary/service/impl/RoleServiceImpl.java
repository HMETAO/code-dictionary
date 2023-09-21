package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.constants.RedisConstants;
import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.dto.RolePermissionDTO;
import com.hmetao.code_dictionary.entity.Role;
import com.hmetao.code_dictionary.form.QueryForm;
import com.hmetao.code_dictionary.mapper.RoleMapper;
import com.hmetao.code_dictionary.mapper.RolePermissionMapper;
import com.hmetao.code_dictionary.po.RolePermissionPO;
import com.hmetao.code_dictionary.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    public static final String LOG_INFO_KEY = "RoleServiceImpl === > ";

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<RoleDTO> getRoles() throws JsonProcessingException {
        try {
            // 检查是否有查询role的权限
            StpUtil.checkPermission("role-select");
        } catch (Exception e) {
            // 返回空角色列表
            return new ArrayList<>();
        }
        // 查缓存
        String rolesJson = redisUtil.getCacheObject(RedisConstants.ROLES_KEY);
        // 缓存内无
        if (StringUtils.isEmpty(rolesJson)) {
            log.info(LOG_INFO_KEY + "roles缓存内无，将从数据库查询");
            // 查询角色列表
            List<Role> roles = baseMapper.selectList(Wrappers.emptyWrapper());
            List<RoleDTO> roleDTOS = roleMappingRoleDTOS(roles);

            if (CollectionUtils.isEmpty(roleDTOS)) roleDTOS = Collections.emptyList();

            // 转成json字符串
            rolesJson = objectMapper.writeValueAsString(roleDTOS);
            // 放入redis
            redisUtil.setCacheObject(RedisConstants.ROLES_KEY, rolesJson);
            log.info(LOG_INFO_KEY + "roles: {}", rolesJson);
            return roleDTOS;
        }
        return objectMapper.readValue(rolesJson, new TypeReference<>() {
        });
    }

    private static List<RoleDTO> roleMappingRoleDTOS(List<Role> roles) {
        // 映射成DTO
        return roles.stream().map(r -> MapUtil.beanMap(r, RoleDTO.class)).collect(Collectors.toList());
    }

    @Override
    public PageInfo<RolePermissionDTO> getRolesPage(QueryForm queryForm) {
        PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        // 查出来需要条数的role
        List<Role> roles = baseMapper.selectList(Wrappers.emptyWrapper());
        List<Long> roleIds = new ArrayList<>();
        List<RolePermissionDTO> resultDTO = roles.stream().map(r -> {
            // 提取roleId去查这个role对应的permission
            roleIds.add(r.getId());
            return MapUtil.beanMap(r, RolePermissionDTO.class);
        }).collect(Collectors.toList());
        // 查找到PO对象
        List<RolePermissionPO> rolePermissionPOS = rolePermissionMapper.getPermissionByRoleIds(roleIds);
        // 按roleId分组key是roleId val是这个role下的perms
        Map<Long, List<RolePermissionPO>> rolePermissionMap = rolePermissionPOS.stream().collect(Collectors.groupingBy(RolePermissionPO::getRoleId));
        resultDTO.forEach(rolePermissionDTO -> {
            // 为每个roleId找到自己的perms
            Long roleId = rolePermissionDTO.getId();
            if (rolePermissionMap.containsKey(roleId)) {
                // 存在perms将PO转成DTO
                rolePermissionDTO.setPerms(findAndBuildPermissionDTO(rolePermissionMap, roleId));
            }
        });
        return MapUtil.PageInfoCopy(roles, resultDTO);
    }

    private static List<PermissionDTO> findAndBuildPermissionDTO(Map<Long, List<RolePermissionPO>> rolePermissionMap, Long roleId) {
        return rolePermissionMap.get(roleId).stream().map(po -> new PermissionDTO(po.getPermissionId(), po.getName(), po.getPath())).collect(Collectors.toList());
    }
}
