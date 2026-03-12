package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.dto.PasswordUpdateDto;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder; // 用于密码加密和校验

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
        userService.updateProfile(getCurrentUserId(), updateInfo);
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