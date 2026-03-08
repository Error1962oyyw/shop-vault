package com.TsukasaChan.ShopVault.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户行为轨迹表
 * @TableName sys_user_behavior
 */
@TableName(value ="sys_user_behavior")
@Data
public class UserBehavior implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 行为: 1点击 2收藏 3加购物车 4购买
     */
    private Integer behaviorType;

    /**
     * 权重分值 (如点击1分，购买5分)
     */
    private Integer weight;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}