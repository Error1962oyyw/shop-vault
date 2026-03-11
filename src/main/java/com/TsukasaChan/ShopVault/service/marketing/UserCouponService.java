package com.TsukasaChan.ShopVault.service.marketing;

import com.TsukasaChan.ShopVault.entity.marketing.UserCoupon;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserCouponService extends IService<UserCoupon> {
    void claimCoupon(Long userId, Long activityId);
}
