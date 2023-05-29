package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmetao.code_dictionary.dto.FriendInformationDTO;
import com.hmetao.code_dictionary.entity.FriendInformation;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.mapper.FriendInformationMapper;
import com.hmetao.code_dictionary.service.FriendInformationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.utils.MapUtils;
import com.hmetao.code_dictionary.utils.SaTokenUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2023-05-29
 */
@Service
public class FriendInformationServiceImpl extends ServiceImpl<FriendInformationMapper, FriendInformation> implements FriendInformationService {

    @Override
    public List<FriendInformationDTO> getInformation(Long id) {
        // 获取登录用户
        User sysUser = SaTokenUtils.getLoginUserInfo();
        // 获取消息(最后30条)
        List<FriendInformation> contents = baseMapper.selectList(new LambdaQueryWrapper<FriendInformation>()
                .eq(FriendInformation::getMasterId, sysUser.getId())
                .eq(FriendInformation::getSlaveId, id)
                .orderByAsc(FriendInformation::getCreateTime)
                .last("limit 30"));
        // 映射成DTO
        return contents.stream()
                .map(content -> MapUtils.beanMap(content, FriendInformationDTO.class))
                .sorted(Comparator.comparing(FriendInformationDTO::getCreateTime))
                .collect(Collectors.toList());
    }
}
