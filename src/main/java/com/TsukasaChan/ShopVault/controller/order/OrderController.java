package com.TsukasaChan.ShopVault.controller.order;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    private Long getCurrentUserId() {
        String username = SecurityUtils.getCurrentUsername();
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username)).getId();
    }

    /**
     * DTO 内部类，用于接收前端传来的下单数据
     */
    @Data
    public static class SubmitOrderDto {
        private List<Long> productIds;
        private List<Integer> quantities;
    }

    /**
     * 提交订单
     */
    @PostMapping("/submit")
    public Result<String> submitOrder(@RequestBody SubmitOrderDto dto) {
        if (dto.getProductIds() == null || dto.getProductIds().size() != dto.getQuantities().size()) {
            return Result.error(400, "商品数据异常");
        }

        Long userId = getCurrentUserId();
        // 调用我们刚刚写好的高能事务方法！
        String orderNo = orderService.createOrder(userId, dto.getProductIds(), dto.getQuantities());

        return Result.success("下单成功，订单号：" + orderNo);
    }
}