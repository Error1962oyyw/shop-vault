package com.TsukasaChan.ShopVault.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.TsukasaChan.ShopVault.service.order.CartItemService;
import com.TsukasaChan.ShopVault.mapper.order.CartItemMapper;
import org.springframework.stereotype.Service;

@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {
    @Override
    public void addOrUpdateCart(CartItem cartItem, Long userId) {

        CartItem existItem = this.getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, cartItem.getProductId()));

        if (existItem != null) {
            existItem.setQuantity(existItem.getQuantity() + cartItem.getQuantity());
            updateById(existItem);
        } else {
            this.save(cartItem);
        }
    }
}