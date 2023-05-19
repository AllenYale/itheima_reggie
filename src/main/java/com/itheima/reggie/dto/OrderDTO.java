package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO extends Orders {
    private List<OrderDetail> orderDetails;

    private int sumNum;//（前端会计算数量）
}

