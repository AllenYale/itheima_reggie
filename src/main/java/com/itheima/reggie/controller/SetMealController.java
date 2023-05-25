package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealDishService;
import com.itheima.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 套餐管理
 * @ Author: Hanyuye
 * @ Date: 2023/1/18 15:56
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    /*
    * 新增套餐时，清理缓存（某一个分类下缓存数据全部删除, allEntries = true）
    * */
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("添加套餐。{}", setmealDto.toString());
        setMealService.saveWithDish(setmealDto);


        return R.success("新增套餐成功1！");
    }

    /**
     *
     * @param page 当前页
     * @param pageSize 页大小
     * @param name 查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        Page<SetmealDto> pageInfoDto = new Page<>();

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Setmeal::getName, name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setMealService.page(pageInfo, lambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo, pageInfoDto, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> collect = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());

        pageInfoDto.setRecords(collect);
        return R.success(pageInfoDto);
    }

    //learning：SpringMVC 可以接收到前端
    // 字符串逗号分开的get请求传值（http://localhost:8080/setmeal?ids=1616948151799898113,1415580119015145474）
    // controller参数自动接收 加上@RequestParam  封装为list 或者 数组


    @DeleteMapping
    /*优化：
    * 删除套餐时候，清理缓存（某一个分类下缓存数据全部删除, allEntries = true）
    * */
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
      /*  log.info("ids: {}", ids);
        setMealService.removeWithDish(ids);
        return R.success("套餐删除成功。。。");*/
        int index=0;
        for(Long id:ids) {
            Setmeal setmeal = setMealService.getById(id);
            if(setmeal.getStatus()!=1){
                setMealService.removeById(id);
            }else {
                index++;
            }
        }
        if (index>0&&index==ids.size()){
            return R.error("选中的套餐均为启售状态，不能删除");
        }else {
            return R.success("删除成功");
        }
    }


    //刪除套餐
//    @DeleteMapping
//    public R<String> delete(String[] ids){
//        int index=0;
//        for(String id:ids) {
//            Setmeal setmeal = setMealService.getById(id);
//            if(setmeal.getStatus()!=1){
//                setMealService.removeById(id);
//            }else {
//                index++;
//            }
//        }
//        if (index>0&&index==ids.length){
//            return R.error("选中的套餐均为启售状态，不能删除");
//        }else {
//            return R.success("删除成功");
//        }
//    }

    //停售启售修改状态
    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable int status,String[] ids){
        for (String id:ids){
            Setmeal setmeal = setMealService.getById(id);
            setmeal.setStatus(status);
            setMealService.updateById(setmeal);
        }
        return R.success("修改状态成功");
    }
    //根据Id查询套餐信息
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto=setMealService.getByIdWithDish(id);

        return R.success(setmealDto);
    }

    //修改套餐
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setMealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    /**
     * categoryId  菜品分类id （套餐分类）
     * status 状态 0:停用 1:启用
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    /*优化：
     查询时如果缓存中有数据则用缓存，没有数据就加入缓存；#setmeal SPEL表达式可以获取到形参*/
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(/*String categoryId, Integer status*/Setmeal setmeal){
        //查询套餐
        //SQL: select * from setmeal where categoryId = ? and status = ?
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setMealService.list(setmealLambdaQueryWrapper);

        return R.success(setmealList);
    }


}
