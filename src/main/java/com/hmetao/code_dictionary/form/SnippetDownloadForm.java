package com.hmetao.code_dictionary.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnippetDownloadForm {
    private List<String> ids;
}
