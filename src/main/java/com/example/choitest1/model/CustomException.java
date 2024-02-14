package com.example.choitest1.model;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CustomException extends RuntimeException {
    private String message;
    private String code;

    public CustomException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
