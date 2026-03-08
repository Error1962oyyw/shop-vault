package com.TsukasaChan.ShopVault.service.marketing.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.marketing.PointsRecord;
import com.TsukasaChan.ShopVault.service.marketing.PointsRecordService;
import com.TsukasaChan.ShopVault.mapper.marketing.PointsRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord>
    implements PointsRecordService{

}