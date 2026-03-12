package com.TsukasaChan.ShopVault.controller.marketing;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.entity.marketing.Activity;
import com.TsukasaChan.ShopVault.service.marketing.ActivityService;
import com.TsukasaChan.ShopVault.service.marketing.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController extends BaseController {

    private final ActivityService activityService;
    private final UserCouponService userCouponService; // 注入新生成的 Service

    /**
     * 积分商城：兑换商品 (零元购)
     * @param activityId 活动ID (sms_activity表中的主键)
     */
    @PostMapping("/exchange/{activityId}")
    public Result<String> exchangeProduct(@PathVariable Long activityId) {
        String orderNo = activityService.exchangeProduct(getCurrentUserId(), activityId);
        return Result.success("积分兑换成功！已为您生成发货订单，单号：" + orderNo);
    }

    /**
     * 获取当前所有正在进行中且类型为“派发优惠券”的活动 (前端首页/领券中心调用)
     */
    @GetMapping("/coupons/available")
    public Result<List<Activity>> getAvailableCoupons() {
        return Result.success(activityService.getAvailableCoupons());
    }

    /**
     * 用户领取优惠券
     */
    @PostMapping("/coupons/claim/{activityId}")
    public Result<String> claimCoupon(@PathVariable Long activityId) {
        Long userId = getCurrentUserId();
        userCouponService.claimCoupon(userId, activityId);
        return Result.success("优惠券领取成功，快去下单使用吧！");
    }
}