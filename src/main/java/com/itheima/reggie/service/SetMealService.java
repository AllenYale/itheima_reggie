package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 15:53
 */
public interface SetMealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除菜单和菜品
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    SetmealDto getByIdWithDish(Long id);

    /**
     * 修改套餐和菜品
     * @param setmealDto
     */
    void updateWithDish(SetmealDto setmealDto);

}
