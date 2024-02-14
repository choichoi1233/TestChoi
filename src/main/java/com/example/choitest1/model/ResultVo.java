package com.example.choitest1.model;

import lombok.Data;

@Data
public class ResultVo<T> {
    public ResultVo(String msg, String code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    private String msg;
    private String code;
    private T data;
}
