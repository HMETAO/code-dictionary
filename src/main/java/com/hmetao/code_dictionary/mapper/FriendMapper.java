package com.hmetao.code_dictionary.mapper;

import com.hmetao.code_dictionary.dto.FriendDTO;
import com.hmetao.code_dictionary.entity.Friend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author HMETAO
 * @since 2023-05-25
 */
public interface FriendMapper extends BaseMapper<Friend> {

    List<FriendDTO> getFriendsByMasterId(Long masterId);
}
