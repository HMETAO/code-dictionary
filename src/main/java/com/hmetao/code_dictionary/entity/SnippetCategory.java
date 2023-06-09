package com.hmetao.code_dictionary.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 文章标签关系表
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SnippetCategory对象", description = "片断分类关系表")
public class SnippetCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标签ID")
    private Long categoryId;

    @ApiModelProperty(value = "片段ID")
    private Long snippetId;

    @ApiModelProperty(value = "片段标题")
    private String snippetTitle;

    @ApiModelProperty(value = "snippet 类型")
    private Integer type;
}
