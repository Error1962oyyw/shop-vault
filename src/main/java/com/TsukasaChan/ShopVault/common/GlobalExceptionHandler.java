package com.TsukasaChan.ShopVault.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 在控制台打印完整的错误信息，方便你排错
        log.error("系统发生异常: ", e);
        // 返回给前端友好的提示
        return Result.error(500, "服务器开小差了，请稍后再试: " + e.getMessage());
    }

    // 后续你可以添加更多的 @ExceptionHandler 来处理特定的异常（比如自定义的业务异常、Token过期异常等）
}