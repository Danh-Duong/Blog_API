package com.example.Blog_API.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResponseCode {
    SUCCESSFUL("00"),
    MISSING_MANDATORY_VALUE("01"),
    DUPLICATED("02"),
    RECORD_NOT_FOUND("03"),
    RECORD_EXISTED("04"),
    ACCESS_DENIED("20"),
    ERROR("30"),
    UNKNOWN("99");

    private String code;

    public String getCode() {
        return code;
    }
}
