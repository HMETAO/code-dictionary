package com.hmetao.code_dictionary.service.impl;

import com.hmetao.code_dictionary.service.UserRoleService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserRoleServiceImplTest {

    @Resource
    private UserRoleService userRoleService;


    @Test
    public void getRoleListTest(){
        System.out.println(userRoleService.getRoleList(1L));
    }

}