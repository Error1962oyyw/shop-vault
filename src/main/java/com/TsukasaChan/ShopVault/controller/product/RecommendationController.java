package com.TsukasaChan.ShopVault.controller.product;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.manager.RecommendationEngine;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationEngine recommendationService;
    private final UserService userService;

    @GetMapping("/guess-you-like")
    public Result<List<Product>> guessYouLike(@RequestParam(defaultValue = "10") int count) {
        String username = SecurityUtils.getCurrentUsername();
        Long userId = null;

        // 如果用户已登录，则使用 CF 算法推荐；如果是游客，依然可以查询，但会走降级逻辑（热销推荐）
        if (username != null && !username.equals("anonymousUser")) {
            User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            if (user != null) {
                userId = user.getId();
            }
        }

        // 调用协同过滤算法服务
        List<Product> products = recommendationService.getRecommendationsForUser(userId, count);
        return Result.success(products);
    }
}