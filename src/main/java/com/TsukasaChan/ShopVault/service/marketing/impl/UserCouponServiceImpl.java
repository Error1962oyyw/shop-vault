package com.TsukasaChan.ShopVault.service.marketing.impl;

import com.TsukasaChan.ShopVault.entity.marketing.Activity;
import com.TsukasaChan.ShopVault.service.marketing.ActivityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.marketing.UserCoupon;
import com.TsukasaChan.ShopVault.service.marketing.UserCouponService;
import com.TsukasaChan.ShopVault.mapper.marketing.UserCouponMapper;
import org.springframework.stereotype.Service;

@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements UserCouponService {
    ActivityService  activityService;

    @Override
    public void claimCoupon(Long userId, Long activityId) {


        Activity activity = activityService.getById(activityId);
        if (activity == null || activity.getType() != 3 || activity.getStatus() != 1) {
            throw new RuntimeException("该优惠券不存在或已下架");
        }

        // 防刷：每个人针对同一张券只能领一次
        long count = this.count(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getActivityId, activityId));
        if (count > 0) {
            throw new RuntimeException("您已经领取过这张优惠券啦！");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setActivityId(activityId);
        userCoupon.setStatus(0); // 0:未使用
        userCoupon.setExpireTime(activity.getEndTime()); // 优惠券的过期时间就是活动的结束时间
        this.save(userCoupon);
    }
}