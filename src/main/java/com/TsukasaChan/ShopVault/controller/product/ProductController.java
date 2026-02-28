package com.TsukasaChan.ShopVault.controller.product;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.entity.product.Category;
import com.TsukasaChan.ShopVault.service.product.CategoryService;
import com.TsukasaChan.ShopVault.service.product.ProductService;
import com.TsukasaChan.ShopVault.service.system.YoloClientService;
import com.TsukasaChan.ShopVault.service.system.YoloMappingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final YoloClientService yoloClientService;
    private final YoloMappingService yoloMappingService;
    private final CategoryService categoryService;

    /**
     * 1. 发布商品 (仅限管理员)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/publish")
    public Result<String> publishProduct(@RequestBody Product product) {
        if (!StringUtils.hasText(product.getName()) || product.getPrice() == null) {
            return Result.error(400, "商品名称和价格不能为空");
        }

        product.setStatus(1); // 默认上架状态
        product.setSales(0);  // 初始销量0
        productService.save(product);

        return Result.success("商品发布成功！");
    }

    /**
     * 2. 分页获取商品列表
     */
    @GetMapping("/list")
    public Result<Page<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {

        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Product::getStatus, 1);

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }

        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }

        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> productPage = productService.page(page, wrapper);
        return Result.success(productPage);
    }

    /**
     * 3. AI视觉检索 (粗筛+细选逻辑)
     */
    @PostMapping("/yolo-search")
    public Result<List<Category>> yoloSearch(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "请上传图片");
        }

        // 1. 调用 Python YOLO 获取英文标签
        List<String> labels = yoloClientService.detectImage(file);
        if (labels.isEmpty()) {
            return Result.error(404, "抱歉，AI没能在图中找到认识的商品哦");
        }

        // 2. 根据标签去查对应的系统分类 ID (比如查出 [1, 2])
        List<Long> categoryIds = yoloMappingService.findCategoryIdsByLabels(labels);
        if (categoryIds.isEmpty()) {
            return Result.error(404, "图片中识别出了: " + labels + "，但目前商城没有相关的商品分类");
        }

        // 3. 重构点：拿着这些 ID，去分类表里查出具体的分类信息 (分类名称、图标等)
        List<Category> categories = categoryService.listByIds(categoryIds);

        // 4. 返回分类列表给前端，让用户做选择
        return Result.success(categories);
    }
}
