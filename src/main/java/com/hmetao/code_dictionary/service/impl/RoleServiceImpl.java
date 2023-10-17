package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.hmetao.code_dictionary.dto.RoleDTO;
import com.hmetao.code_dictionary.dto.RolePermissionDTO;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.entity.Role;
import com.hmetao.code_dictionary.entity.RolePermission;
import com.hmetao.code_dictionary.entity.UserRole;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.form.QueryForm;
import com.hmetao.code_dictionary.form.RolePermissionForm;
import com.hmetao.code_dictionary.form.RoleStatusForm;
import com.hmetao.code_dictionary.mapper.RoleMapper;
import com.hmetao.code_dictionary.mapper.RolePermissionMapper;
import com.hmetao.code_dictionary.mapper.UserRoleMapper;
import com.hmetao.code_dictionary.po.RolePermissionPO;
import com.hmetao.code_dictionary.service.RolePermissionService;
import com.hmetao.code_dictionary.service.RoleService;
import com.hmetao.code_dictionary.utils.MapUtil;
import com.hmetao.code_dictionary.utils.RedisUtil;
import com.hmetao.code_dictionary.utils.SaTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private RolePermissionMapper rolePermissionMapper;

    @Resource
    private RolePermissionService rolePermissionService;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ObjectMapper objectMapper;

    @Override
