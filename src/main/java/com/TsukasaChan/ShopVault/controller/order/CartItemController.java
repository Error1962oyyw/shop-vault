package com.TsukasaChan.ShopVault.controller.order;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.order.CartItemService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
    private final UserService userService;

    // 获取当前用户ID的私有方法
    private Long getCurrentUserId() {
        String username = SecurityUtils.getCurrentUsername();
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user.getId();
    }

    /**
     * 加入购物车 (商品详情页调用)
     */
    @PostMapping("/add")
    public Result<String> addCart(@RequestBody CartItem cartItem) {
        Long userId = getCurrentUserId();
        cartItem.setUserId(userId);

        // 检查购物车是否已有该商品
        CartItem existItem = cartItemService.getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, cartItem.getProductId()));

        if (existItem != null) {
            // 已有则数量相加
            existItem.setQuantity(existItem.getQuantity() + cartItem.getQuantity());
            cartItemService.updateById(existItem);
        } else {
            // 没有则新增
            cartItemService.save(cartItem);
        }
        return Result.success("加入购物车成功");
    }

    /**
     * 查看我的购物车
     */
    @GetMapping("/list")
    public Result<List<CartItem>> list() {
        List<CartItem> list = cartItemService.list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, getCurrentUserId())
                .orderByDesc(CartItem::getCreateTime));
        return Result.success(list);
    }
}