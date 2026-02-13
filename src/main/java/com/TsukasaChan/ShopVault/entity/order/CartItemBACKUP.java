package com.TsukasaChan.ShopVault.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 购物车表
 */
@Data
@TableName("oms_cart_item")
public class CartItemBACKUP implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private LocalDateTime createTime;
}
