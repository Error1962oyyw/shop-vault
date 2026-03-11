package com.TsukasaChan.ShopVault.service.product;

import com.TsukasaChan.ShopVault.entity.product.Favorite;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FavoriteService extends IService<Favorite> {
    String toggleFavorite(Long userId, Long productId);
}
