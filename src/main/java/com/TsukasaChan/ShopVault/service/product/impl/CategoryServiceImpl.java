package com.TsukasaChan.ShopVault.service.product.impl;

import com.TsukasaChan.ShopVault.entity.product.Category;
import com.TsukasaChan.ShopVault.mapper.product.CategoryMapper;
import com.TsukasaChan.ShopVault.service.product.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    // @Cacheable 的意思是：先去 Redis 里找 key 叫 "categoryTree" 的数据，
    // 如果有，直接返回；如果没有，执行下面的 SQL，并把结果存入 Redis！
    @Override
    @Cacheable(value = "categoryCache", key = "'categoryTree'")
    public List<Category> listCategories() {
        return this.list(); // 这里可以进一步处理成树形结构
    }
}