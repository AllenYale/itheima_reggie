package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

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
        log.info("项目启动成功==========");
//        System.out.println(LocalDateTime.now());

    }
    //TODO：2023年4月26日08:26:37 登入功能完成 P18； mac 配置 本地可以项目run；配置云redis？
    // https://www.bilibili.com/video/BV13a411q753?p=6&spm_id_from=pageDriver&vd_source=b6b5278300a836e624628f2d216bb728

}
