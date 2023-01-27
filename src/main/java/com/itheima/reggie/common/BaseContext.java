package com.itheima.reggie.common;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 11:41
 */
//ThreadLocal
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置userid
     * @param id
     */
    public static void setValue(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取userid
     * @return
     */
    public static Long getValue(){
        return threadLocal.get();
    }
}
