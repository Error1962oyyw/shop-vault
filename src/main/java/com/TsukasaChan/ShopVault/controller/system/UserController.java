package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder; // 用于密码加密和校验

    @Data
    public static class PasswordUpdateDto {
        private String oldPassword;
        private String newPassword;
    }

    // 获取当前登录用户
    private User getCurrentUser() {
        return userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, SecurityUtils.getCurrentUsername()));
    }

    /**
     * 1. 获取个人资料
     */
    @GetMapping("/profile")
    public Result<User> getProfile() {
        User user = getCurrentUser();
        user.setPassword(null); // 安全起见，不要把密码哈希传给前端
        return Result.success(user);
    }

    /**
     * 2. 修改个人资料 (限定可改字段)
     */

    @LogOperation(module = "个人中心", action = "修改个人资料")
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody User updateInfo) {
        User user = getCurrentUser();

        // 仅允许更新非敏感字段
        if (updateInfo.getNickname() != null) {
            user.setNickname(updateInfo.getNickname());
        }
        if (updateInfo.getAvatar() != null) {
            user.setAvatar(updateInfo.getAvatar());
        }
        // 如果修改了邮箱或手机号，真实项目中这里可能需要重新验证，毕设中可以直接放行
        if (updateInfo.getEmail() != null) {
            user.setEmail(updateInfo.getEmail());
        }
        if (updateInfo.getPhone() != null) {
            user.setPhone(updateInfo.getPhone());
        }

        // 注意：千万不要把 updateInfo 直接传给 updateById，
        // 必须像上面这样，把允许修改的值赋给从数据库查出来的 user 对象，然后再 update，
        // 这样可以防止恶意用户在请求体中构造 {"points": 99999} 来篡改积分。
        userService.updateById(user);

        return Result.success("资料修改成功");
    }

    /**
     * 3. 修改密码
     */
    @LogOperation(module = "个人中心", action = "修改密码")
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody PasswordUpdateDto dto) {
        User user = getCurrentUser();

        // 校验旧密码是否正确
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return Result.error(400, "原密码错误");
        }

        // 加密新密码并保存
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userService.updateById(user);

        // 注意：实际项目中，改完密码可能需要清空当前 Token，强制用户重新登录
        return Result.success("密码修改成功，请牢记新密码");
    }
}