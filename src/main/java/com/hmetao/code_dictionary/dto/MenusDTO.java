package com.hmetao.code_dictionary.dto;

import com.hmetao.code_dictionary.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MenusDTO extends BaseTreeDTO<Long> implements Serializable  {

    private static final long serialVersionUID = 1L;

    private String menusName;

    private String path;


}
