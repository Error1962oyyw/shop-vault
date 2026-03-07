package com.TsukasaChan.ShopVault.service.marketing;

import com.TsukasaChan.ShopVault.entity.marketing.Activity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Error1962
* @description 针对表【sms_activity(营销活动与积分商城表)】的数据库操作Service
* @createDate 2026-03-08 02:47:11
*/
public interface ActivityService extends IService<Activity> {
    String exchangeProduct(Long userId, Long activityId);

}
