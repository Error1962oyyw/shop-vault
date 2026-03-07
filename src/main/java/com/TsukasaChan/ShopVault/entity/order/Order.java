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
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号(唯一)
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实付金额 (扣除优惠后)
     */
    private BigDecimal payAmount;

    /**
     * 状态: 0待付款 1待发货 2已发货 3已收货 4已关闭 5售后中
     */
    private Integer status;

    /**
     * 收货人信息快照(JSON格式，防止地址修改影响旧订单)
     */
    private String receiverSnapshot;

    /**
     * 物流公司
     */
    private String trackingCompany;

    /**
     * 物流单号
     */
    private String trackingNo;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 确认收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 自动收货时间(发货后+10天)
     */
    private LocalDateTime autoReceiveTime;

    /**
     * 是否已延长收货: 0否 1是
     */
    private Integer isExtended;

    /**
     * 订单关闭时间
     */
    private LocalDateTime closeTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}