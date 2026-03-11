package com.TsukasaChan.ShopVault.service.system.impl;

import com.TsukasaChan.ShopVault.entity.system.UserBehavior;
import com.TsukasaChan.ShopVault.mapper.system.UserBehaviorMapper;
import com.TsukasaChan.ShopVault.service.system.UserBehaviorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior> implements UserBehaviorService {

    /**
     * 记录用户行为并打分
     * @param type 1:点击(1分) 2:收藏(2分) 3:加购(3分) 4:购买(5分)
     */
    @Override
    public void recordBehavior(Long userId, Long productId, Integer type) {
        if (userId == null || productId == null) return;

        int weight = switch (type) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 5;
            default -> 0;
        };

        // 检查今天是否对同一商品做过相同的行为（防止刷分）
        long count = this.count(new LambdaQueryWrapper<UserBehavior>()
                .eq(UserBehavior::getUserId, userId)
                .eq(UserBehavior::getProductId, productId)
                .eq(UserBehavior::getBehaviorType, type)
                .apply("DATE(create_time) = CURDATE()")); // MySQL 函数，判断是否是今天

        if (count == 0) {
            UserBehavior behavior = new UserBehavior();
            behavior.setUserId(userId);
            behavior.setProductId(productId);
            behavior.setBehaviorType(type);
            behavior.setWeight(weight);
            this.save(behavior);
        }
    }
}