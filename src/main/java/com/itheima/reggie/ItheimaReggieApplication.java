package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

//log.info() 通过@Slf4j该注解输出日志。
@Slf4j
@SpringBootApplication

/*在SpringBootApplication上使用@ServletComponentScan注解后，
Servlet（控制器）、Filter（过滤器）、Listener（监听器）可以直接通过@WebServlet、@WebFilter、@WebListener注解自动注册到Spring容器中，无需其他代码。*/
@ServletComponentScan
/*springboot开启事务*/
@EnableTransactionManagement
/*springboot开启springcache缓存框架，开启缓存注解功能*/
@EnableCaching
public class ItheimaReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItheimaReggieApplication.class, args);
        log.info("log.info==============项目启动成功==========");
        //测试LocalDateTime
//        System.out.println(LocalDateTime.now());//2023-05-05T23:22:06.407


    }

}
