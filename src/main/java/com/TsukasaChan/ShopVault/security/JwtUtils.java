package com.TsukasaChan.ShopVault.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.nio.charset.StandardCharsets;

@Component
public class JwtUtils {

    @Value("${shop-vault.jwt.secret}")
    private String secret;

    @Value("${shop-vault.jwt.expiration}")
    private long expiration;

    // 生成 Key (修复 Base64 解码报错)
    private SecretKey getSignInKey() {
        // 原来的错误写法：byte[] keyBytes = Decoders.BASE64.decode(secret);
        // 现在的正确写法：直接把普通字符串转为 UTF-8 字节数组
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 生成 Token
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256) // JJWT 0.12+ 写法
                .compact();
    }

    // 从 Token 中获取用户名
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 解析 Token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 验证 Token 是否有效
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}