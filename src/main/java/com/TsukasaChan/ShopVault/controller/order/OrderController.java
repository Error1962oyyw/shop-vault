package com.TsukasaChan.ShopVault.controller.order;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    @Data
    public static class BuyNowDto {
        private Long productId;
        private Integer quantity;
    }

    // 1. 立即购买 (详情页)
    @PostMapping("/buy-now")
    public Result<String> buyNow(@RequestBody BuyNowDto dto) {
        String orderNo = orderService.buyNow(getCurrentUserId(), dto.getProductId(), dto.getQuantity());
        return Result.success("下单成功！单号：" + orderNo);
    }

    // 2. 购物车结算
    @PostMapping("/cart-checkout")
    public Result<String> cartCheckout(@RequestBody List<Long> cartItemIds) {
        String orderNo = orderService.cartCheckout(getCurrentUserId(), cartItemIds);
        return Result.success("购物车结算成功！单号：" + orderNo);
    }

    // 3. 模拟付款 (状态 0 -> 1)
    @PostMapping("/pay/{orderNo}")
    public Result<String> payOrder(@PathVariable String orderNo) {
        orderService.payOrder(orderNo, getCurrentUserId());
        return Result.success("支付成功！");
    }

    // 4. 模拟商家发货 (状态 1 -> 2) - 仅限管理员
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

    // 6. 申请售后 (状态 2/3 -> 5)
    @PostMapping("/after-sales/apply/{orderNo}")
    public Result<String> applyAfterSales(@PathVariable String orderNo) {
        orderService.applyAfterSales(orderNo, getCurrentUserId());
        return Result.success("已提交售后申请");
    }

    // 7. 处理售后 (状态 5 -> 4 或 3) - 仅限管理员
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/after-sales/resolve/{orderNo}")
    public Result<String> resolveAfterSales(@PathVariable String orderNo, @RequestParam boolean isRefund) {
        orderService.resolveAfterSales(orderNo, isRefund);
        return Result.success("售后处理完毕！" + (isRefund ? "已退款并扣除积分" : ""));
    }
}