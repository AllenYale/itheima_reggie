package com.itheima.reggie.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/27 13:58
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    /*
    * 需求：自己创建redistemplate
    * 目的：设置序列化器，序列化时，让key/value 不再“乱码”
    * 操作：设置RedisConfig  @Configuration配置类   @bean redisTemplate方法
    * redis key序列化器
    * */
    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        //默认的key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
