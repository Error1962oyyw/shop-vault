package com.TsukasaChan.ShopVault.service.order;

import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CartItemService extends IService<CartItem> {
    void addOrUpdateCart(CartItem cartItem, Long userId);

    List<CartItem> listMyCart(Long userId);

    void updateQuantity(Long cartItemId, Integer quantity, Long userId);

    void deleteCartItems(List<Long> cartItemIds, Long userId);
}
