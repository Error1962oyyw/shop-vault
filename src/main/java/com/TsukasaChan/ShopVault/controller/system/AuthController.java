package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.common.LoginDto;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.security.JwtUtils;
import com.TsukasaChan.ShopVault.service.system.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * 1. 移动端/商城前台登录 (普通用户)
     */
    @PostMapping("/login")
    public Result<String> userLogin(@RequestBody LoginDto loginDto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        // 校验：不能是管理员
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return Result.error(403, "管理员请从后台入口登录！");
        }

        return Result.success(jwtUtils.generateToken(loginDto.getUsername()));
    }

    /**
     * 2. 后台管理系统登录 (仅限管理员)
     */
    @PostMapping("/login/admin")
    public Result<String> adminLogin(@RequestBody LoginDto loginDto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        // 校验：必须是管理员
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return Result.error(403, "权限不足，非管理员账号！");
        }

        return Result.success(jwtUtils.generateToken(loginDto.getUsername()));
    }

    // 注册接口保持不变
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        userService.register(user);
        return Result.success("注册成功");
    }
}