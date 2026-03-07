package com.TsukasaChan.ShopVault.service.order;

import com.TsukasaChan.ShopVault.entity.order.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface OrderService extends IService<Order> {
    // 立即购买 (单商品)
    String buyNow(Long userId, Long productId, Integer quantity);
    // 购物车结算 (多商品)
    String cartCheckout(Long userId, List<Long> cartItemIds);
    // 模拟付款
    void payOrder(String orderNo, Long userId);
    // 模拟商家发货
    void shipOrder(String orderNo);
    // 确认收货 (发积分 1:100)
    void confirmReceive(String orderNo, Long userId);

    void extendReceiveTime(String orderNo, Long userId);

    void cancelOrder(String orderNo, Long userId);
}
