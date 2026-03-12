package com.TsukasaChan.ShopVault.controller.product;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.manager.RecommendationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendation")
@RequiredArgsConstructor
public class RecommendationController extends BaseController {

    private final RecommendationEngine recommendationService;

    @GetMapping("/guess-you-like")
    public Result<List<Product>> guessYouLike(@RequestParam(defaultValue = "10") int count) {
        // 调用 BaseController 的宽容模式：游客拿到的是 null，登录用户拿到的是真实 ID
        Long userId = getOptionalUserId();

        // CF算法如果收到 null，会自动走热销兜底逻辑，非常完美！
        List<Product> products = recommendationService.getRecommendationsForUser(userId, count);
        return Result.success(products);
    }
}