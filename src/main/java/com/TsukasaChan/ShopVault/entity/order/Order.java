package com.TsukasaChan.ShopVault.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表
 */
@Data
@TableName("oms_order")
public class Order implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号(唯一)
     */
    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    /**
     * 实付金额 (扣除优惠后)
     */
    private BigDecimal payAmount;

    /**
     * 状态: 0待付款 1待发货 2已发货 3已完成 4已关闭 5售后中
     */
    private Integer status;

    /**
     * 收货人信息快照(JSON)
     */
    private String receiverSnapshot;

    private String trackingCompany;

    private String trackingNo;

    private LocalDateTime paymentTime;

    private LocalDateTime deliveryTime;

    private LocalDateTime receiveTime;

    private LocalDateTime createTime;
}
