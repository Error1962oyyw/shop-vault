package com.TsukasaChan.ShopVault.controller;

import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    @Autowired
    protected UserService userService;

    /**
     * 获取当前登录的完整用户信息
     */
    protected User getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null || "anonymousUser".equals(username)) {
            throw new RuntimeException("用户未登录或登录已过期");
        }
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("找不到当前登录用户信息");
        }
        return user;
    }

    /**
     * 获取当前登录用户的 ID
     */
    protected Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * 宽容模式：获取当前登录用户的 ID（用于允许游客访问的接口）
     * 如果未登录，返回 null 而不是抛出异常
     */
    protected Long getOptionalUserId() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null || "anonymousUser".equals(username)) {
            return null; // 没登录就是 null
        }
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user != null ? user.getId() : null;
    }
}