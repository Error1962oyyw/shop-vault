package com.TsukasaChan.ShopVault.service.order.impl;

import com.TsukasaChan.ShopVault.entity.order.AfterSales;
import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.mapper.order.AfterSalesMapper;
import com.TsukasaChan.ShopVault.service.order.AfterSalesService;
import com.TsukasaChan.ShopVault.service.order.OrderItemService;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.product.ProductService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AfterSalesServiceImpl extends ServiceImpl<AfterSalesMapper, AfterSales> implements AfterSalesService {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderItemService orderItemService; // 用于查明细恢复库存

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyAfterSales(AfterSales afterSales, Long userId) {
        Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, afterSales.getOrderNo())
                .eq(Order::getUserId, userId));

        // 状态 1(待发货), 2(待收货), 3(已完成) 均可申请售后
        if (order == null || (order.getStatus() != 1 && order.getStatus() != 2 && order.getStatus() != 3)) {
            throw new RuntimeException("当前订单状态不支持申请售后");
        }

        long count = this.count(new LambdaQueryWrapper<AfterSales>().eq(AfterSales::getOrderNo, order.getOrderNo()));
        if (count > 0) throw new RuntimeException("该订单已存在售后记录，请勿重复提交");

        afterSales.setUserId(userId);
        afterSales.setRefundAmount(order.getPayAmount()); // 默认申请全额退款

        User user = userService.getById(userId);

        // 信誉分特权：如果处于【待发货】状态，且信誉分 >= 90，触发极速退款，无需商家审批！
        if (order.getStatus() == 1 && user.getCreditScore() >= 90) {
            afterSales.setStatus(3); // 售后直接完成
            afterSales.setMerchantReply("信誉极好，系统自动秒退款");
            this.save(afterSales);

            order.setStatus(4); // 订单直接关闭
            order.setCloseTime(LocalDateTime.now());
            orderService.updateById(order);

            // 恢复库存
            orderItemService.restoreInventoryByOrderId(order.getId());
            return;
        }

        // 普通流程：进入待商家处理
        afterSales.setStatus(0);
        this.save(afterSales);

        order.setStatus(5); // 售后中
        orderService.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resolveAfterSales(String orderNo, boolean isAgree, String reply, BigDecimal refundAmount) {
        AfterSales afterSales = this.getOne(new LambdaQueryWrapper<AfterSales>().eq(AfterSales::getOrderNo, orderNo));
        if (afterSales == null || afterSales.getStatus() != 0) throw new RuntimeException("找不到待处理的售后记录");

        Order order = orderService.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        afterSales.setMerchantReply(reply);
        User user = userService.getById(order.getUserId());

        if (isAgree) {
            afterSales.setStatus(1);
            afterSales.setRefundAmount(refundAmount);
            order.setStatus(4); // 订单关闭
            order.setCloseTime(LocalDateTime.now());

            // 如果订单之前已经确认收货（发过积分了），则需要扣回积分
            if (order.getReceiveTime() != null) {
                int deductPoints = order.getPayAmount().multiply(new BigDecimal("100")).intValue();
                if (user.getPoints() >= deductPoints) {
                    user.setPoints(user.getPoints() - deductPoints);
                } else {
                    // 积分不足，从退款金额里扣除现金
                    int missingPoints = deductPoints - user.getPoints();
                    BigDecimal deductMoney = new BigDecimal(missingPoints).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    user.setPoints(0);
                    afterSales.setRefundAmount(refundAmount.subtract(deductMoney).max(BigDecimal.ZERO));
                }
            }

            // 恢复库存
            orderItemService.restoreInventoryByOrderId(order.getId());

        } else {
            afterSales.setStatus(2); // 拒绝
            // 恢复订单原本的状态 (根据是否有发货时间判断)
            order.setStatus(order.getDeliveryTime() == null ? 1 : (order.getReceiveTime() == null ? 2 : 3));

            // 惩罚机制：如果是恶意退款被拒绝，可选择扣除信誉分
            user.setCreditScore(Math.max(0, user.getCreditScore() - 5));
        }

        userService.updateById(user);
        this.updateById(afterSales);
        orderService.updateById(order);
    }
}