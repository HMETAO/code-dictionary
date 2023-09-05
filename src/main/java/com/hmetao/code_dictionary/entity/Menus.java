package com.hmetao.code_dictionary.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@ApiModel(value = "Menus对象", description = "菜单表")
public class Menus extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "菜单名")
    private String menusName;

    @ApiModelProperty(value = "跳转路径")
    private String path;

    @ApiModelProperty(value = "父类id")
    private Long pid;


    @ApiModelProperty(value = "权限标识")
    private String perms;

}
