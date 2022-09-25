package com.hmetao.code_dictionary.enums;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum GithubSinceEnum {

    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly");

    private final String SinceRequestName;

    public String getSinceRequestName() {
        return SinceRequestName;
    }
}
