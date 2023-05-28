package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.FriendDTO;
import com.hmetao.code_dictionary.entity.Friend;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HMETAO
 * @since 2023-05-25
 */
public interface FriendService extends IService<Friend> {

    List<FriendDTO> getFriends();

}
