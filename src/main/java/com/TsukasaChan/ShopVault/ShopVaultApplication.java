package com.TsukasaChan.ShopVault;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.TsukasaChan.ShopVault.mapper") //扫描所有mapper包
@EnableScheduling // 开启定时任务功能
@EnableCaching
public class ShopVaultApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopVaultApplication.class, args);
    }
}
