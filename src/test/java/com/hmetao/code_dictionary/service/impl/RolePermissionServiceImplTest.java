package com.hmetao.code_dictionary.service.impl;

import com.hmetao.code_dictionary.dto.PermissionDTO;
import com.hmetao.code_dictionary.service.RolePermissionService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class RolePermissionServiceImplTest {
    @Resource
    private RolePermissionService rolePermissionService;

    @Test
    public void getPermissionListTest(){
        List<PermissionDTO> permissionList = rolePermissionService.getPermissionList(Collections.singletonList(1L));
        System.out.println(permissionList);
    }

}