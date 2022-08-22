package com.hmetao.code_dictionary.form;

import com.hmetao.code_dictionary.entity.BaseEntity;
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
 * @since 2022-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SnippetForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String snippet;

    private Long categoryId;

}
