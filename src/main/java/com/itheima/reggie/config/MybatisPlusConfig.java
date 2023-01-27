package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MP分页拦截器
 * @ Author: Hanyuye
 * @ Date: 2023/1/17 22:22
 */
@Configuration
public class MybatisPlusConfig {

    /*
    * 需求目的：分页、MybatisPlus 分页需要设置配置类
    * 操作：添加分页拦截器，通过配置类、配置类@bean方法，返回值设置一个bean交给spring管理
    * */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }

}
