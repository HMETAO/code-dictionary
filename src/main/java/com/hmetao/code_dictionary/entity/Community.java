package com.hmetao.code_dictionary.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author HMETAO
 * @since 2023-07-04
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Community对象", description = "")
public class Community extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    private Long uid;

    @ApiModelProperty("snippetId")
    private Long snippetId;

}
