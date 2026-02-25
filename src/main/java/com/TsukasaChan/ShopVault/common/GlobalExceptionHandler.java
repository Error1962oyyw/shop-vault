package com.TsukasaChan.ShopVault.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// 注意如果是旧版 Security 可能是 org.springframework.security.access.AccessDeniedException
// 为了保险，我们可以把这两个异常都拦截下来

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ★ 新增：专门拦截 Spring Security 的“权限不足”异常
     */
    @ExceptionHandler({AuthorizationDeniedException.class, org.springframework.security.access.AccessDeniedException.class})
    public Result<String> handleAccessDeniedException(Exception e) {
        log.warn("权限拦截: {}", e.getMessage());
        return Result.error(403, "对不起，您的权限不足，无法访问该接口！");
    }

    /**
     * 兜底拦截其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统发生异常: ", e);
        return Result.error(500, "服务器开小差了，请稍后再试: " + e.getMessage());
    }
}