package com.TsukasaChan.ShopVault.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 订单主表
 * @TableName oms_order
 */
@TableName(value ="oms_order")
@Data
public class Order implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号(唯一)
     */
    @TableField(value = "order_no")
    private String order_no;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long user_id;

    /**
     * 订单总金额
     */
    @TableField(value = "total_amount")
    private BigDecimal total_amount;

    /**
     * 实付金额 (扣除优惠后)
     */
    @TableField(value = "pay_amount")
    private BigDecimal pay_amount;

    /**
     * 状态: 0待付款 1待发货 2已发货 3已完成 4已关闭 5售后中
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 收货人信息快照(JSON格式，防止地址修改影响旧订单)
     */
    @TableField(value = "receiver_snapshot")
    private String receiver_snapshot;

    /**
     * 物流公司
     */
    @TableField(value = "tracking_company")
    private String tracking_company;

    /**
     * 物流单号
     */
    @TableField(value = "tracking_no")
    private String tracking_no;

    /**
     * 支付时间
     */
    @TableField(value = "payment_time")
    private LocalDateTime payment_time;

    /**
     * 发货时间
     */
    @TableField(value = "delivery_time")
    private LocalDateTime delivery_time;

    /**
     * 确认收货时间
     */
    @TableField(value = "receive_time")
    private LocalDateTime receive_time;

    /**
     * 
     */
    @TableField(value = "create_time")
    private LocalDateTime create_time;

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
        Order other = (Order) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrder_no() == null ? other.getOrder_no() == null : this.getOrder_no().equals(other.getOrder_no()))
            && (this.getUser_id() == null ? other.getUser_id() == null : this.getUser_id().equals(other.getUser_id()))
            && (this.getTotal_amount() == null ? other.getTotal_amount() == null : this.getTotal_amount().equals(other.getTotal_amount()))
            && (this.getPay_amount() == null ? other.getPay_amount() == null : this.getPay_amount().equals(other.getPay_amount()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getReceiver_snapshot() == null ? other.getReceiver_snapshot() == null : this.getReceiver_snapshot().equals(other.getReceiver_snapshot()))
            && (this.getTracking_company() == null ? other.getTracking_company() == null : this.getTracking_company().equals(other.getTracking_company()))
            && (this.getTracking_no() == null ? other.getTracking_no() == null : this.getTracking_no().equals(other.getTracking_no()))
            && (this.getPayment_time() == null ? other.getPayment_time() == null : this.getPayment_time().equals(other.getPayment_time()))
            && (this.getDelivery_time() == null ? other.getDelivery_time() == null : this.getDelivery_time().equals(other.getDelivery_time()))
            && (this.getReceive_time() == null ? other.getReceive_time() == null : this.getReceive_time().equals(other.getReceive_time()))
            && (this.getCreate_time() == null ? other.getCreate_time() == null : this.getCreate_time().equals(other.getCreate_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrder_no() == null) ? 0 : getOrder_no().hashCode());
        result = prime * result + ((getUser_id() == null) ? 0 : getUser_id().hashCode());
        result = prime * result + ((getTotal_amount() == null) ? 0 : getTotal_amount().hashCode());
        result = prime * result + ((getPay_amount() == null) ? 0 : getPay_amount().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getReceiver_snapshot() == null) ? 0 : getReceiver_snapshot().hashCode());
        result = prime * result + ((getTracking_company() == null) ? 0 : getTracking_company().hashCode());
        result = prime * result + ((getTracking_no() == null) ? 0 : getTracking_no().hashCode());
        result = prime * result + ((getPayment_time() == null) ? 0 : getPayment_time().hashCode());
        result = prime * result + ((getDelivery_time() == null) ? 0 : getDelivery_time().hashCode());
        result = prime * result + ((getReceive_time() == null) ? 0 : getReceive_time().hashCode());
        result = prime * result + ((getCreate_time() == null) ? 0 : getCreate_time().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", order_no=").append(order_no);
        sb.append(", user_id=").append(user_id);
        sb.append(", total_amount=").append(total_amount);
        sb.append(", pay_amount=").append(pay_amount);
        sb.append(", status=").append(status);
        sb.append(", receiver_snapshot=").append(receiver_snapshot);
        sb.append(", tracking_company=").append(tracking_company);
        sb.append(", tracking_no=").append(tracking_no);
        sb.append(", payment_time=").append(payment_time);
        sb.append(", delivery_time=").append(delivery_time);
        sb.append(", receive_time=").append(receive_time);
        sb.append(", create_time=").append(create_time);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}