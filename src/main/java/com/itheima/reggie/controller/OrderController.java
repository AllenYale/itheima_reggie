package com.itheima.reggie.controller;

import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDTO;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @ Author: Hanyuye
 * @ Date: 2023/1/24 20:25
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

//    /**
//     * 订单管理
//     * @param page
//     * @param pageSize
//     * @return
//     */
//    //TODO 20230519 历史订单前端会现实两条记录
//    @Transactional
//    @GetMapping("/userPage")
//    public R<Page> userPage(int page,int pageSize){
//        //构造分页构造器
//        Page<Orders> pageInfo = new Page<>(page, pageSize);
//
//        Page<OrderDTO> ordersDtoPage = new Page<>();
//
//        //构造条件构造器
//        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
//
//        //添加排序条件
//        queryWrapper.orderByDesc(Orders::getOrderTime);
//
//        //进行分页查询
//        orderService.page(pageInfo,queryWrapper);
//
//        //对象拷贝
//        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
//
//        List<Orders> records=pageInfo.getRecords();
//
//        List<OrderDTO> list = records.stream().map((item) -> {
//            OrderDTO ordersDto = new OrderDTO();
//
//            BeanUtils.copyProperties(item, ordersDto);
//            Long Id = item.getId();
//            //根据id查分类对象
//            Orders orders = orderService.getById(Id);
//            String number = orders.getNumber();
//            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(OrderDetail::getOrderId,number);
//            List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper);
//            int num=0;
//
//            for(OrderDetail l:orderDetailList){
//                num+=l.getNumber().intValue();
//            }
//
//            ordersDto.setSumNum(num);
//            return ordersDto;
//        }).collect(Collectors.toList());
//
//        ordersDtoPage.setRecords(list);
//
//        return R.success(ordersDtoPage);
//    }

    /**
     * 支付后查看订单功能
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDTO> pageDto = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getValue());
        //这里是直接把当前用户分页的全部结果查询出来，要添加用户id作为查询条件，否则会出现用户可以查询到其他用户的订单情况
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //这里是把所有的订单分页查询出来
        orderService.page(pageInfo,queryWrapper);

        //对OrderDto进行属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrderDTO> orderDtoList = records.stream().map((item) ->{//item其实就是分页查询出来的每个订单对象
            OrderDTO orderDto = new OrderDTO();
            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = item.getId();//获取订单id
            //调用根据订单id条件查询订单明细数据的方法，把查询出来订单明细数据存入orderDetailList
            List<OrderDetail> orderDetailList = orderService.getOrderDetailListByOrderId(orderId);

            BeanUtils.copyProperties(item,orderDto);//把订单对象的数据复制到orderDto中
            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        //将订单分页查询的订单数据以外的内容复制到pageDto中，不清楚可以对着图看
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        pageDto.setRecords(orderDtoList);
        return R.success(pageDto);
    }



}
