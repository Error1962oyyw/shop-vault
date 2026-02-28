package com.TsukasaChan.ShopVault.service.order;

import com.TsukasaChan.ShopVault.entity.order.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Error1962
* @description 针对表【oms_order(订单主表)】的数据库操作Service
* @createDate 2026-02-13 20:34:18
*/
public interface OrderService extends IService<Order> {
    String createOrder(Long userId, List<Long> productIds, List<Integer> quantities);

}
