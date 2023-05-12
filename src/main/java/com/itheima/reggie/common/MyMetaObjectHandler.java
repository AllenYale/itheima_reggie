package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 11:03
 */
/*元数据对象处理器
* entity公共字段自动填充
* 在这个类不能获得httpsession对象，所以使用过滤器中放到ThreadLocal中的session对象userID
* */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * insert操作字段自动填充
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info("公共字段自动填充========== 当前线程id：{}, name: {}", Thread.currentThread().getId(), Thread.currentThread().getName());

        log.info(metaObject.toString());

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getValue());
        metaObject.setValue("updateUser", BaseContext.getValue());
    }

    /**
     * update操作字段自动填充
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject/*metaObj中有前端传过来的原始 表单/请求 对象*/) {
        log.info("公共字段自动填充[update]...");
        log.info("公共字段自动填充[update]========== 当前线程id：{}, name: {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        log.info(metaObject.toString());

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getValue());

    }
}
