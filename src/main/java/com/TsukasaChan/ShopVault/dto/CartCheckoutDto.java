package com.TsukasaChan.ShopVault.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartCheckoutDto {
    private List<Long> cartItemIds;
    private Long userCouponId; // 优惠券ID，允许为null
}