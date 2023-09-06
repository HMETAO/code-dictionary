package com.hmetao.code_dictionary.form;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryForm {

    Integer pageSize = 5;

    Integer pageNum = 1;
}
