package com.TsukasaChan.ShopVault.service.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.order.OrderItem;
import com.TsukasaChan.ShopVault.service.order.OrderItemService;
import com.TsukasaChan.ShopVault.mapper.order.OrderItemMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【oms_order_item(订单商品详情表)】的数据库操作Service实现
* @createDate 2026-02-13 20:34:18
*/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
    implements OrderItemService{

}




