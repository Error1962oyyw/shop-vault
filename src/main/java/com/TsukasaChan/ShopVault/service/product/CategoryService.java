package com.TsukasaChan.ShopVault.service.product;

import com.TsukasaChan.ShopVault.entity.product.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> listCategories();
}