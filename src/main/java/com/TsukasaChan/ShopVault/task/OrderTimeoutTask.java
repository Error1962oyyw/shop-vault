package com.TsukasaChan.ShopVault.task;

import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.entity.order.OrderItem;
import com.TsukasaChan.ShopVault.entity.product.Comment;
import com.TsukasaChan.ShopVault.service.order.OrderItemService;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.product.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderService orderService;
    private final CommentService commentService;
    private final OrderItemService orderItemService;

    /**
     * 每 10 分钟执行一次：处理超时未支付、自动收货、自动好评
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void processTimeoutOrders() {
        log.info("开始执行定时任务：订单状态巡检...");
        LocalDateTime now = LocalDateTime.now();

        // 1. 处理 24 小时未付款的订单 -> 已关闭 (4)
        LocalDateTime payTimeout = now.minusHours(24);
        List<Order> unpaidOrders = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 0)
                .lt(Order::getCreateTime, payTimeout));
        if (!unpaidOrders.isEmpty()) {
            for (Order order : unpaidOrders) {
                // 调用 OrderService 里写好的取消订单(恢复库存)的方法
                orderService.cancelOrder(order.getOrderNo(), order.getUserId());
            }
            log.info("定时任务：关闭了 {} 个超时未付款订单并恢复库存。", unpaidOrders.size());
        }

        // 2. 处理发货后到期未确认的订单 -> 已完成 (3)
        List<Order> receivingOrders = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 2)
                .isNotNull(Order::getAutoReceiveTime)
                .lt(Order::getAutoReceiveTime, now));
        if (!receivingOrders.isEmpty()) {
            for (Order order : receivingOrders) {
                orderService.confirmReceive(order.getOrderNo(), order.getUserId());
            }
            log.info("定时任务：自动确认收货了 {} 个订单。", receivingOrders.size());
        }

        // 3. 处理收货后 15 天未评价的订单 -> 自动好评
        LocalDateTime commentTimeout = now.minusDays(15);
        List<Order> finishedOrders = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 3)
                .isNotNull(Order::getReceiveTime)
                .lt(Order::getReceiveTime, commentTimeout));

        int autoCommentCount = 0;
        for (Order order : finishedOrders) {
            // 检查是否已经评价过
            long count = commentService.count(new LambdaQueryWrapper<Comment>().eq(Comment::getOrderId, order.getId()));
            if (count == 0) {
                List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
                for(OrderItem item : items) {
                    Comment comment = new Comment();
                    comment.setOrderId(order.getId());
                    comment.setProductId(item.getProductId());
                    comment.setUserId(order.getUserId());
                    comment.setStar(new BigDecimal("5.0"));
                    comment.setContent("系统默认好评");
                    comment.setAuditStatus(1); // 自动过审
                    commentService.save(comment);
                    autoCommentCount++;
                }
            }
        }
        if (autoCommentCount > 0) {
            log.info("定时任务：生成了 {} 条系统默认好评。", autoCommentCount);
        }
    }
}