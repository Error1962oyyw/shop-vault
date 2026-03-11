package com.TsukasaChan.ShopVault.controller.marketing;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.marketing.PointsRecordService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketing")
@RequiredArgsConstructor
public class MarketingController {

    private final UserService userService;
    private final PointsRecordService pointsRecordService;

    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    /**
     * 每日签到获取积分
     */
    @PostMapping("/sign-in")
    public Result<String> signIn() {
        // getCurrentUserId() 记得保留
        String msg = pointsRecordService.signIn(getCurrentUserId());
        return Result.success(msg);
    }
}