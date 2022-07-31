package com.hmetao.code_dictionary.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CategorySnippetMenusDTO extends BaseTreeDTO<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String label;

}
