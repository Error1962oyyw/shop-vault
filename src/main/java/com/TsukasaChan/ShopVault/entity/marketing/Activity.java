package com.TsukasaChan.ShopVault.entity.marketing;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 营销活动与积分商城表
 * @TableName sms_activity
 */
@TableName(value ="sms_activity")
@Data
public class Activity implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称 (如: 2月会员日)
     */
    @TableField(value = "name")
    private String name;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private LocalDateTime end_time;

    /**
     * 类型: 1折扣活动 2积分兑换商品
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 折扣率 (如0.85)
     */
    @TableField(value = "discount_rate")
    private BigDecimal discount_rate;

    /**
     * 兑换所需积分
     */
    @TableField(value = "point_cost")
    private Integer point_cost;

    /**
     * 关联商品ID (若是兑换活动)
     */
    @TableField(value = "product_id")
    private Long product_id;

    /**
     * 状态 1启用 0停用
     */
    @TableField(value = "status")
    private Integer status;

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
        Activity other = (Activity) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getStart_time() == null ? other.getStart_time() == null : this.getStart_time().equals(other.getStart_time()))
            && (this.getEnd_time() == null ? other.getEnd_time() == null : this.getEnd_time().equals(other.getEnd_time()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getDiscount_rate() == null ? other.getDiscount_rate() == null : this.getDiscount_rate().equals(other.getDiscount_rate()))
            && (this.getPoint_cost() == null ? other.getPoint_cost() == null : this.getPoint_cost().equals(other.getPoint_cost()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getStart_time() == null) ? 0 : getStart_time().hashCode());
        result = prime * result + ((getEnd_time() == null) ? 0 : getEnd_time().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getDiscount_rate() == null) ? 0 : getDiscount_rate().hashCode());
        result = prime * result + ((getPoint_cost() == null) ? 0 : getPoint_cost().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", start_time=").append(start_time);
        sb.append(", end_time=").append(end_time);
        sb.append(", type=").append(type);
        sb.append(", discount_rate=").append(discount_rate);
        sb.append(", point_cost=").append(point_cost);
        sb.append(", product_id=").append(product_id);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}