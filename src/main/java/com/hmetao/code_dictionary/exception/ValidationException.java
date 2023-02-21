package com.hmetao.code_dictionary.exception;

public class ValidationException extends RuntimeException {
    public String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ValidationException(String msg) {
        super(msg);
        this.msg = msg;
    }

}
