package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/24 16:09
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加购物车 add(@RequestBody ShoppingCart shoppingCart) 入参,{}", shoppingCart);

        //1：收到购物车数据，套餐或者菜品。
            //获取userID，封装到bean
        Long userID = BaseContext.getValue();
        shoppingCart.setUserId(userID);

        //2：判断菜品或者套餐是否存在相同口味的记录，存在数量就+1
        //3：不存在就新增记录
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(shoppingCart.getDishId()!=null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
            lambdaQueryWrapper.eq(shoppingCart.getDishFlavor()!=null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        } else if (shoppingCart.getSetmealId() != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        } else{
            return R.error("error!!without dishID or setmealID...");
        }
            ShoppingCart serviceOne = shoppingCartService.getOne(lambdaQueryWrapper);

            //已经存在记录,存在数量就+1
        if (serviceOne != null){
            Integer number = serviceOne.getNumber();
            serviceOne.setNumber(++number);
            shoppingCartService.updateById(serviceOne);
        } else {
            shoppingCart.setNumber(1);

            //自动填充字段失效（缺少其他自动填充字段），需要手动fill
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);

            serviceOne = shoppingCart;
        }

        return R.success(serviceOne);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        //查看当前用户的shoppingCart，每个user的购物车都是隔离的。shoppingCart表里的userID是不一样的
        Long value = BaseContext.getValue();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,value);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        return R.success(shoppingCarts);
    }


    /**
     * 删除清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        Long value = BaseContext.getValue();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, value);
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("del success");
    }




}
