package com.TsukasaChan.ShopVault.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * AI视觉标签映射表
 * 用于将YOLO识别出的英文标签映射为系统内部的商品分类ID
 */
@Data
@TableName("sys_yolo_mapping")
public class YoloMappingBACKUP implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * YOLO模型输出的标签 (如: cup, backpack)
     */
    private String yoloLabel;

    /**
     * 关联的系统分类ID
     */
    private Long categoryId;

    /**
     * 置信度阈值 (可选)
     */
    private BigDecimal confidenceThreshold;

    /**
     * 是否启用映射 1:是 0:否
     */
    private Boolean isActive;
}
