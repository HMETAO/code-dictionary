package com.hmetao.code_dictionary.service;

import com.hmetao.code_dictionary.dto.GithubTrendDTO;
import com.hmetao.code_dictionary.form.TrendForm;

import java.util.List;

public interface GithubTrendService {
    List<GithubTrendDTO> trending(TrendForm trendForm);
}
