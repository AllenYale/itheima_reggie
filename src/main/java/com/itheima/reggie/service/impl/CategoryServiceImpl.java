package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 14:27
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;



    @Override
    public void remove(long id) {
        //查询是否关联菜品、有关联抛出自定义异常
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(queryWrapper);
        //全局处理器处理异常。返回R对象
        if(count>0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询是否关联套餐、有关联抛出自定义异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setMealService.count(setmealLambdaQueryWrapper);
        //如果分类下关联了setMeal不能删除，抛出异常，全局处理器处理异常。返回R对象
        if(count1>0){
            throw new CustomException("当前分类下关联了setmeal，不能删除");
        }

        //正常删除
        super.removeById(id);


    }
}
