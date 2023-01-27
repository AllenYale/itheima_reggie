package com.itheima.reggie.common;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 16:18
 */
//自定义业务异常
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
