package com.hmetao.code_dictionary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class ToolDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    private String toolName;

    private String toolType;

    private String toolSize;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String url;


}