//    @Cacheable(value = {"role#86400"}, key = RedisConstants.ROLES_KEY + "+T(com.hmetao.code_dictionary.utils.SaTokenUtil).getLoginUserId()")
    public List<RoleDTO> getRoles() throws JsonProcessingException {
        Long sysUserId = SaTokenUtil.getLoginUserId();
        try {
            // 检查是否有查询role的权限
            StpUtil.checkPermission("role-select");
        } catch (Exception e) {
            // 返回空角色列表
            return new ArrayList<>();
        }
        // 查缓存
        String roleJson = redisUtil.getCacheObject(RedisConstants.ROLES_KEY);
        if (StringUtils.isEmpty(roleJson)) {
            log.info(LOG_INFO_KEY + "用户：{} 的roles缓存内无，将从数据库查询", sysUserId);
            // 查询所有角色
            List<Role> roles = baseMapper.selectList(Wrappers.emptyWrapper());
            List<RoleDTO> roleDTOS = roleMappingRoleDTOS(roles);
            if (CollectionUtils.isEmpty(roleDTOS)) roleDTOS = Collections.emptyList();
            log.info(LOG_INFO_KEY + "{} 的roles: {}", sysUserId, roleDTOS);
            // 放入redis
            redisUtil.setCacheObject(RedisConstants.ROLES_KEY, objectMapper.writeValueAsString(roleDTOS), TimeConstants.DAY_SECONDS, TimeUnit.SECONDS);
            return roleDTOS;
        }
        return objectMapper.readValue(roleJson, new TypeReference<>() {
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

    @Override
    public RolePermissionDTO getRole(Long roleId) {
        // 查找角色
        Role role = baseMapper.selectById(roleId);
        if (role == null) throw new ValidationException("未查找到角色");
        RolePermissionDTO rolePermissionDTO = MapUtil.beanMap(role, RolePermissionDTO.class);
        // 获取角色对应的permission
        rolePermissionDTO.setPerms(rolePermissionMapper.getPermissionList(Collections.singletonList(roleId)));
        return rolePermissionDTO;
    }

    @Override
    @Transactional
    public void insertRole(RolePermissionForm rolePermissionForm) {
        // 判断是否出现重复
        Long sysCount = baseMapper.selectCount(new LambdaQueryWrapper<Role>().eq(Role::getRoleName, rolePermissionForm.getRoleName()).or().eq(Role::getRoleSign, rolePermissionForm.getRoleSign()));
        // 发生重复
        if (sysCount > 0) throw new ValidationException("角色名或角色标识发生重复");

        Long sysUserId = SaTokenUtil.getLoginUserId();
        Role role = MapUtil.beanMap(rolePermissionForm, Role.class);
        // 先插入role
        baseMapper.insert(role);
        List<Long> perms = rolePermissionForm.getPerms();
        // 创建中间表对象
        List<RolePermission> rolePermissions = perms.stream().map(p -> new RolePermission(role.getId(), p, sysUserId)).collect(Collectors.toList());
        // 批量存入中间表
        rolePermissionService.saveBatch(rolePermissions);
        log.info(LOG_INFO_KEY + "{} 添加新角色： {} 附带权限：{}", sysUserId, role, rolePermissions);
        // 删除缓存
        redisUtil.deleteObject(RedisConstants.ROLES_KEY);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        UserDTO userInfo = SaTokenUtil.getLoginUserInfo();
        Role sysRole = baseMapper.selectById(roleId);
        if (sysRole == null) throw new ValidationException("未找到需要删除role");
        if (Objects.equals(sysRole.getRoleSign(), "admin")) throw new ValidationException("管理员角色拒绝删除");
        log.info(LOG_INFO_KEY + "用户： {} 删除角色： {}", userInfo.getUsername(), sysRole.getRoleName());
        // 删除角色
        baseMapper.deleteById(roleId);
        // 删除角色的权限
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        // 查询有该角色的用户然后踢下线
        List<UserRole> userRoles = userRoleMapper.selectList(Wrappers.lambdaQuery(UserRole.class).eq(UserRole::getRoleId, roleId));
        userRoles.forEach(userRole -> {
            // 踢下线
            StpUtil.logout(userRole.getUserId());
        });
        // 删除缓存
        redisUtil.deleteObject(RedisConstants.ROLES_KEY);
    }

    @Override
    public void updateRole(RolePermissionForm rolePermissionForm) {
        List<Long> perms = rolePermissionForm.getPerms();
        // 查询系统内的perms
        List<RolePermissionPO> sysPerms = rolePermissionMapper.getPermissionByRoleIds(Collections.singletonList(rolePermissionForm.getId()));
        boolean check = perms.size() != sysPerms.size();
        // 删除缓存
        redisUtil.deleteObject(RedisConstants.ROLES_KEY);
        if (!check) {
            // 判断是否发生改变
            Collections.sort(perms);
            sysPerms.sort(Comparator.comparingLong(RolePermissionPO::getPermissionId));
            for (int i = 0; i < perms.size(); i++) {
                if (!Objects.equals(sysPerms.get(i).getPermissionId(), perms.get(i))) {
                    check = true;
                    break;
                }
            }
        }
        if (check) {
            // 判断是否存在更新角色权限
            StpUtil.checkPermission("permission-update");
            // 覆盖角色权限
            coverRolePermission(rolePermissionForm, perms);
        }
    }

    @Override
    public void updateStatus(RoleStatusForm roleStatusForm) {
        log.info(LOG_INFO_KEY + "用户： {} 更新角色： {}", SaTokenUtil.getLoginUserId(), roleStatusForm.getId());
        baseMapper.updateById(MapUtil.beanMap(roleStatusForm, Role.class));
        // 删除缓存
        redisUtil.deleteObject(RedisConstants.ROLES_KEY);
    }

    private void coverRolePermission(RolePermissionForm rolePermissionForm, List<Long> perms) {
        log.info(LOG_INFO_KEY + "用户： {} 赋予 {} 权限：{}", SaTokenUtil.getLoginUserId(), rolePermissionForm.getId(), perms);
        // 删除中间表内容
        rolePermissionMapper.delete(Wrappers.lambdaQuery(RolePermission.class)
                .eq(RolePermission::getRoleId, rolePermissionForm.getId()));
        // 创建中间表对象
        rolePermissionService.saveBatch(perms.stream()
                .map(p -> new RolePermission(rolePermissionForm.getId(), p, SaTokenUtil.getLoginUserId())).collect(Collectors.toList()));
    }

    private static List<PermissionDTO> findAndBuildPermissionDTO(Map<Long, List<RolePermissionPO>> rolePermissionMap, Long roleId) {
        return rolePermissionMap.get(roleId).stream().map(po -> new PermissionDTO(po.getPermissionId(), po.getName(), po.getPath())).collect(Collectors.toList());
    }
}
