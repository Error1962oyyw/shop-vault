package com.TsukasaChan.ShopVault.entity.product;

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
 * 商品评价表
 * @TableName pms_comment
 */
@TableName(value ="pms_comment")
@Data
public class Comment implements Serializable {

    @TableField(exist = false)
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 星级 1.0-5.0
     */
    private BigDecimal star;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片(JSON数组)
     */
    private String images;

    /**
     * 状态: 1正常展示 2被管理员删除
     */
    private Integer auditStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 点踩数
     */
    private Integer dislikes;

    /**
     * 是否被举报: 0否 1是
     */
    private Integer isReported;
}