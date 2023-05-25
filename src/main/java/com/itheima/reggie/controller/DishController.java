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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private RedisTemplate redisTemplate;

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

        //优化：清理所有菜品缓存数据，redis key支持通配符
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        //优化2：清理某个分类菜品缓存数据
        redisTemplate.delete("dish_"+dishDTO.getCategoryId()+"_"+dishDTO.getStatus());

        return R.success("操作成功！！！！！！");
    }

    /**
     * 查询dishList，支持条件查询
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDTO>> list(Dish dish){
        /*
        * 优化：
        * list数据先查询redis缓存，如果没有再查询db，将db中查询出来的数据放入缓存
        * 、（dml操作时要清理或更新缓存中数据，否则数据库和缓存中数据不一致）
        * */
        List<DishDTO> dishDTOS = null;
        //设置key
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //1：查询redis
        dishDTOS = (List<DishDTO>) redisTemplate.opsForValue().get(key);
            //缓存中有数据直接返回
        if(dishDTOS!=null){
            return R.success(dishDTOS);
        }

        //2: 没有数据，将db中查询出来的数据放入缓存

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId, dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus, 1);
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);

    // 修改list控制器返回数据类型，前端需要拿到值口味数据 -> 判断显示按钮+数据展示
        //stream流操作遍历流中每个item 装配DTO返回前端
            dishDTOS = list.stream().map(item -> {
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
            //将db中查询出来的数据放入缓存，设置过期时间TTL 3mins
        redisTemplate.opsForValue().set(key, dishDTOS, 3, TimeUnit.MINUTES);
        return R.success(dishDTOS);
    }

    //菜品起售
    @PostMapping("/status/{status}")
    public R<String> updateDishStatus(@PathVariable int status, String[] ids){
        //菜品起售优化-菜品停售对应的套餐要停售，套餐起售菜品也要起售。（不需要，商家dish不想单卖，只想放到套餐里卖）
        log.info("修改状态status，{}", status);
        for(String temp: ids){
            Dish dish = dishService.getById(temp);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }

    //删除菜品
    @DeleteMapping
    public R<String> delete(String[] ids){
        //TODO：2023年5月14日10:13:27 删除菜品 逻辑优化（目前仅优化在售dish则不删除）
        //1：在售则不删除。删除菜品前判断是否在售或者有相关的套餐在售
        //2：没有在售dish和涉及dish的套餐则：删除菜品，删除菜品、口味表、套餐菜品表 。删除涉及表：dish & dish_flavor & setmeal_dish
        for (String id:ids) {
//            dishService.removeById(id);
            dishService.checkDishStatusAndDel(id);
        }
        return R.success("删除成功");
    }


}