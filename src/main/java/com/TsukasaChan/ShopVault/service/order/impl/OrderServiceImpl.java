package com.TsukasaChan.ShopVault.service.order.impl;

import cn.hutool.core.util.IdUtil;
import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.entity.order.OrderItem;
import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.mapper.order.OrderMapper;
import com.TsukasaChan.ShopVault.service.order.CartItemService;
import com.TsukasaChan.ShopVault.service.order.OrderItemService;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.product.ProductService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ProductService productService;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final CartItemService cartItemService;

    // 校验：限制用户最多只能有 3 个未付款订单，防止恶意占库存
    private void checkUnpaidLimit(Long userId) {
        long unpaidCount = this.count(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .eq(Order::getStatus, 0));
        if (unpaidCount >= 3) {
            throw new RuntimeException("您有太多未付款的订单，请先支付或取消后再下单！");
        }
    }

    // 内部通用生成订单号及处理会员日逻辑的方法
    private Order buildOrder(Long userId, BigDecimal totalAmount, String orderNo) {
        BigDecimal payAmount = totalAmount;
        boolean isMemberDay = (LocalDate.now().getDayOfMonth() % 10 == 8);
        if (isMemberDay) {
            payAmount = totalAmount.multiply(new BigDecimal("0.8")).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setStatus(0); // ★ 0: 待付款
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String buyNow(Long userId, Long productId, Integer quantity) {
        checkUnpaidLimit(userId);
        String orderNo = IdUtil.getSnowflakeNextIdStr();

        Product product = productService.getById(productId);
        if (product == null || product.getStatus() == 0) throw new RuntimeException("商品已下架");
        if (product.getStock() < quantity) throw new RuntimeException("库存不足");

        product.setStock(product.getStock() - quantity);
        productService.updateById(product);

        BigDecimal totalAmount = product.getPrice().multiply(new BigDecimal(quantity));

        OrderItem item = new OrderItem();
        item.setOrderNo(orderNo);
        item.setProductId(productId);
        item.setProductName(product.getName());
        item.setProductImg(product.getMainImage());
        item.setProductPrice(product.getPrice());
        item.setQuantity(quantity);

        Order order = buildOrder(userId, totalAmount, orderNo);
        this.save(order);

        item.setOrderId(order.getId());
        orderItemService.save(item);

        return orderNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cartCheckout(Long userId, List<Long> cartItemIds) {
        checkUnpaidLimit(userId);
        if (cartItemIds == null || cartItemIds.isEmpty()) throw new RuntimeException("未选择商品");

        List<CartItem> cartItems = cartItemService.listByIds(cartItemIds);
        String orderNo = IdUtil.getSnowflakeNextIdStr();
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("商品 [" + product.getName() + "] 库存不足");
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
            productService.updateById(product);

            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem item = new OrderItem();
            item.setOrderNo(orderNo);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductPrice(product.getPrice());
            item.setQuantity(cartItem.getQuantity());
            orderItems.add(item);
        }

        Order order = buildOrder(userId, totalAmount, orderNo);
        this.save(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
        }
        orderItemService.saveBatch(orderItems);

        // ★ 核心逻辑：结算后清空对应的购物车商品
        cartItemService.removeByIds(cartItemIds);

        return orderNo;
    }

    @Override
    public void payOrder(String orderNo, Long userId) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo).eq(Order::getUserId, userId));
        if (order != null && order.getStatus() == 0) {
            order.setStatus(1); // 1: 待发货
            this.updateById(order);
        }
    }

    @Override
    public void shipOrder(String orderNo) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order != null && order.getStatus() == 1) {
            order.setStatus(2); // 2: 待收货
            this.updateById(order);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(String orderNo, Long userId) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo).eq(Order::getUserId, userId));
        if (order == null || order.getStatus() != 2) throw new RuntimeException("订单状态不支持确认收货");

        order.setStatus(3); // 3: 已完成
        this.updateById(order);

        // ★ 修改比例：实付款 1元 = 100积分
        int rewardPoints = order.getPayAmount().multiply(new BigDecimal("100")).intValue();
        if (rewardPoints > 0) {
            User user = userService.getById(userId);
            user.setPoints(user.getPoints() + rewardPoints);
            userService.updateById(user);
        }
    }

    @Override
    public void applyAfterSales(String orderNo, Long userId) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo).eq(Order::getUserId, userId));
        // 允许收货前后申请售后
        if (order != null && (order.getStatus() == 2 || order.getStatus() == 3)) {
            order.setStatus(5); // 5: 售后中
            this.updateById(order);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resolveAfterSales(String orderNo, boolean isRefund) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null || order.getStatus() != 5) return;

        if (isRefund) {
            order.setStatus(4); // 售后同意退款，订单关闭
            // ★ 如果已经发过积分了(说明之前确认过收货)，则扣回 1:100 的积分
            int deductPoints = order.getPayAmount().multiply(new BigDecimal("100")).intValue();
            User user = userService.getById(order.getUserId());
            user.setPoints(Math.max(0, user.getPoints() - deductPoints)); // 保证积分不为负数
            userService.updateById(user);
            // 进阶：这里可以加上恢复库存的代码
        } else {
            order.setStatus(3); // 售后拒绝或只是换货，订单流转回已完成
        }
        this.updateById(order);
    }
}