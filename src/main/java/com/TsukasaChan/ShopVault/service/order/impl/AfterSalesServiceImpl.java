package com.TsukasaChan.ShopVault.service.order.impl;

import com.TsukasaChan.ShopVault.entity.order.AfterSales;
import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.mapper.order.AfterSalesMapper;
import com.TsukasaChan.ShopVault.service.order.AfterSalesService;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class AfterSalesServiceImpl extends ServiceImpl<AfterSalesMapper, AfterSales> implements AfterSalesService {

    private final OrderService orderService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyAfterSales(AfterSales afterSales, Long userId) {
        Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, afterSales.getOrderNo())
                .eq(Order::getUserId, userId));

        if (order == null || (order.getStatus() != 2 && order.getStatus() != 3)) {
            throw new RuntimeException("订单状态不支持申请售后");
        }

        // 检查是否已经申请过
        long count = this.count(new LambdaQueryWrapper<AfterSales>().eq(AfterSales::getOrderNo, order.getOrderNo()));
        if (count > 0) {
            throw new RuntimeException("该订单已存在售后记录，请勿重复提交");
        }

        // 1. 创建售后记录
        afterSales.setUserId(userId);
        afterSales.setStatus(0); // 待处理
        afterSales.setRefundAmount(BigDecimal.ZERO);
        this.save(afterSales);

        // 2. 更新主订单状态
        order.setStatus(5); // 售后中
        orderService.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resolveAfterSales(String orderNo, boolean isAgree, String reply, BigDecimal refundAmount) {
        AfterSales afterSales = this.getOne(new LambdaQueryWrapper<AfterSales>().eq(AfterSales::getOrderNo, orderNo));
        if (afterSales == null || afterSales.getStatus() != 0) {
            throw new RuntimeException("找不到待处理的售后记录");
        }

        Order order = orderService.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        afterSales.setMerchantReply(reply);

        if (isAgree) {
            afterSales.setStatus(1); // 商家同意
            afterSales.setRefundAmount(refundAmount);
            order.setStatus(4); // 订单关闭

            // 积分扣除逻辑 (之前写在OrderService里，现在移到这里)
            int deductPoints = order.getPayAmount().multiply(new BigDecimal("100")).intValue();
            User user = userService.getById(order.getUserId());
            if (user.getPoints() >= deductPoints) {
                user.setPoints(user.getPoints() - deductPoints);
            } else {
                int missingPoints = deductPoints - user.getPoints();
                BigDecimal deductMoney = new BigDecimal(missingPoints).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                user.setPoints(0);
                // 实际退款扣除欠的积分钱
                afterSales.setRefundAmount(refundAmount.subtract(deductMoney).max(BigDecimal.ZERO));
            }
            userService.updateById(user);
        } else {
            afterSales.setStatus(2); // 商家拒绝
            order.setStatus(3); // 订单恢复为已完成
        }

        this.updateById(afterSales);
        orderService.updateById(order);
    }
}