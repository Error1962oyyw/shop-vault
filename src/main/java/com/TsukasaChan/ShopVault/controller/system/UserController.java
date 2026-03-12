package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.dto.PasswordUpdateDto;
import com.TsukasaChan.ShopVault.entity.system.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    @GetMapping("/profile")
    public Result<User> getProfile() {
        User user = getCurrentUser(); // 直接调用父类方法
        user.setPassword(null); // 安全起见，剔除密码
        return Result.success(user);
    }

    @LogOperation(module = "个人中心", action = "修改个人资料")
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody User updateInfo) {
        userService.updateProfile(getCurrentUserId(), updateInfo);
        return Result.success("资料修改成功");
    }

    @LogOperation(module = "个人中心", action = "修改密码")
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody PasswordUpdateDto dto) {
        userService.updatePassword(getCurrentUserId(), dto.getOldPassword(), dto.getNewPassword());
        return Result.success("密码修改成功，请牢记新密码");
    }
}