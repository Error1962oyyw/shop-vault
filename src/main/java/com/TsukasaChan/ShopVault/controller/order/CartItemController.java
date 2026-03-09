package com.TsukasaChan.ShopVault.controller.order;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.order.CartItemService;
import com.TsukasaChan.ShopVault.service.system.UserBehaviorService;
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
    private final UserBehaviorService userBehaviorService; // 记录用户行为

    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    @PostMapping("/add")
    public Result<String> addCart(@RequestBody CartItem cartItem) {
        Long userId = getCurrentUserId();
        cartItem.setUserId(userId);

        CartItem existItem = cartItemService.getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, cartItem.getProductId()));

        if (existItem != null) {
            existItem.setQuantity(existItem.getQuantity() + cartItem.getQuantity());
            cartItemService.updateById(existItem);
        } else {
            cartItemService.save(cartItem);
        }

        // 记录加购行为，提供给推荐算法 (3代表加入购物车)
        userBehaviorService.recordBehavior(userId, cartItem.getProductId(), 3);

        return Result.success("加入购物车成功");
    }

    @GetMapping("/list")
    public Result<List<CartItem>> list() {
        List<CartItem> list = cartItemService.list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, getCurrentUserId())
                .orderByDesc(CartItem::getCreateTime));
        return Result.success(list);
    }

    @PutMapping("/update-quantity/{cartItemId}")
    public Result<String> updateQuantity(@PathVariable Long cartItemId, @RequestParam Integer quantity) {
        if (quantity == null || quantity < 1) {
            return Result.error(400, "商品数量不能小于1");
        }
        CartItem cartItem = cartItemService.getById(cartItemId);
        if (cartItem == null || !cartItem.getUserId().equals(getCurrentUserId())) {
            return Result.error(403, "无权操作或该商品不在购物车中");
        }

        cartItem.setQuantity(quantity);
        cartItemService.updateById(cartItem);
        return Result.success("数量修改成功");
    }

    @DeleteMapping("/delete")
    public Result<String> deleteCartItems(@RequestBody List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return Result.error(400, "请选择要删除的商品");
        }
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, getCurrentUserId())
                .in(CartItem::getId, cartItemIds);
        cartItemService.remove(wrapper);
        return Result.success("已移出购物车");
    }
}