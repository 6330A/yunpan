package com.nenu.exception;

import com.mysql.cj.util.StringUtils;
import com.nenu.pojo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result exception(Exception e) {
        return Result.error(StringUtils.isNullOrEmpty(e.getMessage()) ? "操作失败" : e.getMessage());
    }
}
