package com.TsukasaChan.ShopVault.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * AI视觉标签映射表
 * @TableName sys_yolo_mapping
 */
@TableName(value ="sys_yolo_mapping")
@Data
public class YoloMapping implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * YOLO模型输出的标签 (如: cup, backpack)
     */
    @TableField(value = "yolo_label")
    private String yolo_label;

    /**
     * 关联的系统分类ID
     */
    @TableField(value = "category_id")
    private Long category_id;

    /**
     * 置信度阈值 (可选，用于过滤低可信度识别)
     */
    @TableField(value = "confidence_threshold")
    private BigDecimal confidence_threshold;

    /**
     * 是否启用映射
     */
    @TableField(value = "is_active")
    private Integer is_active;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        YoloMapping other = (YoloMapping) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getYolo_label() == null ? other.getYolo_label() == null : this.getYolo_label().equals(other.getYolo_label()))
            && (this.getCategory_id() == null ? other.getCategory_id() == null : this.getCategory_id().equals(other.getCategory_id()))
            && (this.getConfidence_threshold() == null ? other.getConfidence_threshold() == null : this.getConfidence_threshold().equals(other.getConfidence_threshold()))
            && (this.getIs_active() == null ? other.getIs_active() == null : this.getIs_active().equals(other.getIs_active()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getYolo_label() == null) ? 0 : getYolo_label().hashCode());
        result = prime * result + ((getCategory_id() == null) ? 0 : getCategory_id().hashCode());
        result = prime * result + ((getConfidence_threshold() == null) ? 0 : getConfidence_threshold().hashCode());
        result = prime * result + ((getIs_active() == null) ? 0 : getIs_active().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", yolo_label=").append(yolo_label);
        sb.append(", category_id=").append(category_id);
        sb.append(", confidence_threshold=").append(confidence_threshold);
        sb.append(", is_active=").append(is_active);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}