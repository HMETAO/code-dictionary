package com.hmetao.code_dictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.hmetao.code_dictionary.dto.ToolDTO;
import com.hmetao.code_dictionary.entity.Tool;
import com.hmetao.code_dictionary.entity.User;
import com.hmetao.code_dictionary.mapper.ToolMapper;
import com.hmetao.code_dictionary.service.ToolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmetao.code_dictionary.utils.MapUtils;
import com.hmetao.code_dictionary.utils.SaTokenUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HMETAO
 * @since 2022-08-26
 */
@Service
public class ToolServiceImpl extends ServiceImpl<ToolMapper, Tool> implements ToolService {


    @Override
    public List<ToolDTO> getTools(Integer pageSize, Integer pageNum) {
        User userInfo = SaTokenUtils.getLoginUserInfo();
        PageHelper.startPage(pageNum, pageSize);
        List<Tool> tools = baseMapper.selectList(new LambdaQueryWrapper<Tool>().eq(Tool::getUid, userInfo.getId()));
        return tools.stream().map(tool -> MapUtils.beanMap(tool, ToolDTO.class)).collect(Collectors.toList());
    }
}
