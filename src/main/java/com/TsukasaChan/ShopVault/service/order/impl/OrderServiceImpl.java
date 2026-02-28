package com.TsukasaChan.ShopVault.service.order.impl;

import cn.hutool.core.util.IdUtil;
import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.entity.order.OrderItem;
import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.mapper.order.OrderMapper;
import com.TsukasaChan.ShopVault.service.order.OrderItemService;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.product.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ProductService productService;
    private final OrderItemService orderItemService;

    /**
     * 创建订单的核心业务逻辑
     * @Transactional 保证发生异常时，已扣的库存会回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Long userId, List<Long> productIds, List<Integer> quantities) {
        // 1. 生成唯一订单号 (使用 Hutool 的雪花算法生成)
        String orderNo = IdUtil.getSnowflakeNextIdStr();
        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        // 2. 遍历商品，校验库存并扣减
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer quantity = quantities.get(i);

            Product product = productService.getById(productId);
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("包含不存在或已下架的商品");
            }
            if (product.getStock() < quantity) {
                throw new RuntimeException("商品 [" + product.getName() + "] 库存不足");
            }

            // 扣减库存 (简单的并发安全做法，利用数据库进行运算)
            product.setStock(product.getStock() - quantity);
            productService.updateById(product);

            // 计算总价
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(quantity));
            totalAmount = totalAmount.add(itemTotal);

            // 构建订单明细
            OrderItem item = new OrderItem();
            item.setOrderNo(orderNo);
            item.setProductId(productId);
            item.setProductName(product.getName());
            item.setProductImg(product.getMainImage());
            item.setProductPrice(product.getPrice());
            item.setQuantity(quantity);
            orderItems.add(item);
        }

        // 3. 创建主订单记录
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount); // 如果有优惠券在此处扣减
        order.setStatus(0); // 0: 待付款
        this.save(order);

        // 4. 批量保存订单明细
        // 获取刚保存的 order 的 ID (MyBatisPlus 自动回填)
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
        }
        orderItemService.saveBatch(orderItems);

        return orderNo;
    }
}