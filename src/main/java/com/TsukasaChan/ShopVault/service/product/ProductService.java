package com.TsukasaChan.ShopVault.service.product;

import com.TsukasaChan.ShopVault.entity.product.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


public interface ProductService extends IService<Product> {
    Page<Product> getProductPage(Integer current, Integer size, String keyword, Long categoryId);

    Product getProductDetailWithBehavior(Long id, String username);
}
