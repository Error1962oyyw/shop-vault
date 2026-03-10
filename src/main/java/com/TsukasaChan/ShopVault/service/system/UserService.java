package com.TsukasaChan.ShopVault.service.system;

import com.TsukasaChan.ShopVault.entity.system.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    void registerWithEmail(String email, String password);
}
