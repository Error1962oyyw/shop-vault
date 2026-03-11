package com.TsukasaChan.ShopVault.dto;

import lombok.Data;

@Data
public class EmailRegisterDto {
    private String email;
    private String password;
    private String code; // 邮箱验证码
}
