package com.TsukasaChan.ShopVault.service.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.TsukasaChan.ShopVault.service.order.CartItemService;
import com.TsukasaChan.ShopVault.mapper.order.CartItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<CartItem> listMyCart(Long userId) {
        return this.list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getCreateTime));
    }

    @Override
    public void updateQuantity(Long cartItemId, Integer quantity, Long userId) {
        CartItem cartItem = this.getById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作或该商品不在购物车中");
        }
        cartItem.setQuantity(quantity);
        this.updateById(cartItem);
    }

    @Override
    public void deleteCartItems(List<Long> cartItemIds, Long userId) {
        this.remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .in(CartItem::getId, cartItemIds));
    }
}