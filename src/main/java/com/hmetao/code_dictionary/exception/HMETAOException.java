package com.hmetao.code_dictionary.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HMETAOException extends RuntimeException {
    public String msg;

    public String src;

    public HMETAOException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public HMETAOException(String msg, String src) {
        super(msg);
        this.msg = msg;
        this.src = src;
    }
}
