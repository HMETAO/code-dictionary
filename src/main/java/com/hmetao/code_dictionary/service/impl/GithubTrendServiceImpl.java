package com.hmetao.code_dictionary.service.impl;

import com.hmetao.code_dictionary.constants.GithubConstants;
import com.hmetao.code_dictionary.dto.GithubTrendDTO;
import com.hmetao.code_dictionary.enums.GithubSinceEnum;
import com.hmetao.code_dictionary.form.TrendForm;
import com.hmetao.code_dictionary.service.GithubTrendService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class GithubTrendServiceImpl implements GithubTrendService {

    @Override
    public List<GithubTrendDTO> trending(TrendForm trendForm) {
        // 获取since
        GithubSinceEnum sinceEnum = trendForm.getSince();
        String since = sinceEnum.getSinceRequestName();
        // 构造url
        String url = buildGithubRequestURL(since);
        try {
            // 生成dom
            Document doc = Jsoup.connect(url)
                    .timeout(3000)
                    .get();
            Elements trendBox = doc.body().select(".Box-row");
            return trendBox.stream().map(this::BuildGithubTrendDTOByDom).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("GithubTrendServiceImpl === >" + e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    private GithubTrendDTO BuildGithubTrendDTOByDom(Element element) {
        GithubTrendDTO githubTrendDTO = new GithubTrendDTO();
        String href = element.select(".h3.lh-condensed a").first().attr("href");
        String description = element.select(".col-9.color-fg-muted.my-1.pr-4").text();
        String language = element.select("span[itemprop='programmingLanguage']").text();
        String[] projectInfo = element.select(".Link--muted.d-inline-block.mr-3").text().split(" ");
        String starNumber = projectInfo[0];
        String shareNumber = projectInfo[1];
        githubTrendDTO.setTitle(href.substring(1));
        githubTrendDTO.setUrl(GithubConstants.GITHUB_URL + href);
        githubTrendDTO.setLanguage(language);
        githubTrendDTO.setDescription(description);
        githubTrendDTO.setStarNumber(starNumber);
        githubTrendDTO.setShareNumber(shareNumber);
        return githubTrendDTO;
    }

    private String buildGithubRequestURL(String since) {
        return UriComponentsBuilder
                .fromUriString(GithubConstants.GITHUB_TRENDING_URL)
                .queryParam("since", since)
                .toUriString();
    }

}
