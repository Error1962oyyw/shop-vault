package com.TsukasaChan.ShopVault.controller.product;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.entity.product.Favorite;
import com.TsukasaChan.ShopVault.service.product.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController extends BaseController {

    private final FavoriteService favoriteService;

    /**
     * 收藏/取消收藏 商品 (Toggle 逻辑)
     */
    @LogOperation(module = "商品模块", action = "用户收藏/取消收藏")
    @PostMapping("/toggle/{productId}")
    public Result<String> toggleFavorite(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        return Result.success(favoriteService.toggleFavorite(userId, productId));
    }

    /**
     * 查看我的收藏夹
     */
    @GetMapping("/my-list")
    public Result<List<Favorite>> getMyFavorites() {
        return Result.success(favoriteService.getMyFavorites(getCurrentUserId()));
    }
}