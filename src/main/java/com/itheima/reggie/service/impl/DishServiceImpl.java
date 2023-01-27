package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDTO;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 15:54
 */
@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    public void savaWithFlavor(DishDTO dishDTO) {
        //新增菜品，同事插入菜品对应口味数据；操作dish、dishflavor两张表
        //1.新增菜品，新增同时雪花算法生成了id
        save(dishDTO);
        Long id = dishDTO.getId();//获得自动生成的dishID

        List<DishFlavor> flavors = dishDTO.getFlavors();
        //把list元素重新加工了一下，又转换为list
        flavors = flavors.stream().map((item) -> {
            //给口味设置菜品id，口味：菜品 = n：1 多对一
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //2. 保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        //通过id查询dish信息
        Dish dishById = this.getById(id);

        //新建dishDTO，copy dish信息
        DishDTO dishDTO = new DishDTO();
        BeanUtils.copyProperties(dishById, dishDTO);

        //通过dishID查询口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, dishById.getId());
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDTO.setFlavors(list);
        //封装返回
        return dishDTO;
    }

    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //更新dish表
        updateById(dishDTO);
        Long dishId = dishDTO.getId();

        //更新flavor表，先删除在新增
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(queryWrapper);

        //批量为flavor设置dishID
        List<DishFlavor> dishDTOFlavors = dishDTO.getFlavors();
//        List<DishFlavor> collect = dishDTOFlavors.stream().map(new Function<DishFlavor, DishFlavor>() {
//            @Override
//            public DishFlavor apply(DishFlavor dishFlavor) {
//                dishFlavor.setDishId(dishId);
//                return dishFlavor;
//            }
//        }).collect(Collectors.toList());
        List<DishFlavor> collect = dishDTOFlavors.stream().map(s -> {
            s.setDishId(dishId);
            return s;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(collect);
    }
}
