package com.hmetao.code_dictionary.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
 * @since 2022-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Tool对象", description="")
public class Tool extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工具名称")
    private String toolName;

    @ApiModelProperty(value = "工具类型")
    private String toolType;

    @ApiModelProperty(value = "下载地址")
    private String url;

    @ApiModelProperty(value = "用户id")
    private Long uid;


}
