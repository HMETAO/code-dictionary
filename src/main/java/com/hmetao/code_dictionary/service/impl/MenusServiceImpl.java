package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.constants.BaseConstants;
import com.hmetao.code_dictionary.dto.MenusDTO;
import com.hmetao.code_dictionary.entity.Menus;
import com.hmetao.code_dictionary.mapper.MenusMapper;
import com.hmetao.code_dictionary.pojo.BaseTree;
import com.hmetao.code_dictionary.service.MenusService;
import com.hmetao.code_dictionary.utils.MapUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-09
 */
@Service
public class MenusServiceImpl extends ServiceImpl<MenusMapper, Menus> implements MenusService {

    @Override
    @SuppressWarnings("unchecked")
    public List<MenusDTO> getMenus() {
        // 获取权限列表
        List<String> permissionList = StpUtil.getPermissionList();
        Wrapper<Menus> wrapper;

        // 最高权限
        if (permissionList.contains("*")) {
            wrapper = Wrappers.emptyWrapper();
        } else {
            // 进行截取权限标识操作
            List<String> permissionPath = permissionList.stream()
                    .map(permission -> permission.contains("-") ? permission.split("-")[0] : permission).collect(Collectors.toList());
            // 基础menus权限标识
            permissionPath.add(BaseConstants.BASE_PERMS);

            wrapper = new LambdaQueryWrapper<Menus>().in(Menus::getPerms, permissionPath);
        }

        List<MenusDTO> menusDTOS = baseMapper.selectList(wrapper)
                .stream()
                .distinct()
                .map(menu -> {
                    MenusDTO menusDTO = MapUtil.beanMap(menu, MenusDTO.class);
                    menusDTO.setParentId(menu.getPid());
                    return menusDTO;
                }).collect(Collectors.toList());
        return (List<MenusDTO>) BaseTree.buildTree(menusDTOS, 0L);
    }
}
