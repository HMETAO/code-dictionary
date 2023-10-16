package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.constants.RedisConstants;
import com.hmetao.code_dictionary.constants.TimeConstants;
import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.entity.Permission;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.form.PermissionUpdateForm;
import com.hmetao.code_dictionary.form.QueryForm;
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
    public PageInfo<PermissionDTO> getPermissions(QueryForm queryForm) throws JsonProcessingException {
        Long sysUserId = SaTokenUtil.getLoginUserId();
        try {
            StpUtil.checkPermission("permission-select");
        } catch (Exception e) {
            return new PageInfo<>(Collections.emptyList());
        }
        String redisKey = RedisConstants.PERMISSION_KEY + queryForm.getPageNum() + ":" + queryForm.getPageSize();
        String permissionJson = redisUtil.getCacheObject(redisKey);
        if (StringUtils.isEmpty(permissionJson)) {
            log.info(LOGO_INFO_KEY + "缓存内无用户： {} 的权限，将查询数据库", sysUserId);
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
            // 将权限查出
            List<Permission> permissions = baseMapper.selectList(Wrappers.emptyWrapper());
            // 转成DTO
            List<PermissionDTO> permissionDTOS = permissions.stream().map(p -> MapUtil.beanMap(p, PermissionDTO.class)).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(permissionDTOS)) {
                permissionDTOS = Collections.emptyList();
            }
            log.info(LOGO_INFO_KEY + "{} 的permission：{}", sysUserId, permissionDTOS);
            // 映射成page对象
            PageInfo<PermissionDTO> permissionPageInfo = MapUtil.PageInfoCopy(permissions, permissionDTOS);
            // 缓存到redis
            redisUtil.setCacheObject(redisKey, objectMapper.writeValueAsString(permissionPageInfo), TimeConstants.DAY_SECONDS, TimeUnit.SECONDS);
            return permissionPageInfo;
        }
        return objectMapper.readValue(permissionJson, new TypeReference<>() {
        });
    }

    @Override
    public void updatePermission(PermissionUpdateForm permissionUpdateForm) {
        log.info(LOGO_INFO_KEY + "用户 {} 更新权限： {}", SaTokenUtil.getLoginUserId(), permissionUpdateForm);
        int c = baseMapper.updateById(MapUtil.beanMap(permissionUpdateForm, Permission.class));
        if (c != 1) {
            throw new ValidationException("传入的权限信息有误，更新失败，请检查传入");
        }
        // 把有这个权限的都踢下线
        logoutHavePermissionUser(permissionUpdateForm.getId());
    }

    private void logoutHavePermissionUser(Long permissionId) {
        List<Long> userIds = baseMapper.getUserIdsByPermissionId(permissionId);
        if (!CollectionUtils.isEmpty(userIds)) {
            for (Long userId : userIds) {
                StpUtil.logout(userId);
            }
        }
    }

    @Override

    public PermissionDTO getPermission(Long permissionId) {
        Permission permission = baseMapper.selectById(permissionId);
        return MapUtil.beanMap(permission, PermissionDTO.class);
    }

    @Override
    public void deletePermission(Long permissionId) {
        log.info(LOGO_INFO_KEY + "用户 {} 删除权限： {}", SaTokenUtil.getLoginUserId(), permissionId);
        baseMapper.deleteById(permissionId);
        logoutHavePermissionUser(permissionId);
    }

    @Override
    public void insertPermission(Permission permission) {
        log.info(LOGO_INFO_KEY + "用户 {} 添加权限： {}", SaTokenUtil.getLoginUserId(), permission);
        baseMapper.insert(permission);
    }
}
