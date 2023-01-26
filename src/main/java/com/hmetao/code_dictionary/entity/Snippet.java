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
 * @since 2022-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Snippet对象", description = "片断表")
public class Snippet extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主标题")
    private String title;

    // 保留字段 未使用
    @ApiModelProperty(value = "子标题")
    private String subtitle;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "片段")
    private String snippet;

    @ApiModelProperty(value = "置顶（0:不置顶，1:置顶）")
    private Boolean top;


}
