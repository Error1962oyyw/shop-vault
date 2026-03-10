package com.TsukasaChan.ShopVault.manager;

import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.product.ProductService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardManager {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    @Data
    public static class DashboardStatDto {
        private long totalUsers;
        private long totalProducts;
        private long totalOrders;
        private BigDecimal totalRevenue; // 总营收
        private List<Map<String, Object>> recent7DaysSales; // 近7天销售额趋势
    }

    public DashboardStatDto getDashboardStats() {
        DashboardStatDto stat = new DashboardStatDto();

        // 1. 基础数量统计
        stat.setTotalUsers(userService.count());
        stat.setTotalProducts(productService.count());
        // 排除已关闭(4)的订单
        stat.setTotalOrders(orderService.count(new LambdaQueryWrapper<Order>().ne(Order::getStatus, 4)));

        // 2. 总营收统计 (只统计已付款、发货、收货的订单: 状态 1, 2, 3)
        List<Order> validOrders = orderService.list(new LambdaQueryWrapper<Order>()
                .in(Order::getStatus, 1, 2, 3));

        BigDecimal revenue = BigDecimal.ZERO;
        for (Order order : validOrders) {
            revenue = revenue.add(order.getPayAmount());
        }
        stat.setTotalRevenue(revenue);

        // 3. 统计近 7 天销售趋势 (供 ECharts 折线图使用)
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Order> recentOrders = orderService.list(new LambdaQueryWrapper<Order>()
                .in(Order::getStatus, 1, 2, 3)
                .ge(Order::getCreateTime, sevenDaysAgo));

        // 按日期分组汇总
        Map<String, BigDecimal> dailySalesMap = new HashMap<>();
        for (Order order : recentOrders) {
            // 截取日期部分，例如 "2026-03-05"
            String dateStr = order.getCreateTime().toLocalDate().toString();
            BigDecimal dailyTotal = dailySalesMap.getOrDefault(dateStr, BigDecimal.ZERO);
            dailySalesMap.put(dateStr, dailyTotal.add(order.getPayAmount()));
        }

        List<Map<String, Object>> trendList = new ArrayList<>();
        // 构造连续7天的数据（防止某天没订单导致断层）
        for (int i = 6; i >= 0; i--) {
            String dateKey = LocalDateTime.now().minusDays(i).toLocalDate().toString();
            Map<String, Object> point = new HashMap<>();
            point.put("date", dateKey);
            point.put("sales", dailySalesMap.getOrDefault(dateKey, BigDecimal.ZERO));
            trendList.add(point);
        }
        stat.setRecent7DaysSales(trendList);

        return stat;
    }
}