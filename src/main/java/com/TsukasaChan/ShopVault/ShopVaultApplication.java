package com.TsukasaChan.ShopVault;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.TsukasaChan.ShopVault.mapper") //扫描所有mapper包
public class ShopVaultApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopVaultApplication.class, args);
    }
}
