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
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("添加购物车 add(@RequestBody ShoppingCart shoppingCart) 入参,{}", shoppingCart);

        //1：收到购物车数据，套餐或者菜品。
        //获取userID，封装到bean
        Long userID = BaseContext.getValue();
        shoppingCart.setUserId(userID);

        //2：判断菜品或者套餐是否存在相同口味的记录，存在数量就+1
        //3：不存在就新增记录
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (shoppingCart.getDishId() != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
            lambdaQueryWrapper.eq(shoppingCart.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        } else if (shoppingCart.getSetmealId() != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        } else {
            return R.error("error!!without dishID or setmealID...");
        }
        ShoppingCart serviceOne = shoppingCartService.getOne(lambdaQueryWrapper);

        //已经存在记录,存在数量就+1
        if (serviceOne != null) {
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

    /**
     * 购物车sub功能bug修复
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getValue();
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne1 = shoppingCartService.getOne(queryWrapper);
        //这一大堆我直接抄的上面的懒得写了可优化主要看下面

        //如果已经存在，就在原来数量基础上加一
        //先获取number的数据
        Integer number = cartServiceOne1.getNumber();
        if (number != 1) {//如果不等于1 比如等于2345 就正常减1
            cartServiceOne1.setNumber(number - 1);
            shoppingCartService.updateById(cartServiceOne1);
        } else {//如果等于1 点击了减号先设为0在删除 因为前端代码是根据number的值显示“加减”还是“选择规格“ 又兴趣的可以看前端代码174行那几行
            Long id = cartServiceOne1.getId();
            cartServiceOne1.setNumber(0);//这句很关键 删除前先把number设为0 前端根据这个判断显示
            shoppingCartService.removeById(id);
        }
        return R.success(cartServiceOne1);
    }


    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车...");

        //查看当前用户的shoppingCart，每个user的购物车都是隔离的。shoppingCart表里的userID是不一样的
        Long value = BaseContext.getValue();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, value);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        return R.success(shoppingCarts);
    }


    /**
     * 删除清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete() {
        Long value = BaseContext.getValue();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, value);
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("del success");
    }


}
