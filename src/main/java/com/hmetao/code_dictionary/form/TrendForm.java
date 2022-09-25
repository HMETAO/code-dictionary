package com.hmetao.code_dictionary.form;

import com.hmetao.code_dictionary.enums.GithubSinceEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendForm {
    private GithubSinceEnum since = GithubSinceEnum.DAILY;
}
