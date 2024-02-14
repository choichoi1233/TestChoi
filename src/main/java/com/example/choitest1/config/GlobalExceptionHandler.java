package com.example.choitest1.config;

import com.example.choitest1.model.CustomException;
import com.example.choitest1.model.ResultVo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 공통적인 익셉션 핸들링
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleException(Exception e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ResultVo<String>> handleCustomException(CustomException e) {
        ResultVo<String> result = null;
        result = new ResultVo<String>(e.getMessage(), "F", null);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }


}