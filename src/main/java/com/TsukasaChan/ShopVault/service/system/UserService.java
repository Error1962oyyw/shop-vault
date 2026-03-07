package com.TsukasaChan.ShopVault.service.system;

import com.TsukasaChan.ShopVault.entity.system.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Error1962
* @description 针对表【sys_user(用户表)】的数据库操作Service
* @createDate 2026-03-08 02:40:27
*/
public interface UserService extends IService<User> {
    void register(User user);
}
