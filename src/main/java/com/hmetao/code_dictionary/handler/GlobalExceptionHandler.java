package com.hmetao.code_dictionary.handler;

import com.hmetao.code_dictionary.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Result> processException(Exception e) {
        log.error("GlobalExceptionHandler === > " + e.getMessage(), e);
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
