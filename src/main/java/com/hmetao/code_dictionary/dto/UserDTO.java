package com.hmetao.code_dictionary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserDTO extends UserInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "UserSig")
    private String userSig;

}
