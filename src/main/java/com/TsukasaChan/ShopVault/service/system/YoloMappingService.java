package com.TsukasaChan.ShopVault.service.system;

import com.TsukasaChan.ShopVault.entity.system.YoloMapping;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* @author Error1962
* @description 针对表【sys_yolo_mapping(AI视觉标签映射表)】的数据库操作Service
* @createDate 2026-02-13 20:45:10
*/
public interface YoloMappingService extends IService<YoloMapping> {
    /**
     * 根据一组 YOLO 标签，查找对应的所有系统分类 ID
     */
    List<Long> findCategoryIdsByLabels(List<String> labels);

}
