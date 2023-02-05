package com.hmetao.code_dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeetCodeDTO implements Serializable {

    private String title;

    private LocalDate startTime;

    private String titleSlug;

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        Instant instant = Instant.ofEpochSecond(startTime);
        ZoneId zone = ZoneId.systemDefault();
        this.startTime =  LocalDate.ofInstant(instant, zone);
    }
}
