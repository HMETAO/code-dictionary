package com.hmetao.code_dictionary.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author HMETAO
 * @since 2023-05-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="FriendInformation对象", description="")
public class FriendInformation extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "发送人id")
    private Long masterId;

    @ApiModelProperty(value = "接收方id")
    private Long slaveId;

}
