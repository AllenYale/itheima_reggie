package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/17 14:36
 */
@Slf4j
/*配置类*/
@Configuration
/*
在 Spring MVC 中，我们可以通过继承 WebMvcConfigurationSupport 来自定义配置类，并在其中添加自己的拦截器、视图解析器、消息转换器等组件。实际上，Spring Boot 也是基于 Spring MVC 构建的，因此同样可以使用 WebMvcConfigurationSupport 类来进行定制化配置。

在一个 Spring Boot 项目中，如果需要自定义 Spring MVC 的配置，可以创建一个继承 WebMvcConfigurationSupport 的配置类，并通过 @Configuration 注解将其注入到 Spring 容器中，如下所示：
 */
/*
WebMvcConfigurationSupport是webmvc的配置类，如果在springboot项目中，有配置类继承了WebMvcConfigurationSupport，
那么webmvc的自动配置类WebMvcAutoConfiguration就会失效。
使用WebMvcConfigurationSupport配置webmvc的一些方法：
 */
public class MyWebMvcConfig extends WebMvcConfigurationSupport{
    /*
    * 在 Spring Boot 中，静态资源应该放置在项目的 classpath 下。由于 Spring Boot 默认的 classpath 为 src/main/resources 文件夹，因此通常建议将静态资源文件放置在这个文件夹下。而在 src/main/resources 文件夹下，可以根据实际情况创建 static、templates、public 等文件夹，分别用于存放不同类型的静态资源。
具体来说，在 Spring Boot 项目中，静态资源会在启动时被加载到内存中，并通过 ResourceHttpRequestHandler 来处理静态资源的请求。其中，静态资源的访问路径取决于它们在 classpath 下的相对位置，例如：
    * */
    /**
     * 默认springboot 静态资源放在static or templates文件夹下，也可以自己写配置类自定义
     * MVC框架
     * 设置静态资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射=======================");
        //classpath：/dirName/后斜杠不能少
        //添加资源处理器（处理某些请求地址）   添加资源定位地址（将请求地址指向哪里）
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展mvc消息转换器，（扩展目的：避免long型传到前端、js丢失精度；转换为string，）
     * 如果不扩展，会使用mvc默认的消息转换器，无法将对象自定义转换成相应格式的json
     *
     * @param converters the list of configured converters to extend
     */
    @Override
    /*在mvc配置类中，该方法项目启动就会调用，参数converters是mvc默认的8个转换器，可以将自定的转换器加入并分配index*/
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器==============");

        //创建消息转换器对象：作用将controller返回对象转换为json
        MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();

/**
 * 对象映射器:基于jackson将Java对象转为json，或者将json转为Java对象
 * 将JSON解析为Java对象的过程称为 [从JSON反序列化Java对象]
 * 从Java对象生成JSON的过程称为 [序列化Java对象到JSON]
 */
        httpMessageConverter.setObjectMapper(new JacksonObjectMapper());

        //追加到mvc框架的转换器中，并设置优先使用
        converters.add(0, httpMessageConverter);

    }


}
