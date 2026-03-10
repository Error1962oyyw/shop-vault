package com.TsukasaChan.ShopVault.manager;

import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.entity.order.OrderItem;
import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.service.order.OrderItemService;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.product.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationEngine {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final ProductService productService;

    /**
     * Item-based CF: 根据用户最近购买的商品，推荐相似商品
     */
    public List<Product> getRecommendationsForUser(Long userId, int recommendCount) {
        // 1. 获取该用户购买过的所有商品ID
        List<Order> userOrders = orderService.list(new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId));
        if (userOrders.isEmpty()) {
            return getFallbackRecommendations(recommendCount); // 冷启动：如果没有购买记录，推荐热销商品
        }

        List<Long> userOrderIds = userOrders.stream().map(Order::getId).collect(Collectors.toList());
        List<OrderItem> userOrderItems = orderItemService.list(new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, userOrderIds));
        Set<Long> userPurchasedProductIds = userOrderItems.stream().map(OrderItem::getProductId).collect(Collectors.toSet());

        if (userPurchasedProductIds.isEmpty()) {
            return getFallbackRecommendations(recommendCount);
        }

        // 2. 构建商品同现矩阵 (简易版 CF 算法核心)
        // 获取所有订单详情
        List<OrderItem> allOrderItems = orderItemService.list();
        // 按订单分组，找出每个订单包含的商品
        Map<Long, List<Long>> orderToProductsMap = allOrderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId,
                        Collectors.mapping(OrderItem::getProductId, Collectors.toList())));

        // 记录商品之间同时出现的次数: Map<目标商品ID, Map<关联商品ID, 共同购买次数>>
        Map<Long, Map<Long, Integer>> itemCoOccurrenceMatrix = new HashMap<>();

        for (List<Long> productsInOrder : orderToProductsMap.values()) {
            for (Long p1 : productsInOrder) {
                itemCoOccurrenceMatrix.putIfAbsent(p1, new HashMap<>());
                for (Long p2 : productsInOrder) {
                    if (!p1.equals(p2)) {
                        Map<Long, Integer> coMap = itemCoOccurrenceMatrix.get(p1);
                        coMap.put(p2, coMap.getOrDefault(p2, 0) + 1);
                    }
                }
            }
        }

        // 3. 计算推荐得分
        Map<Long, Integer> recommendationScores = new HashMap<>();
        for (Long purchasedId : userPurchasedProductIds) {
            Map<Long, Integer> relatedProducts = itemCoOccurrenceMatrix.getOrDefault(purchasedId, new HashMap<>());
            for (Map.Entry<Long, Integer> entry : relatedProducts.entrySet()) {
                Long relatedProductId = entry.getKey();
                // 如果用户已经买过了，就不推荐了
                if (!userPurchasedProductIds.contains(relatedProductId)) {
                    recommendationScores.put(relatedProductId,
                            recommendationScores.getOrDefault(relatedProductId, 0) + entry.getValue());
                }
            }
        }

        // 4. 排序并取 Top N
        List<Long> recommendedProductIds = recommendationScores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // 按分数降序
                .limit(recommendCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (recommendedProductIds.isEmpty()) {
            return getFallbackRecommendations(recommendCount); // 如果算不出推荐，走兜底逻辑
        }

        return productService.listByIds(recommendedProductIds);
    }

    /**
     * 兜底策略：冷启动时，按销量推荐热门商品
     */
    private List<Product> getFallbackRecommendations(int limit) {
        return productService.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1) // 必须是上架的
                .orderByDesc(Product::getSales)
                .last("LIMIT " + limit));
    }
}