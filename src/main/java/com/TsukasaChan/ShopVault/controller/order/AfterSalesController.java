package com.TsukasaChan.ShopVault.controller.order;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.dto.ResolveDto;
import com.TsukasaChan.ShopVault.entity.order.AfterSales;
import com.TsukasaChan.ShopVault.service.order.AfterSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/after-sales")
@RequiredArgsConstructor
public class AfterSalesController extends BaseController {

    private final AfterSalesService afterSalesService;

    @LogOperation(module = "售后服务", action = "用户提交售后申请")
    @PostMapping("/apply")
    public Result<String> applyAfterSales(@RequestBody AfterSales afterSales) {
        if (afterSales.getOrderNo() == null || afterSales.getReason() == null) {
            return Result.error(400, "订单号和售后原因不能为空");
        }
        afterSalesService.applyAfterSales(afterSales, getCurrentUserId());
        return Result.success("售后申请已提交，请等待商家处理");
    }

    @GetMapping("/my-list")
    public Result<List<AfterSales>> getMyAfterSalesList() {
        return Result.success(afterSalesService.getMyAfterSalesList(getCurrentUserId()));
    }

    @LogOperation(module = "售后服务", action = "商家审核退款")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/resolve")
    public Result<String> resolveAfterSales(@RequestBody ResolveDto dto) {
        if (dto.getOrderNo() == null || dto.getIsAgree() == null) return Result.error(400, "处理参数不完整");
        BigDecimal refund = dto.getRefundAmount() != null ? dto.getRefundAmount() : BigDecimal.ZERO;
        afterSalesService.resolveAfterSales(dto.getOrderNo(), dto.getIsAgree(), dto.getMerchantReply(), refund);
        return Result.success("售后处理完毕：" + (dto.getIsAgree() ? "已同意退款" : "已拒绝"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-list")
    public Result<List<AfterSales>> getAllAfterSalesList() {
        return Result.success(afterSalesService.getAllAfterSalesList());
    }
}