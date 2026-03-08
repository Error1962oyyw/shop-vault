package com.TsukasaChan.ShopVault.service.system;

import com.TsukasaChan.ShopVault.entity.system.YoloMapping;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface YoloMappingService extends IService<YoloMapping> {
    /**
     * 根据一组 YOLO 标签，查找对应的所有系统分类 ID
     */
    List<Long> findCategoryIdsByLabels(List<String> labels);

}
