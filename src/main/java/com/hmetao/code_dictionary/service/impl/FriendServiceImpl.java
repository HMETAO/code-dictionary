package com.hmetao.code_dictionary.service.impl;

import com.hmetao.code_dictionary.dto.FriendDTO;
import com.hmetao.code_dictionary.entity.Friend;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.mapper.FriendMapper;
import com.hmetao.code_dictionary.service.FriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.utils.SaTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2023-05-25
 */
@Service
@Slf4j
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {
    @Resource
    private FriendMapper friendMapper;

    @Override
    public List<FriendDTO> getFriends() {
        // 获取登录用户
        User sysUser = SaTokenUtils.getLoginUserInfo();
        log.info("FriendServiceImpl === > 用户： {} 查询friend列表", sysUser.getId());
        // 查询列表
        return friendMapper.getFriendsByMasterId(sysUser.getId());
    }
}
