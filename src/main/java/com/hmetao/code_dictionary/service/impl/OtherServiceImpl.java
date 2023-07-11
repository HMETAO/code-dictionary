package com.hmetao.code_dictionary.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmetao.code_dictionary.constants.OtherConstants;
import com.hmetao.code_dictionary.constants.RedisConstants;
import com.hmetao.code_dictionary.constants.SSHConstants;
import com.hmetao.code_dictionary.dto.CalendarDTO;
import com.hmetao.code_dictionary.dto.LeetCodeDTO;
import com.hmetao.code_dictionary.form.WebSSHForm;
import com.hmetao.code_dictionary.service.OtherService;
import com.hmetao.code_dictionary.utils.RedisUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.covariance.codeforcesapi.CodeforcesApi;
import ru.covariance.codeforcesapi.entities.Contest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OtherServiceImpl implements OtherService {

    @Resource
    private HttpServletRequest request;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RedisUtil redisUtil;

    private final LocalDate localDate = LocalDate.now();

    @Override
    public void ssh(WebSSHForm webSSHForm) {
        StpUtil.getSession().set(SSHConstants.SSH_DATA_KEY, webSSHForm);
        StpUtil.getTokenSession().set(SSHConstants.SSH_DATA_KEY, webSSHForm);
    }

    @SneakyThrows
    @Override
    public List<CalendarDTO> calendar() {
        // 提取缓存
        String contestJsonStr = redisUtil.getCacheObject(RedisConstants.CALENDAR_CONTESTS_KEY);
        // 缓存非空
        if (StringUtils.isNotBlank(contestJsonStr)) {
            // 转换成对象返回
            return objectMapper.readValue(contestJsonStr, new TypeReference<List<CalendarDTO>>() {
            });
        }
        // 请求 codeforces 比赛信息
        int second = (int) localDate.minusMonths(2).atStartOfDay(ZoneOffset.ofHours(8)).toInstant().getEpochSecond();
        List<Contest> codeForces = new CodeforcesApi().contestList(false)
                .stream().filter(item -> second < item.getStartTimeSeconds())
                .collect(Collectors.toList());
        // 转换成的DTO
        log.info("OtherServiceImpl === > 拉取 CodeForces 比赛信息 " + codeForces);
        List<CalendarDTO> codeForcesCalendarDTO = returnCodeForcesCalendarDTO(codeForces);

        // 请求 leetcode 比赛信息
        List<LeetCodeDTO> leetCodeDTOS = leetcodeContestList();

        log.info("OtherServiceImpl === > 拉取 LeetCode 比赛信息 " + leetCodeDTOS);
        // 转换成的DTO
        List<CalendarDTO> leetcodeCalendarDTO = returnLeetCodeCalendarDTO(leetCodeDTOS);

        // 合并返回
        leetcodeCalendarDTO.addAll(codeForcesCalendarDTO);
        // 将数据存入redis ( time:[7 Day] )
        redisUtil.setCacheObject(RedisConstants.CALENDAR_CONTESTS_KEY,
                objectMapper.writeValueAsString(leetcodeCalendarDTO), 7, TimeUnit.DAYS);
        return leetcodeCalendarDTO;
    }

    private static List<CalendarDTO> returnLeetCodeCalendarDTO(List<LeetCodeDTO> leetCodeDTOS) {
        return leetCodeDTOS.stream().map(leetcode ->
                new CalendarDTO("LeetCode",
                        Collections.singletonList(leetcode.getStartTime()),
                        "blue",
                        "LeetCode " + leetcode.getTitle())).collect(Collectors.toList());
    }

    private static List<CalendarDTO> returnCodeForcesCalendarDTO(List<Contest> codeForces) {
        return codeForces.stream().map(codeforces -> {
            Instant instant = Instant.ofEpochSecond(codeforces.getStartTimeSeconds());
            ZoneId zone = ZoneId.systemDefault();
            return new CalendarDTO("CodeForces",
                    Collections.singletonList(LocalDate.ofInstant(instant, zone)),
                    "red",
                    codeforces.getName());
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("All")
    private List<LeetCodeDTO> leetcodeContestList() throws IOException, InterruptedException {

        HttpRequest leetcodeRequest = HttpRequest.newBuilder()
                .header("Content-type", "application/json")
                .header("User-Agent", request.getHeader("User-Agent"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        returnLeetCodeBodyJsonStr()))
                .uri(returnLeetCodeURI()).build();
        HttpResponse<String> send = HttpClient.newBuilder().build().send(leetcodeRequest, HttpResponse.BodyHandlers.ofString());
        log.info(send.body());
        HashMap map = objectMapper.readValue(send.body(), LinkedHashMap.class);
        LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) map.get("data");
        List<LeetCodeDTO> leetCodeDTOS = objectMapper.convertValue(data.get("contestUpcomingContests"), new TypeReference<>() {
        });
        return leetCodeDTOS;
    }

    private String returnLeetCodeBodyJsonStr() {
        return "{\n" +
                "    \"operationName\": null,\n" +
                "    \"variables\": {},\n" +
                "    \"query\": \"{\\n  contestUpcomingContests {    title\\n    titleSlug\\n     startTime\\n     }\\n}\\n\"\n" +
                "}";
    }

    private URI returnLeetCodeURI() {
        return UriComponentsBuilder.fromUriString(OtherConstants.LEETCODE_URL).path("graphql").build().toUri();
    }


}
