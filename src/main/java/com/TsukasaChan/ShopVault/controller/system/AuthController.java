package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.common.LoginDto;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.security.JwtUtils;
import com.TsukasaChan.ShopVault.service.system.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    // 移除了 PasswordEncoder，因为加密逻辑已经转移到了 UserServiceImpl 里

    // 登录接口
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDto loginDto) {
        // 1. Spring Security 自动校验用户名密码 (失败会抛异常)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        // 2. 校验通过，生成 Token
        String token = jwtUtils.generateToken(loginDto.getUsername());
        return Result.success(token);
    }

    // 注册接口
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        // 直接调用 Service 层的注册逻辑 (包含了查重、加密、赋初始值和保存)
        userService.register(user);
        return Result.success("注册成功");
    }
}
