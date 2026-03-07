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
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称 (如: 2月会员日)
     */
    private String name;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
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

    /**
     * 关联商品ID (若是兑换活动)
     */
    private Long productId;

    /**
     * 状态 1启用 0停用
     */
    private Integer status;

    /**
     * 规则表达式(如: 8代表每月8号, 4代表周四)
     */
    private String ruleExpression;

    /**
     * 积分获取倍率(如 2.0 代表双倍积分)
     */
    private BigDecimal pointsMultiplier;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}