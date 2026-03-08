package com.TsukasaChan.ShopVault.service.order;

import com.TsukasaChan.ShopVault.entity.order.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderItemService extends IService<OrderItem> {
    // 提取的公共方法：恢复订单中所有商品的库存
    void restoreInventoryByOrderId(Long orderId);
}