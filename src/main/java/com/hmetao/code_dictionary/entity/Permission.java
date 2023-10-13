package com.hmetao.code_dictionary.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * <p>
 * 权限
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Permission对象", description = "权限")
public class Permission extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "访问路径")
    @Pattern(message = "请按规则编写权限标识", regexp = ".*-?(\\*|select|update|delete|insert)")
    private String path;


}
