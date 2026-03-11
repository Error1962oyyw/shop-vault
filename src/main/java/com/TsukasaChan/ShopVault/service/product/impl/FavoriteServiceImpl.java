package com.TsukasaChan.ShopVault.service.product.impl;

import com.TsukasaChan.ShopVault.service.system.UserBehaviorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.product.Favorite;
import com.TsukasaChan.ShopVault.service.product.FavoriteService;
import com.TsukasaChan.ShopVault.mapper.product.FavoriteMapper;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    UserBehaviorService userBehaviorService;

    @Override
    public String toggleFavorite(Long userId, Long productId) {
        // 查查是否已经收藏了
        Favorite exist = getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));

        if (exist != null) {
            // 如果有了，说明是再次点击，执行取消收藏
            this.removeById(exist.getId());
            return "已取消收藏";
        } else {
            // 如果没有，新增收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            this.save(favorite);

            userBehaviorService.recordBehavior(userId, productId, 2);
        }
        return "收藏成功";
    }
}