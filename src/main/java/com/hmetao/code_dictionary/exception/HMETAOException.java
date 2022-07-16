package com.hmetao.code_dictionary.exception;


public class HMETAOException extends RuntimeException {
    public String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public HMETAOException(String msg) {
        super(msg);
        this.msg = msg;
    }

}
