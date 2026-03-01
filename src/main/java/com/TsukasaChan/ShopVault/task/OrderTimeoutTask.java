package com.TsukasaChan.ShopVault.task;

import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderService orderService;

    /**
     * 每 10 分钟执行一次，清理 24 小时未付款的订单
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void closeTimeoutOrders() {
        log.info("开始执行定时任务：扫描并关闭超时未付款订单...");
        // 计算 24 小时前的时间
        LocalDateTime timeoutTime = LocalDateTime.now().minusHours(24);

        // 查询：状态为 0 (待付款) 且 创建时间在 24 小时之前的订单
        List<Order> timeoutOrders = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 0)
                .lt(Order::getCreateTime, timeoutTime));

        if (!timeoutOrders.isEmpty()) {
            for (Order order : timeoutOrders) {
                order.setStatus(4); // 4: 已关闭
                // 这里还可以增加：恢复商品库存的逻辑
            }
            orderService.updateBatchById(timeoutOrders);
            log.info("成功关闭了 {} 个超时订单。", timeoutOrders.size());
        }
    }
}