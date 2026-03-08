package com.TsukasaChan.ShopVault.controller.product;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.TsukasaChan.ShopVault.entity.product.Category;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.order.CartItemService;
import com.TsukasaChan.ShopVault.service.product.CategoryService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    //private final CartItemService cartItemService;
    private final UserService userService;

    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    /**
     * 获取所有商品分类 (前端商城首页展示用，已加 Redis 缓存)
     */
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        // 这里调用了你刚刚写的带有 @Cacheable 的方法！
        return Result.success(categoryService.listCategories());
    }

    /**
     * 修改购物车商品数量
     */
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

    /**
     * 移出购物车 (支持批量删除)
     */
    @DeleteMapping("/delete")
    public Result<String> deleteCartItems(@RequestBody List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return Result.error(400, "请选择要删除的商品");
        }

        // 安全校验：只能删自己的购物车记录
        LambdaQueryWrapper<CartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartItem::getUserId, getCurrentUserId())
                .in(CartItem::getId, cartItemIds);

        cartItemService.remove(wrapper);
        return Result.success("已移出购物车");
    }
}