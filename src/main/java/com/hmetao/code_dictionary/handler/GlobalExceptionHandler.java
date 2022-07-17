package com.hmetao.code_dictionary.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.hmetao.code_dictionary.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public ResponseEntity<Result> notLoginException(Exception e) {
        return Result.error(HttpStatus.FORBIDDEN, "未能识别登录状态，请重新登陆");
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Result> processException(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}
