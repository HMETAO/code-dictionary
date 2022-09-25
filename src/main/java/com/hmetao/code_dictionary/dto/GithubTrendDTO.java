package com.hmetao.code_dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubTrendDTO {
    private String language;
    private String description;
    private String title;
    private String url;
    private String starNumber;
    private String shareNumber;

}
