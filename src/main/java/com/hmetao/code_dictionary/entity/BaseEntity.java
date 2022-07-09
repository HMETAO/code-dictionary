package com.hmetao.code_dictionary.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    protected Long id;

    /**
     * 时间格式统一采用 ISO 规范
     */
    @TableField(value = "created_date", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected LocalDateTime createdDate;

    @TableField(value = "last_modified_date", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    protected LocalDateTime lastModifiedDate;

    @ApiModelProperty(value = "0-存在 1-删除")
    @TableLogic
    private Integer isDelete;
}
