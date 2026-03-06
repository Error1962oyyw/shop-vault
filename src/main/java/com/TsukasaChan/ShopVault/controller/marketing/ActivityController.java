package com.TsukasaChan.ShopVault.controller.marketing;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.marketing.ActivityService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final UserService userService;

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
}
