package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.List;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 22:03
 */
@Data
public class DishDTO extends Dish {
    //菜品对应多个口味数据
    private List<DishFlavor> flavors;

    private String categoryName;
    private Integer copies;
}
