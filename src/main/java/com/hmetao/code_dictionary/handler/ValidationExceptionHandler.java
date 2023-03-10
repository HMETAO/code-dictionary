package com.hmetao.code_dictionary.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
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

    /**
     * 登录状态检测
     *
     * @param e NotLoginException
     * @return 登录信息
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Result> notLoginException(Exception e) {
        log.error("ValidationExceptionHandler === > " + e.getMessage(), e);
        return Result.error(HttpStatus.FORBIDDEN, "未能识别登录状态，请重新登陆");
    }

    /**
     * 有关校验信息失败异常
     *
     * @param e ValidationException
     * @return 失败原因
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Result> ValidationException(ValidationException e) {
        log.error("ValidationExceptionHandler === > " + e.getMessage(), e);
        return Result.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }


    /**
     * 请求参数校验失败( json 提交 )
     *
     * @param e MethodArgumentNotValidException
     * @return 失败对象，携带失败信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Result> handleConstraintViolation(MethodArgumentNotValidException e) {
        return errorsHandler(e.getBindingResult().getFieldErrors());
    }

    /**
     * 请求参数校验失败( 表单 提交 )
     *
     * @param e BindException
     * @return 失败对象，携带失败信息
     */
    @ExceptionHandler(BindException.class)
    ResponseEntity<Result> handleConstraintViolation(BindException e) {
        return errorsHandler(e.getBindingResult().getFieldErrors());
    }

    public ResponseEntity<Result> errorsHandler(List<FieldError> fieldErrors) {
        StringBuilder errMsg = new StringBuilder();
        Map<String, String> errMap = fieldErrors.stream()
                .peek(fieldError -> {
                    // 拼接异常信息
                    errMsg.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append(" ");
                })
                .collect(Collectors.toMap(FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage));
        log.error("ValidationExceptionHandler === > 请求信息校验错误: " + errMsg);
        return Result.error(errMap, HttpStatus.BAD_REQUEST, errMsg.toString());
    }

}
