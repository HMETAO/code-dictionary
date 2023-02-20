package com.hmetao.code_dictionary.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.hmetao.code_dictionary.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Order(Integer.MIN_VALUE)
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Result> notLoginException(Exception e) {
        log.error("GlobalExceptionHandler === > " + e.getMessage(), e);
        return Result.error(HttpStatus.FORBIDDEN, "未能识别登录状态，请重新登陆");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Result> handleConstraintViolation(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> errMap = fieldErrors.stream().collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
        return Result.error(errMap, HttpStatus.BAD_REQUEST);
    }

}
