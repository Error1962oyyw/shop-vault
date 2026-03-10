package com.TsukasaChan.ShopVault.infrastructure;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String CODE_PREFIX = "verify:code:";
    private static final String LOCK_PREFIX = "verify:lock:"; // 用于防刷限流

    public void sendVerificationCode(String email) {
        // 1. 检查是否在 60 秒冷却期内 (防刷机制)
        if (Boolean.TRUE.equals(redisTemplate.hasKey(LOCK_PREFIX + email))) {
            throw new RuntimeException("验证码发送太频繁，请60秒后再试");
        }

        // 2. 生成 6 位纯数字验证码
        String code = RandomUtil.randomNumbers(6);

        // 3. 存入 Redis，验证码有效期 5 分钟
        redisTemplate.opsForValue().set(CODE_PREFIX + email, code, 5, TimeUnit.MINUTES);
        // 4. 存入 Redis，发送冷却锁有效期 60 秒
        redisTemplate.opsForValue().set(LOCK_PREFIX + email, "locked", 60, TimeUnit.SECONDS);

        // 5. 构建并发送真实邮件
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("【小铺宝库】账号验证码");
            message.setText("亲爱的用户您好：\n\n您正在进行身份验证，您的验证码为：【 " + code + " 】。\n" +
                    "该验证码将在 5 分钟后失效。\n\n如非本人操作，请忽略此邮件。");

            mailSender.send(message);
            log.info("验证码已成功发送至邮箱: {}", email);
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage());
            // 如果邮件发送失败，清除 Redis 里的记录，允许用户立刻重试
            redisTemplate.delete(CODE_PREFIX + email);
            redisTemplate.delete(LOCK_PREFIX + email);
            throw new RuntimeException("邮件发送异常，请稍后再试");
        }
    }

    public boolean verifyCode(String email, String inputCode) {
        String savedCode = redisTemplate.opsForValue().get(CODE_PREFIX + email);
        if (savedCode != null && savedCode.equals(inputCode)) {
            // 验证成功后立即删除验证码，防止重复使用
            redisTemplate.delete(CODE_PREFIX + email);
            return true;
        }
        return false;
    }
}