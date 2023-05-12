package com.itheima.reggie.common;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 11:41
 */
//基于ThreadLocal，封装的工具类。用于保存和获取当前登入用户id。以线程为作用域，每个线程操作自己副本
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
