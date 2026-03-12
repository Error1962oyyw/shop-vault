package com.TsukasaChan.ShopVault.service.product.impl;

import com.TsukasaChan.ShopVault.entity.product.Category;
import com.TsukasaChan.ShopVault.mapper.product.CategoryMapper;
import com.TsukasaChan.ShopVault.service.product.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    @Cacheable(value = "categoryCache", key = "'categoryTree'")
    public List<Category> listCategories() {
        // 1. 查出所有的分类
        List<Category> allCategories = this.list();

        // 2. 组装成树形结构
        return allCategories.stream()
                // 找出所有的一级分类 (假设 parent_id 为 0)
                .filter(category -> category.getParentId() == 0)
                // 递归设置子分类
                .peek(category -> category.setChildren(getChildren(category, allCategories)))
                // 可选：按照 sort 字段排序
                .sorted((c1, c2) -> {
                    int sort1 = c1.getSort() == null ? 0 : c1.getSort();
                    int sort2 = c2.getSort() == null ? 0 : c2.getSort();
                    return Integer.compare(sort1, sort2);
                })
                .collect(Collectors.toList());
    }

    // 递归查找子节点
    private List<Category> getChildren(Category root, List<Category> allCategories) {
        return allCategories.stream()
                .filter(category -> category.getParentId().equals(root.getId()))
                .peek(category -> category.setChildren(getChildren(category, allCategories)))
                .sorted((c1, c2) -> {
                    int sort1 = c1.getSort() == null ? 0 : c1.getSort();
                    int sort2 = c2.getSort() == null ? 0 : c2.getSort();
                    return Integer.compare(sort1, sort2);
                })
                .collect(Collectors.toList());
    }
}