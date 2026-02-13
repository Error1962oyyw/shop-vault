package com.TsukasaChan.ShopVault.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.service.product.ProductService;
import com.TsukasaChan.ShopVault.mapper.product.ProductMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【pms_product(商品表)】的数据库操作Service实现
* @createDate 2026-02-13 20:37:02
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

}




