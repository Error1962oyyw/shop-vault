package com.TsukasaChan.ShopVault.service.product.impl;

import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.mapper.product.ProductMapper;
import com.TsukasaChan.ShopVault.service.product.ProductService;
import com.TsukasaChan.ShopVault.service.system.UserBehaviorService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final UserBehaviorService userBehaviorService;
    private final UserService userService;

    @Override
    public Page<Product> getProductPage(Integer current, Integer size, String keyword, Long categoryId) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (StringUtils.hasText(keyword)) wrapper.like(Product::getName, keyword);
        if (categoryId != null) wrapper.eq(Product::getCategoryId, categoryId);

        wrapper.orderByDesc(Product::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Product getProductDetailWithBehavior(Long id, String username) {
        Product product = this.getById(id);
        if (product == null || product.getStatus() == 0) {
            throw new RuntimeException("商品不存在或已下架");
        }

        // 如果登录了就记录“点击”行为
        if (username != null && !username.equals("anonymousUser")) {
            User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            if (user != null) {
                userBehaviorService.recordBehavior(user.getId(), id, 1);
            }
        }
        return product;
    }
}