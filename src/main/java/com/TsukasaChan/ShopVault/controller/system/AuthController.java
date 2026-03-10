package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.infrastructure.VerificationService;
import com.TsukasaChan.ShopVault.security.JwtUtils;
import com.TsukasaChan.ShopVault.service.system.UserService;
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
    public static class EmailLoginDto {
        private String email;
        private String password;
    }

    @Data
    public static class EmailRegisterDto {
        private String email;
        private String password;
        private String code; // 邮箱验证码
    }

    @Data
    public static class ResetPasswordDto {
        private String email;
        private String code;
        private String newPassword;
    }

    @LogOperation(module = "系统安全", action = "用户前台登录")
    @PostMapping("/login")
    public Result<String> userLogin(@RequestBody EmailLoginDto dto) {
        // 由于 UserDetailsServiceImpl 已经支持邮箱匹配，这里直接传入 email 即可
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return Result.error(403, "管理员请从后台入口登录！");

        // Token 中存入真实的 username (从认证成功的用户信息中提取)
        String realUsername = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        return Result.success(jwtUtils.generateToken(realUsername));
    }

    @LogOperation(module = "系统安全", action = "管理员后台登录")
    @PostMapping("/admin/login")
    public Result<String> adminLogin(@RequestBody EmailLoginDto dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) return Result.error(403, "权限不足，非管理员账号！");

        String realUsername = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        return Result.success(jwtUtils.generateToken(realUsername));
    }

    /**
     * 发送验证码 (注册和找回密码通用)
     */
    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestParam String email) {
        verificationService.sendVerificationCode(email);
        return Result.success("验证码已发送至邮箱，请注意查收");
    }

    /**
     * 邮箱验证码注册
     */
    @LogOperation(module = "系统安全", action = "新用户邮箱注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody EmailRegisterDto dto) {
        if (!verificationService.verifyCode(dto.getEmail(), dto.getCode())) {
            return Result.error(400, "验证码错误或已过期");
        }
        userService.registerWithEmail(dto.getEmail(), dto.getPassword());
        return Result.success("注册成功！");
    }

    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestBody ResetPasswordDto dto) {
        if (!verificationService.verifyCode(dto.getEmail(), dto.getCode())) {
            return Result.error(400, "验证码错误或已过期");
        }

        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getEmail()));
        if (user == null) return Result.error(404, "该邮箱尚未注册");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userService.updateById(user);
        return Result.success("密码重置成功，请重新登录");
    }
}