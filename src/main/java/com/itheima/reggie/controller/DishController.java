package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDTO;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 * @ Author: Hanyuye
 * @ Date: 2023年1月18日21:30:17
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品, dishDTO：{},", dishDTO.toString());
        //新增菜品，同事插入菜品对应口味数据；操作dish、dishflavor两张表
        dishService.savaWithFlavor(dishDTO);
        return R.success("操作成功！！！！！！");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
       lambdaQueryWrapper.like(name != null, Dish::getName, name);
       //类名；；方法引用，表示某个字段
       lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

       dishService.page(pageInfo, lambdaQueryWrapper);

        //Dish没有cateName属性(只有id)，因此封装DishDTO返回前端。
        // 拷贝 相关分页信息，pageInfo到dishDTOpageInfo
        Page<DishDTO> dishDTOPageInfo = new Page<>();
        //对象拷贝工具类spring-beans
        BeanUtils.copyProperties(pageInfo, dishDTOPageInfo, "records");
        //处理records属性，添加categoryName

        //方式一：StreamAPI
        List<DishDTO> dishDTOList = pageInfo.getRecords().stream().map((item) -> {
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(item, dishDTO);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDTO.setCategoryName(categoryName);

            return dishDTO;
        }).collect(Collectors.toList());
        dishDTOPageInfo.setRecords(dishDTOList);

        //方式二：遍历
        /*List<DishDTO> dishDTOList = new ArrayList<>();
        for (Dish record : pageInfo.getRecords())
        {
            Long categoryId = record.getCategoryId();
            //查询categoryName
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            //设置categoryName
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(record, dishDTO);
            dishDTO.setCategoryName(categoryName);
            dishDTOList.add(dishDTO);
        }
        dishDTOPageInfo.setRecords(dishDTOList);*/



        return R.success(dishDTOPageInfo);
    }

    @GetMapping("/{id}")
    public R<DishDTO> get(@PathVariable Long id){
        DishDTO dishDTO = dishService.getByIdWithFlavor(id);
        return R.success(dishDTO);
    }



    //TODO 2023年1月21日11:56:12菜品管理停售、起售（批量操作）
    //注意：业务逻辑判断
    //？？？(1)当我们对某个菜品A停售时，那么，包含这个菜品A的套餐A，套餐B，也要停售，这时需要关联dish，setmealDish，setmeal三张表
    //？？？(2)当我们启售套餐A时，需要检查套餐A里面的菜品A，菜品B，菜品C，是否都是启售状态，否则启售套餐A失败，并且提示，启售套餐A失败，套餐A内有菜品停售。
    //？？？菜品停售和删除还有套餐启售都要判断是否有菜品或者套餐停售或启售
    //我感觉你们想的有点多余了，我是商家，这个单品我就想在套餐里卖，不卖单品不行吗

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品, dishDTO：{},", dishDTO.toString());
        //update 菜品，操作dish、dishflavor两张表
        dishService.updateWithFlavor(dishDTO);
        return R.success("操作成功！！！！！！");
    }

    /**
     * 查询dishList，支持条件查询
     * @param dish
     * @return
     */
    //TODO 2023年1月24日09:34:36修改list控制器返回数据类型，前端需要拿到值口味数据 -> 判断显示按钮+数据展示
    @GetMapping("/list")
    public R<List<DishDTO>> list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId, dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus, 1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);

    // 修改list控制器返回数据类型，前端需要拿到值口味数据 -> 判断显示按钮+数据展示
        //stream流操作遍历流中每个item 装配DTO返回前端
        List<DishDTO> dishDTOS = list.stream().map(item -> {
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(item, dishDTO);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            //为dto设置categoryName
            String categoryName = category.getName();
            dishDTO.setCategoryName(categoryName);
            //dishid，联查dish flavor
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDTO.setFlavors(dishFlavorList);
            return dishDTO;

        }).collect(Collectors.toList());
        return R.success(dishDTOS);
    }


}