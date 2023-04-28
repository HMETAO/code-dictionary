package com.hmetao.code_dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnippetUploadImageDTO implements Serializable {
    private List<String> urls;
}
