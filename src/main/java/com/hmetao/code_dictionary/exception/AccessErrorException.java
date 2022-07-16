package com.hmetao.code_dictionary.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccessErrorException extends RuntimeException {
    public String msg;


    public AccessErrorException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
