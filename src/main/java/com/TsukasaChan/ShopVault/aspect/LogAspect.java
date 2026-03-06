package com.TsukasaChan.ShopVault.aspect;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.Log;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.LogService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    private final LogService sysLogService;
    private final UserService userService;

    // 拦截所有带有 @LogOperation 注解的方法，在方法成功返回后执行
    @AfterReturning(pointcut = "@annotation(com.TsukasaChan.ShopVault.annotation.LogOperation)")
    public void recordLog(JoinPoint joinPoint) {
        try {
            // 获取注解上的模块和动作说明
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogOperation logAnnotation = method.getAnnotation(LogOperation.class);

            // 获取当前请求的 IP 地址
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
            String ip = request != null ? request.getRemoteAddr() : "未知IP";

            // 获取当前登录人
            String username = SecurityUtils.getCurrentUsername();
            User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

            // 封装实体并保存
            Log sysLog = new Log();
            if (user != null) {
                sysLog.setUserId(user.getId());
                sysLog.setUsername(user.getUsername());
                sysLog.setRole(user.getRole());
            } else {
                sysLog.setUsername("未登录用户");
            }
            sysLog.setModule(logAnnotation.module());
            sysLog.setAction(logAnnotation.action());
            sysLog.setIpAddress(ip);

            sysLogService.save(sysLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }
}