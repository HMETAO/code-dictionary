package com.hmetao.code_dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategorySnippetMenusDTO extends BaseTreeDTO<String> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String label;

    private Boolean snippet;

    public CategorySnippetMenusDTO(String id, String label, String parentId) {
        super(id, parentId);
        this.label = label;
    }

    public CategorySnippetMenusDTO(String id, String label, String parentId, Boolean snippet) {
        super(id, parentId);
        this.label = label;
        this.snippet = snippet;
    }
}