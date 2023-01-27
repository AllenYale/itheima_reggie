package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车
 * @ Author: Hanyuye
 * @ Date: 2023/1/24 15:57
 */
@Data
public class ShoppingCart /*implements Serializable*/ {
//    private static final long serialVersionUID = 1L;
    private Long id;
    /*菜品or套餐名称*/
    private String name;
    private String image;
    private Long userId;
    private Long dishId;
    private Long setmealId;
    private String dishFlavor;
    private Integer number;
    /*金额*/
    private BigDecimal amount;

//    @TableField(fill = FieldFill.INSERT) 不能加这个注解，只有创建时间，没有其他自动填充字段
    /*rg.apache.ibatis.reflection.ReflectionException:
    There is no setter for property named 'updateTime' in 'class com.itheima.reggie.entity.ShoppingCart'*/
    private LocalDateTime createTime;



}
