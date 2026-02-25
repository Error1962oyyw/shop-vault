package com.TsukasaChan.ShopVault.service.system.impl;

import com.TsukasaChan.ShopVault.entity.system.YoloMapping;
import com.TsukasaChan.ShopVault.mapper.system.YoloMappingMapper;
import com.TsukasaChan.ShopVault.service.system.YoloMappingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class YoloMappingServiceImpl extends ServiceImpl<YoloMappingMapper, YoloMapping> implements YoloMappingService {

    @Override
    public List<Long> findCategoryIdsByLabels(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return List.of();
        }

        // 使用 Lambda 表达式查询匹配的标签
        List<YoloMapping> mappings = this.list(new LambdaQueryWrapper<YoloMapping>()
                .in(YoloMapping::getYoloLabel, labels)
                .eq(YoloMapping::getIsActive, true));

        // 提取所有的 CategoryId 并去重
        return mappings.stream()
                .map(YoloMapping::getCategoryId)
                .distinct()
                .collect(Collectors.toList());
    }
}