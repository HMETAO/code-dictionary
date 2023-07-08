package com.hmetao.code_dictionary.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.CommunityDTO;
import com.hmetao.code_dictionary.entity.Community;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HMETAO
 * @since 2023-07-04
 */
public interface CommunityService extends IService<Community> {

    PageInfo<CommunityDTO> getCommunities(Integer pageNum, Integer pageSize);

    void insertCommunity(Long snippetId);
}
