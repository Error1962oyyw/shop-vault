package com.TsukasaChan.ShopVault.service.system.impl;

import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.mapper.system.UserMapper;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    // 自定义注册方法
    public void register(User user) {
        // 1. 检查用户名是否已存在
        long count = this.count(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. 设置默认值 (数据库没有默认值的话，代码里设置最稳妥)
        user.setRole("USER"); // 默认注册为普通用户
        user.setPoints(0);
        user.setBalance(new BigDecimal("0.00"));

        // 4. 保存到数据库
        this.save(user);
    }
}