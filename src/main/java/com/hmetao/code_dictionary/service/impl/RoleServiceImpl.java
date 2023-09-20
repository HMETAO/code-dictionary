package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmetao.code_dictionary.constants.RedisConstants;
import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.entity.Role;
import com.hmetao.code_dictionary.mapper.RoleMapper;
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
            // 映射成DTO
            List<RoleDTO> roleDTOS = roles.stream().map(r -> MapUtil.beanMap(r, RoleDTO.class)).collect(Collectors.toList());

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
}
