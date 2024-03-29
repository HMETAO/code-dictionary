package com.hmetao.code_dictionary.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HMETAOException extends RuntimeException {

    public String src;

    public String message;
    public HMETAOException(String message) {
        super(message);
        this.message = message;
    }

    public HMETAOException(String src, String message) {
        super(message);
        this.src = src;
        this.message = message;
    }
}
