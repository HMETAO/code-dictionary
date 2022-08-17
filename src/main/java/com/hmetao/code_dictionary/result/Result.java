package com.hmetao.code_dictionary.result;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Result implements Serializable {

    // 状态码
    private Integer code;

    // 数据
    private Object data;

    // 是否成功
    private Boolean success;

    // 返回信息
    private String message;

    public static ResponseEntity<Result> success() {
        return success(null, HttpStatus.OK);
    }

    public static ResponseEntity<Result> success(HttpStatus status) {
        return success(null, status);
    }

    public static ResponseEntity<Result> success(Object data) {
        return success(data, HttpStatus.OK);
    }


    public static ResponseEntity<Result> success(HttpStatus status, String message) {
        return success(null, status, message);
    }

    public static ResponseEntity<Result> success(Object data, HttpStatus status) {
        Result result = BuildSuccessResult(data, status);
        return new ResponseEntity<>(result, status);
    }

    public static ResponseEntity<Result> success(Object data, HttpStatus status, String message) {
        Result result = BuildSuccessResult(data, status, message);
        return new ResponseEntity<>(result, status);
    }

    public static ResponseEntity<Result> error(HttpStatus status, String message) {
        Result result = BuildErrorResult(status, message);
        return new ResponseEntity<>(result, status);
    }

    public static ResponseEntity<Result> error(HttpStatus status) {
        Result result = BuildErrorResult(status);
        return new ResponseEntity<>(result, status);
    }

    public static Result BuildErrorResult(HttpStatus status) {
        Result result = new Result();
        result.success = false;
        result.code = status.value();
        result.message = status.name();
        return result;
    }

    public static Result BuildErrorResult(HttpStatus status, String message) {
        Result result = new Result();
        result.success = false;
        result.code = status.value();
        result.message = message;
        return result;
    }

    public static Result BuildSuccessResult(Object data, HttpStatus status) {
        Result result = new Result();
        result.success = true;
        result.code = status.value();
        result.message = status.name();
        result.data = data;
        return result;
    }

    public static Result BuildSuccessResult(Object data, HttpStatus status, String message) {
        Result result = new Result();
        result.success = true;
        result.code = status.value();
        result.message = message;
        result.data = data;
        return result;
    }
}
