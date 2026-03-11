package com.TsukasaChan.ShopVault.service.marketing;

import com.TsukasaChan.ShopVault.entity.marketing.PointsRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PointsRecordService extends IService<PointsRecord> {
    String signIn(Long userId);
}
