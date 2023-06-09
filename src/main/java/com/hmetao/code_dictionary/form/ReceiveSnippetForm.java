package com.hmetao.code_dictionary.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveSnippetForm {
    private Long snippetId;

    private Long uid;

    private Integer type;
}
