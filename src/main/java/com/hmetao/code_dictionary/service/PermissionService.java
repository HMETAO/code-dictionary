package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限 服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
public interface PermissionService extends IService<Permission> {

    List<PermissionDTO> getPermission();
}
