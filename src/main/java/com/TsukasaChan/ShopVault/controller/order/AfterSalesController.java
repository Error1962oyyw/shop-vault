package com.TsukasaChan.ShopVault.controller.order;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.dto.ResolveDto;
import com.TsukasaChan.ShopVault.entity.order.AfterSales;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.order.AfterSalesService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/after-sales")
@RequiredArgsConstructor
public class AfterSalesController {

    private final AfterSalesService afterSalesService;
    private final UserService userService;

    // 获取当前登录用户ID
    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    /**
     * 1. 用户端：申请售后
     */
    @LogOperation(module = "售后服务", action = "用户提交售后申请")
    @PostMapping("/apply")
    public Result<String> applyAfterSales(@RequestBody AfterSales afterSales) {
        if (afterSales.getOrderNo() == null || afterSales.getReason() == null) {
            return Result.error(400, "订单号和售后原因不能为空");
        }
        afterSalesService.applyAfterSales(afterSales, getCurrentUserId());
        return Result.success("售后申请已提交，请等待商家处理");
    }

    /**
     * 2. 用户端：查看我的售后记录
     */
    @GetMapping("/my-list")
    public Result<List<AfterSales>> getMyAfterSalesList() {
        List<AfterSales> list = afterSalesService.list(new LambdaQueryWrapper<AfterSales>()
                .eq(AfterSales::getUserId, getCurrentUserId())
                .orderByDesc(AfterSales::getCreateTime));
        return Result.success(list);
    }

    // --- 以下为管理员接口 ---
    /**
     * 3. 管理端：审核并处理售后申请
     */
    @LogOperation(module = "售后服务", action = "商家审核退款")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/resolve")
    public Result<String> resolveAfterSales(@RequestBody ResolveDto dto) {
        if (dto.getOrderNo() == null || dto.getIsAgree() == null) {
            return Result.error(400, "处理参数不完整");
        }

        // 默认退款金额为0
        BigDecimal refund = dto.getRefundAmount() != null ? dto.getRefundAmount() : BigDecimal.ZERO;

        afterSalesService.resolveAfterSales(dto.getOrderNo(), dto.getIsAgree(), dto.getMerchantReply(), refund);

        String msg = dto.getIsAgree() ? "已同意退款并扣除对应积分" : "已拒绝该售后申请";
        return Result.success("售后处理完毕：" + msg);
    }

    /**
     * 4. 管理端：查看所有售后申请
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-list")
    public Result<List<AfterSales>> getAllAfterSalesList() {
        List<AfterSales> list = afterSalesService.list(new LambdaQueryWrapper<AfterSales>()
                .orderByAsc(AfterSales::getStatus) // 待处理的排在前面
                .orderByDesc(AfterSales::getCreateTime));
        return Result.success(list);
    }
}