package com.TsukasaChan.ShopVault.service.system;

import com.TsukasaChan.ShopVault.entity.system.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    void registerWithEmail(String email, String password);

    void updateProfile(Long userId, User updateInfo);

    void updatePassword(Long userId, String oldPassword, String newPassword);

    void resetPassword(String email, String code, String newPassword);
}
