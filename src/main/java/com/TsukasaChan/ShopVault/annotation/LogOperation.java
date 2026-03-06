package com.TsukasaChan.ShopVault.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    String module() default ""; // 操作模块，如："商品管理"
    String action() default ""; // 具体动作，如："发布商品"
}