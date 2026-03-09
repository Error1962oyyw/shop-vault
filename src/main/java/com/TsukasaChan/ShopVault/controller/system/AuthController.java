package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.LoginDto;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.security.JwtUtils;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.TsukasaChan.ShopVault.service.system.VerificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final VerificationService verificationService;
    private final PasswordEncoder passwordEncoder;

    @Data
    public static class ResetPasswordDto {
        private String target; // 手机号或邮箱
        private String code;   // 验证码
        private String newPassword; // 新密码
    }

    /**
     * 1. 移动端/商城前台登录 (普通用户)
     */
    @LogOperation(module = "系统安全", action = "用户前台登录")
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
    @LogOperation(module = "系统安全", action = "管理员后台登录")
    @PostMapping("/admin/login")
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
    @LogOperation(module = "系统安全", action = "新用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        userService.register(user);
        return Result.success("注册成功");
    }

    /**
     * 发送找回密码验证码
     */
    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestParam String target) {
        // 检查该手机号/邮箱是否在系统中注册过
        long count = userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, target).or().eq(User::getEmail, target));
        if (count == 0) {
            return Result.error(404, "该手机号或邮箱未注册");
        }

        verificationService.sendVerificationCode(target);
        return Result.success("验证码已发送至绑定邮箱，请注意查收");
    }

    /**
     * 验证并重置密码
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody ResetPasswordDto dto) {
        // 1. 校验验证码
        if (!verificationService.verifyCode(dto.getTarget(), dto.getCode())) {
            return Result.error(400, "验证码错误或已过期");
        }

        // 2. 查找用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getTarget()).or().eq(User::getEmail, dto.getTarget()));

        if (user == null) {
            return Result.error(404, "找不到对应用户");
        }

        // 3. 修改密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userService.updateById(user);

        return Result.success("密码重置成功，请使用新密码登录");
    }
}