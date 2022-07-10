package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.MenusDTO;
import com.hmetao.code_dictionary.entity.Menus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-09
 */
public interface MenusService extends IService<Menus> {

    List<MenusDTO> getMenus();
}
