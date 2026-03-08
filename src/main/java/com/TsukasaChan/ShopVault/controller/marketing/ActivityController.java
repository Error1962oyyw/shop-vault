package com.TsukasaChan.ShopVault.controller.marketing;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.marketing.Activity;
import com.TsukasaChan.ShopVault.entity.marketing.UserCoupon;
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

        Activity activity = activityService.getById(activityId);
        if (activity == null || activity.getType() != 3 || activity.getStatus() != 1) {
            return Result.error(400, "该优惠券不存在或已下架");
        }

        // 防刷：每个人针对同一张券只能领一次
        long count = userCouponService.count(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getActivityId, activityId));
        if (count > 0) {
            return Result.error(400, "您已经领取过这张优惠券啦！");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setActivityId(activityId);
        userCoupon.setStatus(0); // 0:未使用
        userCoupon.setExpireTime(activity.getEndTime()); // 优惠券的过期时间就是活动的结束时间
        userCouponService.save(userCoupon);

        return Result.success("优惠券领取成功，快去下单使用吧！");
    }
}
