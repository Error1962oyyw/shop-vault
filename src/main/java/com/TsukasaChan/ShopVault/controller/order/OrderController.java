package com.TsukasaChan.ShopVault.controller.order;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.dto.BuyNowDto;
import com.TsukasaChan.ShopVault.dto.CartCheckoutDto;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    // 1. 立即购买 (详情页)
    @LogOperation(module = "订单交易", action = "直接下单购买")
    @PostMapping("/buy-now")
    public Result<String> buyNow(@RequestBody BuyNowDto dto) {
        String orderNo = orderService.buyNow(getCurrentUserId(), dto.getProductId(), dto.getQuantity(), dto.getUserCouponId());
        return Result.success("下单成功！单号：" + orderNo);
    }

    // 2. 购物车结算
    @LogOperation(module = "订单交易", action = "购物车批量结算")
    @PostMapping("/cart-checkout")
    public Result<String> cartCheckout(@RequestBody CartCheckoutDto dto) {
        String orderNo = orderService.cartCheckout(getCurrentUserId(), dto.getCartItemIds(), dto.getUserCouponId());
        return Result.success("购物车结算成功！单号：" + orderNo);
    }

    // 3. 模拟付款 (状态 0 -> 1)
    @PostMapping("/pay/{orderNo}")
    public Result<String> payOrder(@PathVariable String orderNo) {
        orderService.payOrder(orderNo, getCurrentUserId());
        return Result.success("支付成功！");
    }

    // 4. 模拟商家发货 (状态 1 -> 2) - 仅限管理员
    @LogOperation(module = "订单管理", action = "商家发货")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ship/{orderNo}")
    public Result<String> shipOrder(@PathVariable String orderNo) {
        orderService.shipOrder(orderNo);
        return Result.success("发货成功！");
    }

    // 5. 确认收货并领积分 (状态 2 -> 3)
    @PostMapping("/receive/{orderNo}")
    public Result<String> receiveOrder(@PathVariable String orderNo) {
        orderService.confirmReceive(orderNo, getCurrentUserId());
        return Result.success("收货成功，100倍积分已到账！");
    }

    /**
     * 延长收货时间
     */
    @PostMapping("/extend/{orderNo}")
    public Result<String> extendReceiveTime(@PathVariable String orderNo) {
        orderService.extendReceiveTime(orderNo, getCurrentUserId());
        return Result.success("已成功延长收货时间5天");
    }

    /**
     * 取消未付款的订单
     */
    @LogOperation(module = "订单交易", action = "用户取消未付款订单")
    @PostMapping("/cancel/{orderNo}")
    public Result<String> cancelOrder(@PathVariable String orderNo) {
        orderService.cancelOrder(orderNo, getCurrentUserId());
        return Result.success("订单已取消");
    }
}