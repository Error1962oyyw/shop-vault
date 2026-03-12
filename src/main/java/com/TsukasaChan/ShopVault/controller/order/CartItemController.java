package com.TsukasaChan.ShopVault.controller.order;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController; // 引入父类
import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.TsukasaChan.ShopVault.service.order.CartItemService;
import com.TsukasaChan.ShopVault.service.system.UserBehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController extends BaseController { // ★ 继承父类

    private final CartItemService cartItemService;
    private final UserBehaviorService userBehaviorService;

    @PostMapping("/add")
    public Result<String> addCart(@RequestBody CartItem cartItem) {
        Long userId = getCurrentUserId(); // 直接调用父类方法
        cartItemService.addOrUpdateCart(cartItem, userId);
        userBehaviorService.recordBehavior(userId, cartItem.getProductId(), 3);
        return Result.success("加入购物车成功");
    }

    @GetMapping("/list")
    public Result<List<CartItem>> list() {
        return Result.success(cartItemService.listMyCart(getCurrentUserId()));
    }

    @PutMapping("/update-quantity/{cartItemId}")
    public Result<String> updateQuantity(@PathVariable Long cartItemId, @RequestParam Integer quantity) {
        if (quantity == null || quantity < 1) return Result.error(400, "商品数量不能小于1");
        cartItemService.updateQuantity(cartItemId, quantity, getCurrentUserId());
        return Result.success("数量修改成功");
    }

    @DeleteMapping("/delete")
    public Result<String> deleteCartItems(@RequestBody List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) return Result.error(400, "请选择要删除的商品");
        cartItemService.deleteCartItems(cartItemIds, getCurrentUserId());
        return Result.success("已移出购物车");
    }
}