package com.TsukasaChan.ShopVault.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResolveDto {
    private String orderNo;
    private Boolean isAgree;
    private String merchantReply;
    private BigDecimal refundAmount; // 如果同意退款，需指定退款金额
}