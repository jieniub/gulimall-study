package com.ljj.gulimall.product.exception;


import com.ljj.common.exception.BizCodeEnume;
import com.ljj.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


/*
* 集中处理所有异常
* */
@RestControllerAdvice
@Slf4j
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException exception){
        log.error("数据校验出现问题{}，异常类型{}", exception.getMessage(),exception.getClass());
        BindingResult bindingResult = exception.getBindingResult();
        Map<String,String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError)->{
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(),BizCodeEnume.VALID_EXCEPTION.getMessage()).put("data",errorMap);
    }

//
}
