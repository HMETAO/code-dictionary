package com.hmetao.code_dictionary.service.impl;

import com.hmetao.code_dictionary.dto.MenusDTO;
import com.hmetao.code_dictionary.entity.Menus;
import com.hmetao.code_dictionary.mapper.MenusMapper;
import com.hmetao.code_dictionary.service.MenusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.utils.MapUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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
    public List<MenusDTO> getMenus() {
        List<Menus> menus = this.list(null);
        return build(menus);
    }

    private List<MenusDTO> build(List<Menus> menus) {
        List<MenusDTO> menusDTOs = new ArrayList<>();
        // 找到顶级父类
        menus.stream().filter(f -> f.getPid() == 0).forEach(item -> {
            // 映射为DTO对象
            MenusDTO menusDTO = MapUtil.beanMap(item, MenusDTO.class);
            // 插入菜单列表，并查询子节点
            menusDTOs.add(findChildren(menus, menusDTO));
        });
        return menusDTOs;
    }


    private MenusDTO findChildren(List<Menus> trees, MenusDTO node) {
        node.setChildren(new ArrayList<>());

        // 查询儿子节点列表
        trees.stream()
                .filter(f -> f.getPid().equals(node.getId()))
                .forEach(item -> {
                    MenusDTO menusDTO = MapUtil.beanMap(item, MenusDTO.class);
                    // 都是它儿子，递归查询并加入列表
                    node.getChildren().add(findChildren(trees, menusDTO));
                });

        return node;
    }
}
