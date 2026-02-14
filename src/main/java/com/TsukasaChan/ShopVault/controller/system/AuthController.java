// AuthController.java
package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.common.LoginDto;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.security.JwtUtils;
import com.TsukasaChan.ShopVault.service.system.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    // 登录接口
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDto loginDto) {
        // 1. Spring Security 自动校验用户名密码 (失败会抛异常)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        // 2. 校验通过，生成 Token
        // 实际项目中这里可能返回一个包含 token 和 userInfo 的对象
        String token = jwtUtils.generateToken(loginDto.getUsername());
        return Result.success(token);
    }

    // 注册接口
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        // 检查用户名是否重复 (需要在 Service 里实现 checkUsername)
        // ...

        // 1. 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 2. 初始化默认值
        user.setRole("USER");
        user.setPoints(0);
        user.setBalance(new java.math.BigDecimal("0.00"));

        // 3. 保存
        userService.save(user);
        return Result.success("注册成功");
    }
}