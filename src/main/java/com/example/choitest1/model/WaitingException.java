package com.example.choitest1.model;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WaitingException extends RuntimeException {
    private String message;
    private String code;

    public WaitingException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
