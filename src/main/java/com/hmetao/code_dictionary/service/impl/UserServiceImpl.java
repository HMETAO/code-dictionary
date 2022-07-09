package com.hmetao.code_dictionary.service.impl;

import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.mapper.UserMapper;
import com.hmetao.code_dictionary.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
