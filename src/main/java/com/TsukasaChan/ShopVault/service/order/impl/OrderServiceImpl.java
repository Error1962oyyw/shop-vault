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
import java.math.RoundingMode;
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
            payAmount = totalAmount.multiply(new BigDecimal("0.8")).setScale(2, RoundingMode.HALF_UP);
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
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId)); // 严格限制只能查自己的单子

        if (order == null) {
            throw new RuntimeException("订单不存在或您无权操作该订单！");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态异常，当前无法进行支付！");
        }

        order.setStatus(1); // 1: 待发货
        this.updateById(order);
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
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId));

        if (order == null) {
            throw new RuntimeException("订单不存在或您无权操作该订单！");
        }
        if (order.getStatus() == 5) {
            throw new RuntimeException("该订单正在售后处理中，请勿重复提交！");
        }
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            throw new RuntimeException("当前订单状态不支持申请售后！(仅待收货或已完成的订单可申请)");
        }

        order.setStatus(5); // 5: 售后中
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resolveAfterSales(String orderNo, boolean isRefund) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null || order.getStatus() != 5) {
            throw new RuntimeException("找不到需要处理的售后订单");
        }

        if (isRefund) {
            order.setStatus(4); // 售后同意退款，订单关闭

            // 计算当初发了多少积分 (实付金额 * 100)
            int deductPoints = order.getPayAmount().multiply(new BigDecimal("100")).intValue();
            User user = userService.getById(order.getUserId());

            if (user.getPoints() >= deductPoints) {
                // 积分够扣，直接扣除
                user.setPoints(user.getPoints() - deductPoints);
            } else {
                // ★ 进阶逻辑：积分不够扣了！把差的积分折算回钱，从退款里扣除
                int missingPoints = deductPoints - user.getPoints(); // 算算欠了多少积分
                // 100 积分 = 1 元
                BigDecimal deductMoney = new BigDecimal(missingPoints).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                user.setPoints(0); // 积分直接清零

                // 此时退给用户的钱 = 订单实付 - 抵扣掉的钱
                BigDecimal actualRefund = order.getPayAmount().subtract(deductMoney);

                // 为了记录最终退了多少钱，我们可以把这个值更新到订单表里（这里假设暂存在 payAmount 字段做记录，或者你有 refundAmount 字段更好）
                order.setPayAmount(actualRefund.max(BigDecimal.ZERO));
            }
            userService.updateById(user);

            // TODO: (进阶) 根据 OrderItem 表里的数据，把商品的 stock 加回去

        } else {
            order.setStatus(3); // 售后拒绝或处理完毕(非退款)，流转回已完成
        }
        this.updateById(order);
    }
}