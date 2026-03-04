package com.TsukasaChan.ShopVault.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 售后服务记录表
 * @TableName oms_after_sales
 */
@Data
@TableName(value ="oms_after_sales")
public class AfterSales implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 申请人
     */
    private Long userId;

    /**
     * 申请售后的原因/诉求
     */
    private String reason;

    /**
     * 凭证图片(JSON数组)
     */
    private String images;

    /**
     * 状态: 0待商家处理 1商家同意 2商家拒绝 3已完成 4已撤销
     */
    private Integer status;

    /**
     * 商家的回复/拒绝理由
     */
    private String merchantReply;

    /**
     * 实际退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 处理时间
     */
    private LocalDateTime updateTime;

}