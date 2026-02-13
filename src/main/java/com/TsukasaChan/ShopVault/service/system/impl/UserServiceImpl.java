package com.TsukasaChan.ShopVault.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.TsukasaChan.ShopVault.mapper.system.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【sys_user(用户表)】的数据库操作Service实现
* @createDate 2026-02-13 20:45:10
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




