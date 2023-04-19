package com.hmetao.code_dictionary.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
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

    private Long id;

    @NotNull(message = "标题禁止为空")
    private String title;

    private String snippet;

    @NotNull(message = "请选择分组")
    private Long categoryId;

    private String type;
}
