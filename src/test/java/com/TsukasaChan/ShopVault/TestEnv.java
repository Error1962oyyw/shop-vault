package com.TsukasaChan.ShopVault;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestEnv {

    @Value("${spring.mail.port}")
    private String port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;

    @Test
    public void OutputEnv() {
        System.out.println(port);
        System.out.println(username);
        System.out.println(password);
    }
}
