package com.TsukasaChan.ShopVault.security;

import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 2. 转换为 Spring Security 需要的 UserDetails 对象
        // 这里暂时没做复杂的权限 List，给个空的，或者根据 user.getRole() 添加 "ROLE_ADMIN"
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // 权限列表
        );
    }
}