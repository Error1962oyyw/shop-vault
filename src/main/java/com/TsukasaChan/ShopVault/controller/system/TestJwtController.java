package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.common.Result;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于测试 JWT 和 权限角色的 Controller
 */
@RestController
@RequestMapping("/api/test")
public class TestJwtController {

    /**
     * 1. 游客接口：不需要 Token，谁都可以访问 (用来测试：看商品、看导购)
     * 注意：需要在 SecurityConfig 的 requestMatchers 中放行 "/api/test/guest"
     */
    @GetMapping("/guest")
    public Result<String> guestAccess() {
        return Result.success("【游客权限】成功！这是公开的商品和导购信息，无需登录即可查看。");
    }

    /**
     * 2. 用户接口：必须携带 Token 才能访问，普通用户和管理员都可以 (用来测试：下单、评价)
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user")
    public Result<String> userAccess() {
        return Result.success("【用户权限】成功！你已经登录，可以进行下单、评价等操作。");
    }

    /**
     * 3. 管理员接口：必须携带 Token，且数据库里 role 必须是 ADMIN 才能访问 (用来测试：上架商品、看大屏)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public Result<String> adminAccess() {
        return Result.success("【管理员权限】成功！尊贵的管理员，您可以管理商品和查看大屏数据。");
    }
}