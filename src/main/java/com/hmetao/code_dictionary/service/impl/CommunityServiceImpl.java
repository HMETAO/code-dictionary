package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hmetao.code_dictionary.dto.CommunityDTO;
import com.hmetao.code_dictionary.entity.Community;
import com.hmetao.code_dictionary.entity.Snippet;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.mapper.CommunityMapper;
import com.hmetao.code_dictionary.service.CommunityService;
import com.hmetao.code_dictionary.service.SnippetService;
import com.hmetao.code_dictionary.utils.SaTokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private SnippetService snippetService;

    @Override
    public PageInfo<CommunityDTO> getCommunities(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        // 查询所有的community
        List<CommunityDTO> communities = communityMapper.getCommunities();
        return new PageInfo<>(communities);
    }

    @Override
    public void insertCommunity(Long snippetId) {
        User user = SaTokenUtil.getLoginUserInfo();
        // 安全检查判断这个snippetId是否是这个用户的
        Snippet snippet = snippetService.getOne(new LambdaQueryWrapper<Snippet>()
                .eq(Snippet::getId, snippetId));
        if (snippet == null || !Objects.equals(snippet.getUid(), user.getId())) {
            throw new ValidationException("选择的Snippet出现问题请重新选择");
        }
        // 插入数据
        Community community = new Community();
        community.setUid(user.getId());
        community.setSnippetId(snippetId);
        baseMapper.insert(community);
    }
}
