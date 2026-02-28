package com.TsukasaChan.ShopVault.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评价表
 */
@Data
@TableName("pms_comment")
public class Comment implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long productId;

    private Long userId;

    private Integer star;

    private String content;

    /**
     * 评价图片(JSON数组)
     */
    private String images;

    /**
     * 审核状态: 0待审核 1通过 2拒绝
     */
    private Integer auditStatus;

    private LocalDateTime createTime;
}
