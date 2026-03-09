package com.TsukasaChan.ShopVault.controller.product;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.product.Favorite;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.product.FavoriteService;
import com.TsukasaChan.ShopVault.service.system.UserBehaviorService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;
    private final UserBehaviorService userBehaviorService;

    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    /**
     * 收藏/取消收藏 商品 (Toggle 逻辑)
     */
    @LogOperation(module = "商品模块", action = "用户收藏/取消收藏")
    @PostMapping("/toggle/{productId}")
    public Result<String> toggleFavorite(@PathVariable Long productId) {
        Long userId = getCurrentUserId();

        // 查查是否已经收藏了
        Favorite exist = favoriteService.getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));

        if (exist != null) {
            // 如果有了，说明是再次点击，执行取消收藏
            favoriteService.removeById(exist.getId());
            return Result.success("已取消收藏");
        } else {
            // 如果没有，新增收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            favoriteService.save(favorite);

            userBehaviorService.recordBehavior(userId, productId, 2);

            return Result.success("收藏成功");
        }
    }

    /**
     * 查看我的收藏夹
     */
    @GetMapping("/my-list")
    public Result<List<Favorite>> getMyFavorites() {
        List<Favorite> list = favoriteService.list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, getCurrentUserId())
                .orderByDesc(Favorite::getCreateTime));
        return Result.success(list);
    }
}