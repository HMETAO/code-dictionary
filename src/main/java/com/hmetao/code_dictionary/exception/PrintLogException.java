package com.hmetao.code_dictionary.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintLogException extends RuntimeException {
    public String src;

    public String message;

    public PrintLogException(String message) {
        super(message);
        this.message = message;
    }

    public PrintLogException(String src, String message) {
        super(message);
        this.src = src;
        this.message = message;
    }
}
