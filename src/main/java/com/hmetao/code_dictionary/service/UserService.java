package com.hmetao.code_dictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.UserDTO;
import com.hmetao.code_dictionary.dto.UserRoleDTO;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.form.*;

import java.io.IOException;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
public interface UserService extends IService<User> {

    UserDTO login(LoginForm loginForm);

    void registry(UserRegistryForm userRegistryForm);

    PageInfo<UserRoleDTO> getUsers(QueryForm queryForm);

    void deleteUserId(Long userId);

    UserRoleDTO getUser(Long userId);

    void updateUser(UserRoleUpdateForm baseUserInfoForm) throws IOException;

    void updateStatus(UserStatusForm userStatusForm);
}
