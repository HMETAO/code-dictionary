package com.hmetao.code_dictionary.enums;

public enum CodeEnum {
    Java("java"),
    Cpp("cpp"),
    Python("py");

    private String ext;

    CodeEnum(String ext) {
        this.ext = ext;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
