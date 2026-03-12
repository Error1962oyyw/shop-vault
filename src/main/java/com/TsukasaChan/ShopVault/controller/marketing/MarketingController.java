package com.TsukasaChan.ShopVault.controller.marketing;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.service.marketing.PointsRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketing")
@RequiredArgsConstructor
public class MarketingController extends BaseController {

    private final PointsRecordService pointsRecordService;

    /**
     * 每日签到获取积分
     */
    @PostMapping("/sign-in")
    public Result<String> signIn() {
        String msg = pointsRecordService.signIn(getCurrentUserId());
        return Result.success(msg);
    }
}