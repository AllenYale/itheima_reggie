package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;

import java.util.List;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);


    /**
     * 根据订单id来查询订单明细的数据
     * @param orderId
     * @return
     */
    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId);

}
