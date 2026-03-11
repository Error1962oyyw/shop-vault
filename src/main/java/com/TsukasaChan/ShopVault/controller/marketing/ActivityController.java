package com.TsukasaChan.ShopVault.controller.marketing;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.marketing.Activity;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.marketing.ActivityService;
import com.TsukasaChan.ShopVault.service.marketing.UserCouponService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final UserService userService;
    private final UserCouponService userCouponService; // 注入新生成的 Service

    // 获取当前登录用户ID
    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

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
        // 假设 type=3 代表是优惠券发放活动
        List<Activity> list = activityService.list(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getType, 3)
                .eq(Activity::getStatus, 1)
                .le(Activity::getStartTime, LocalDateTime.now())
                .ge(Activity::getEndTime, LocalDateTime.now()));
        return Result.success(list);
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