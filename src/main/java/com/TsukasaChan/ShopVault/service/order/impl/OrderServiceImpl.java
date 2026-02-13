package com.TsukasaChan.ShopVault.service.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.mapper.order.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【oms_order(订单主表)】的数据库操作Service实现
* @createDate 2026-02-13 20:34:18
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

}




