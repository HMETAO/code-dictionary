package com.hmetao.code_dictionary.service.impl;

import com.hmetao.code_dictionary.constants.OtherConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

class GithubTrendServiceImplTest {

    @Test
    public void trendingTest() {
        try {
            String url = buildGithubRequestURL("daily");
            System.out.println(url);
            Document doc = Jsoup.connect(url)
                    .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.50")
                    .timeout(3000)
                    .get();
            Elements trendBox = doc.body().select(".Box-row");
            for (Element element : trendBox) {
                String href = element.select(".h3.lh-condensed a").first().attr("href");
                String description = element.select(".col-9.color-fg-muted.my-1.pr-4").text();
                String language = element.select("span[itemprop='programmingLanguage']").text();
                System.out.println(Arrays.toString(element.select(".Link--muted.d-inline-block.mr-3").text().split(" ")));
                System.out.println("language  == > " + language);
                System.out.println("description  == > " + description);
                System.out.println("title  == > " + href.substring(1));
                System.out.println("url === > " + OtherConstants.GITHUB_URL + href);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildGithubRequestURL(String since) {
        return UriComponentsBuilder
                .fromUriString(OtherConstants.GITHUB_TRENDING_URL)
                .queryParam("since", since)
                .toUriString();
    }
}