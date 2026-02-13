package com.TsukasaChan.ShopVault.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.product.Category;
import com.TsukasaChan.ShopVault.service.product.CategoryService;
import com.TsukasaChan.ShopVault.mapper.product.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【pms_category(商品分类表)】的数据库操作Service实现
* @createDate 2026-02-13 20:37:02
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




