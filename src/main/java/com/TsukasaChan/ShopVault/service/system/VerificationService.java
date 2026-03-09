package com.TsukasaChan.ShopVault.service.system;

public interface VerificationService {
    /**
     * 发送验证码
     */
    void sendVerificationCode(String email);

    /**
     * 校验验证码
     */
    boolean verifyCode(String email, String inputCode);
}