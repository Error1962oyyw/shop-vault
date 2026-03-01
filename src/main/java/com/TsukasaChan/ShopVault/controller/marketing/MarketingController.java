package com.TsukasaChan.ShopVault.controller.marketing;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/marketing")
@RequiredArgsConstructor
public class MarketingController {

    private final UserService userService;
    private final StringRedisTemplate redisTemplate; // 使用 Redis 防止重复签到

    /**
     * 每日签到获取积分
     */
    @PostMapping("/sign-in")
    public Result<String> signIn() {
        String username = SecurityUtils.getCurrentUsername();
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 构建 Redis Key，格式：signin:日期:用户ID (例如 signin:2026-03-01:1)
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String redisKey = "signin:" + today + ":" + user.getId();

        // setIfAbsent 相当于 Redis 的 SETNX，如果键不存在则设置成功并返回 true
        Boolean isFirstSignIn = redisTemplate.opsForValue().setIfAbsent(redisKey, "signed", 24, TimeUnit.HOURS);

        if (Boolean.TRUE.equals(isFirstSignIn)) {
            // 签到成功，赠送 10 积分
            user.setPoints(user.getPoints() + 10);
            userService.updateById(user);
            return Result.success("签到成功！获得 10 积分，当前总积分：" + user.getPoints());
        } else {
            return Result.error(400, "您今天已经签到过了，明天再来吧！");
        }
    }
}