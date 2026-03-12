package com.TsukasaChan.ShopVault.service.marketing.impl;

import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.marketing.PointsRecord;
import com.TsukasaChan.ShopVault.service.marketing.PointsRecordService;
import com.TsukasaChan.ShopVault.mapper.marketing.PointsRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements PointsRecordService {

    private final StringRedisTemplate redisTemplate;
    private final UserService userService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String signIn(Long userId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String redisKey = "SignIn:" + today + ":" + userId;

        Boolean isFirstSignIn = redisTemplate.opsForValue().setIfAbsent(redisKey, "signed", 24, TimeUnit.HOURS);

        if (Boolean.TRUE.equals(isFirstSignIn)) {
            User user = userService.getById(userId);
            user.setPoints(user.getPoints() + 10);
            userService.updateById(user);
            return "签到成功！获得 10 积分，当前总积分：" + user.getPoints();
        } else {
            throw new RuntimeException("您今天已经签到过了，明天再来吧！");
        }
    }
}