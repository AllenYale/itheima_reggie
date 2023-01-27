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
}
