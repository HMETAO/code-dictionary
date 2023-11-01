package com.hmetao.code_dictionary.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnippetCategoryMenusChangeForm implements Serializable {
    private Long pid;

    private Long currentId;

    private Boolean categoryToCategory;
}
