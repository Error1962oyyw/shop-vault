package com.TsukasaChan.ShopVault.controller;

import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

    // 使用 @Autowired 注入，这样子类就不需要在构造函数里传它了
    @Autowired
    protected UserService userService;

    /**
     * 全局通用的获取当前登录用户ID的方法
     * protected 修饰符保证只有子类能调用
     */
    protected Long getCurrentUserId() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null || "anonymousUser".equals(username)) {
            throw new RuntimeException("用户未登录或登录已过期");
        }
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new RuntimeException("找不到当前登录用户信息");
        }
        return user.getId();
    }
}