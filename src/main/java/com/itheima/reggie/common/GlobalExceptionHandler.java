package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 *
 * 定义全局异常处理器，aop编织入各个业务方法
 * @ControllerAdvice(annotations = {RestController.class, Controller.class})
 * 配合
 *  @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
 *使用
 *
 * @ Author: Hanyuye
 * @ Date: 2023/1/17 21:52
 */
@Slf4j
/*
* @ControllerAdvice比较熟知的用法是结合@ExceptionHandler用于全局异常的处理
* 使用全局异常处理器GlobalExceptionHandle基于aop，抛异常了这里统一处理
* */
//通知、拦截那些controller
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
    public R<String> sqlIntegrityConstraintViolationExceptionHandler(/*只需声明参数，框架会自动注入对象*/SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "already exist！";
            return R.error(msg);
        }
        return R.error("unknown error occurred..."+ex.getMessage());
    }

    //全局异常处理器，处理自定义异常
    @ExceptionHandler(value = {CustomException.class})
    public R<String> sqlIntegrityConstraintViolationExceptionHandler(CustomException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
