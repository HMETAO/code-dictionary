package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmetao.code_dictionary.constants.RedisConstants;
import com.hmetao.code_dictionary.constants.TimeConstants;
import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.entity.Permission;
import com.hmetao.code_dictionary.mapper.PermissionMapper;
import com.hmetao.code_dictionary.service.PermissionService;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.RedisUtil;
import com.hmetao.code_dictionary.utils.SaTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
@Service
@Slf4j
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    public static final String LOGO_INFO_KEY = "PermissionServiceImpl === > ";

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ObjectMapper objectMapper;

    @Override
//    @Cacheable(value = {"permission#86400"}, key = RedisConstants.PERMISSION_KEY + "+T(com.hmetao.code_dictionary.utils.SaTokenUtil).getLoginUserId()")
    public List<PermissionDTO> getPermissions() throws JsonProcessingException {
        Long sysUserId = SaTokenUtil.getLoginUserId();
        try {
            StpUtil.checkPermission("permission-select");
        } catch (Exception e) {
            return Collections.emptyList();
        }

        String permissionJson = redisUtil.getCacheObject(RedisConstants.PERMISSION_KEY);
        if (StringUtils.isEmpty(permissionJson)) {
            log.info(LOGO_INFO_KEY + "缓存内无用户： {} 的权限，将查询数据库", sysUserId);
            // 将权限查出
            List<Permission> permissions = baseMapper.selectList(Wrappers.emptyWrapper());
            // 转成DTO
            List<PermissionDTO> permissionDTOS = permissions.stream().map(p -> MapUtil.beanMap(p, PermissionDTO.class)).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(permissionDTOS)) {
                permissionDTOS = Collections.emptyList();
            }
            log.info(LOGO_INFO_KEY + "{} 的permission：{}", sysUserId, permissionDTOS);
            // 缓存到redis
            redisUtil.setCacheObject(RedisConstants.PERMISSION_KEY, objectMapper.writeValueAsString(permissionDTOS), TimeConstants.DAY_SECONDS, TimeUnit.SECONDS);
            return permissionDTOS;
        }
        return objectMapper.readValue(permissionJson, new TypeReference<>() {
        });
    }
}
