package com.hmetao.code_dictionary.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.hmetao.code_dictionary.exception.ValidationException;
import com.hmetao.code_dictionary.result.Result;
import com.hmetao.code_dictionary.utils.SaTokenUtils;
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
     * 请求参数校验失败
     *
     * @param e MethodArgumentNotValidException
     * @return 失败对象，携带失败信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Result> handleConstraintViolation(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        log.error(String.format("ValidationExceptionHandler === > 请求信息校验错误{ userId = %s } ", SaTokenUtils.getLoginUserInfo().getId()), e);
        Map<String, String> errMap = fieldErrors.stream()
                .collect(Collectors.toMap(FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage));
        return Result.error(errMap, HttpStatus.BAD_REQUEST);
    }

}
