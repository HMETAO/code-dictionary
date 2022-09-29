package com.hmetao.code_dictionary.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmetao.code_dictionary.constants.GithubConstants;
import com.hmetao.code_dictionary.constants.RedisConstants;
import com.hmetao.code_dictionary.dto.GithubTrendDTO;
import com.hmetao.code_dictionary.enums.GithubSinceEnum;
import com.hmetao.code_dictionary.exception.GitHubException;
import com.hmetao.code_dictionary.form.TrendForm;
import com.hmetao.code_dictionary.service.GithubTrendService;
import com.hmetao.code_dictionary.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
public class GithubTrendServiceImpl implements GithubTrendService {


    @Resource
    private RedisUtils redisUtils;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public List<GithubTrendDTO> trending(TrendForm trendForm) throws JsonProcessingException {
        // 获取since
        String since = trendForm.getSince().getSinceRequestName();
        log.info("GithubTrendServiceImpl === > 开始拉取GitHub " + since + "信息");
        String redisTrend = redisUtils.getCacheObject(RedisConstants.GITHUB_TREND_KEY + since);
        // 还是保持着穿透状态
        if (redisTrend != null && redisTrend.equals(RedisConstants.REDIS_CACHE_ERROR_VALUE)) {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(redisTrend)) {
            // 请求GitHub获取数据
            List<GithubTrendDTO> trendList = requestGitHubGetTrend(since);
            // 缓存穿透了没查到数据
            if (trendList.isEmpty()) {
                redisUtils.setCacheObject(RedisConstants.GITHUB_TREND_KEY + since,
                        RedisConstants.REDIS_CACHE_ERROR_VALUE, 30, TimeUnit.SECONDS);
                throw new GitHubException("获取信息失败请稍后重试");
            } else {
                // 缓存到redis
                redisUtils.setCacheObject(RedisConstants.GITHUB_TREND_KEY + since,
                        objectMapper.writeValueAsString(trendList), 1, TimeUnit.DAYS);
            }
            return trendList;
        }
        return objectMapper.readValue(redisTrend, new TypeReference<>() {
        });
    }

    private List<GithubTrendDTO> requestGitHubGetTrend(String since) {
        try {
            // 请求并生成dom
            Document doc = Jsoup.connect(buildGithubRequestURL(since))
                    .timeout(3000)
                    .get();
            Elements trendBox = doc.body().select(".Box-row");
            // 解析dom并返回dto
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
