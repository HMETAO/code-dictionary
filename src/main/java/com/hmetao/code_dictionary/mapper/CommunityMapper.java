package com.hmetao.code_dictionary.mapper;

import com.hmetao.code_dictionary.dto.CommunityDTO;
import com.hmetao.code_dictionary.entity.Community;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author HMETAO
 * @since 2023-07-04
 */
public interface CommunityMapper extends BaseMapper<Community> {

    List<CommunityDTO> getCommunities();
}
