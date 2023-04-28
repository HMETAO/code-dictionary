package com.hmetao.code_dictionary.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnippetUploadImageForm implements Serializable {
    private ArrayList<MultipartFile> files;
}
