package com.hmetao.code_dictionary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmetao.code_dictionary.dto.UserInfoDTO;
import com.hmetao.code_dictionary.entity.User;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
public interface UserMapper extends BaseMapper<User> {

    List<UserInfoDTO> getUserInfoList();

    User getUserByUsernameOrMobile(String username, String mobile);
}
