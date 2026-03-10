package com.TsukasaChan.ShopVault.service.marketing;

import com.TsukasaChan.ShopVault.entity.marketing.Activity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ActivityService extends IService<Activity> {
    String exchangeProduct(Long userId, Long activityId);
}
