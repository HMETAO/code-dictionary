package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.CommunityDTO;
import com.hmetao.code_dictionary.entity.Community;
import com.hmetao.code_dictionary.mapper.CommunityMapper;
import com.hmetao.code_dictionary.service.CommunityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2023-07-04
 */
@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, Community> implements CommunityService {


    @Resource
    private CommunityMapper communityMapper;

    @Override
    public PageInfo<CommunityDTO> getCommunities(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 查询所有的community
        List<CommunityDTO> communities = communityMapper.getCommunities();
        return new PageInfo<>(communities);
    }
}
