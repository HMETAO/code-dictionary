package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.FriendInformationDTO;
import com.hmetao.code_dictionary.entity.FriendInformation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author HMETAO
 * @since 2023-05-29
 */
public interface FriendInformationService extends IService<FriendInformation> {

    List<FriendInformationDTO> getInformation(Long id);
}
