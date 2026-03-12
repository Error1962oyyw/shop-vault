package com.TsukasaChan.ShopVault.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardStatDto {
    private long totalUsers;
    private long totalProducts;
    private long totalOrders;
    private BigDecimal totalRevenue; // 总营收
    private List<Map<String, Object>> recent7DaysSales; // 近7天销售额趋势
}