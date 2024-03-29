package com.hmetao.code_dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDTO implements Serializable {
    private String key;

    private List<LocalDate> dates;

    private String color;

    private String label;


}
