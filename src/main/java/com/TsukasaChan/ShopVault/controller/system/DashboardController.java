package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.dto.DashboardStatDto;
import com.TsukasaChan.ShopVault.manager.DashboardManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardManager dashboardService;

    /**
     * 获取后台大屏统计数据 (仅限管理员)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public Result<DashboardStatDto> getStats() {
        return Result.success(dashboardService.getDashboardStats());
    }
}