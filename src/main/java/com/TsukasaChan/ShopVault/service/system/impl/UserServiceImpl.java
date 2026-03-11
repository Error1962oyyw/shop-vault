package com.TsukasaChan.ShopVault.service.system.impl;

import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.mapper.system.UserMapper;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    // 新增基于邮箱的注册方法
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void registerWithEmail(String email, String password) {
        long count = this.count(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (count > 0) {
            throw new RuntimeException("该邮箱已被注册");
        }

        User user = new User();
        // ★ 系统自动生成全局唯一的 username (例如: sv_user_12345678)
        user.setUsername("sv_user_" + cn.hutool.core.util.RandomUtil.randomNumbers(8));
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // 生成一个默认昵称
        user.setNickname("小铺用户_" + cn.hutool.core.util.RandomUtil.randomString(4));
        user.setRole("USER");
        user.setPoints(0);
        user.setBalance(new BigDecimal("0.00"));
        user.setCreditScore(100); // 初始信誉分

        this.save(user);
    }

    @Override
    public void updateProfile(Long userId, User updateInfo) {
        User user = getById(userId);

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
        updateById(user);
    }
}