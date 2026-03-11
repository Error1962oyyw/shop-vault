package com.TsukasaChan.ShopVault.dto;

import lombok.Data;

@Data
public class PasswordUpdateDto {
    private String oldPassword;
    private String newPassword;
}
