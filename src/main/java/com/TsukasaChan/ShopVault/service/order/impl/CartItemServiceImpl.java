package com.TsukasaChan.ShopVault.service.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.TsukasaChan.ShopVault.service.order.CartItemService;
import com.TsukasaChan.ShopVault.mapper.order.CartItemMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【oms_cart_item(购物车表)】的数据库操作Service实现
* @createDate 2026-02-13 20:34:18
*/
@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem>
    implements CartItemService{

}




