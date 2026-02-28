package com.TsukasaChan.ShopVault.entity.marketing;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分变动记录表
 */
@Data
@TableName("sms_points_record")
public class PointsRecord implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 类型: 1签到 2购物奖励 3兑换消耗 4活动赠送
     */
    private Integer type;

    /**
     * 变动数量 (正数为增，负数为减)
     */
    private Integer amount;

    private String description;

    private LocalDateTime createTime;
}
