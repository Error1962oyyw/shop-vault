package com.TsukasaChan.ShopVault.entity.marketing;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 营销活动与积分商城表
 */
@Data
@TableName("sms_activity")
public class ActivityBACKUP implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    /**
     * 类型: 1折扣活动 2积分兑换商品
     */
    private Integer type;

    /**
     * 折扣率 (如0.85)
     */
    private BigDecimal discountRate;

    /**
     * 兑换所需积分
     */
    private Integer pointCost;

    private Long productId;

    /**
     * 状态 1启用 0停用
     */
    private Integer status;
}
