package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDTO;
import com.itheima.reggie.entity.Dish;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 15:52
 */
public interface DishService extends IService<Dish> {
    /**
     *  新增菜品，插入菜品对应口味数据；操作dish、dishflavor两张表
     * @param dishDTO
     */
    void savaWithFlavor(DishDTO dishDTO);

    /**
     * 通过dishID查询dish信息和口味信息
     *
     * @param id
     * @return
     */
    DishDTO getByIdWithFlavor(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 删除菜品
     * //1：在售则不删除。删除菜品前判断是否在售或者有相关的套餐在售
     * //2：没有在售dish和涉及dish的套餐则：删除菜品，删除菜品、口味表、套餐菜品表 。删除涉及表：dish & dish_flavor & setmeal_dish
     *
     * @param id
     */
    void checkDishStatusAndDel(String id);
}
