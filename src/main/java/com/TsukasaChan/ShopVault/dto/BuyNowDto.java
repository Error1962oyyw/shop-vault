package com.TsukasaChan.ShopVault.dto;

import lombok.Data;

@Data
public class BuyNowDto {
    private Long productId;
    private Integer quantity;
    private Long userCouponId; // 优惠券ID，允许为null
}