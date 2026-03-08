package com.TsukasaChan.ShopVault.service.order.impl;

import cn.hutool.core.util.IdUtil;
import com.TsukasaChan.ShopVault.entity.marketing.Activity;
import com.TsukasaChan.ShopVault.entity.marketing.UserCoupon;
import com.TsukasaChan.ShopVault.entity.order.CartItem;
import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.entity.order.OrderItem;
import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.mapper.order.OrderMapper;
import com.TsukasaChan.ShopVault.service.marketing.ActivityService;
import com.TsukasaChan.ShopVault.service.marketing.UserCouponService;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final ProductService productService;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final CartItemService cartItemService;
    private final ActivityService activityService;
    private final UserCouponService userCouponService;

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
    private Order buildOrder(Long userId, BigDecimal totalAmount, String orderNo, Long userCouponId) {
        BigDecimal payAmount = totalAmount;

        // 1. 处理优惠券抵扣 (如果有传 userCouponId)
        if (userCouponId != null) {
            UserCoupon userCoupon = userCouponService.getById(userCouponId);
            // 校验券是否合法、是否属于该用户、是否未使用、是否过期
            if (userCoupon != null
                    && userCoupon.getUserId().equals(userId)
                    && userCoupon.getStatus() == 0
                    && userCoupon.getExpireTime().isAfter(LocalDateTime.now())) {

                Activity couponRule = activityService.getById(userCoupon.getActivityId());
                // 假设 discountRate 存的是折扣率 (如 0.8 代表打8折) 或直接抵扣的金额
                // 这里我们演示打折模式：
                if (couponRule != null && couponRule.getDiscountRate() != null) {
                    payAmount = payAmount.multiply(couponRule.getDiscountRate()).setScale(2, RoundingMode.HALF_UP);

                    // 将优惠券标记为已使用
                    userCoupon.setStatus(1);
                    userCoupon.setUseTime(LocalDateTime.now());
                    userCouponService.updateById(userCoupon);
                }
            } else {
                throw new RuntimeException("该优惠券无效或已过期");
            }
        }

        // 动态查询当前生效的促销活动 (type = 1)
        List<Activity> activities = activityService.list(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getType, 1)
                .eq(Activity::getStatus, 1)
                .le(Activity::getStartTime, LocalDateTime.now())
                .ge(Activity::getEndTime, LocalDateTime.now()));

        for (Activity activity : activities) {
            String rule = activity.getRuleExpression();
            boolean isMatch = false;

            // 规则解析：如果规则填了 "8"，代表尾号为8的日子生效；如果填了 "EVERYDAY"，每天生效
            if (rule != null && String.valueOf(LocalDate.now().getDayOfMonth()).endsWith(rule)) {
                isMatch = true;
            } else if ("EVERYDAY".equalsIgnoreCase(rule)) {
                isMatch = true;
            }

            if (isMatch && activity.getDiscountRate() != null) {
                payAmount = payAmount.multiply(activity.getDiscountRate()).setScale(2, RoundingMode.HALF_UP);
            }
        }

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount); // 商品原总价
        order.setPayAmount(payAmount);     // 扣除优惠券和折扣后的实付价
        order.setStatus(0); // 待付款
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

        // 结算后清空对应的购物车商品
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
            order.setDeliveryTime(LocalDateTime.now());
            order.setAutoReceiveTime(LocalDateTime.now().plusDays(10)); // 发货后默认10天自动收货
            this.updateById(order);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(String orderNo, Long userId) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo).eq(Order::getUserId, userId));
        if (order == null || order.getStatus() != 2) throw new RuntimeException("订单状态不支持确认收货");

        order.setStatus(3); // 3: 已完成
        order.setReceiveTime(LocalDateTime.now());
        this.updateById(order);

        // 动态查询积分翻倍活动
        BigDecimal pointsMultiplier = new BigDecimal("1.00");
        List<Activity> activities = activityService.list(new LambdaQueryWrapper<Activity>()
                .eq(Activity::getType, 1).eq(Activity::getStatus, 1)
                .le(Activity::getStartTime, LocalDateTime.now()).ge(Activity::getEndTime, LocalDateTime.now()));

        for (Activity activity : activities) {
            if (activity.getPointsMultiplier() != null && activity.getPointsMultiplier().compareTo(BigDecimal.ONE) > 0) {
                pointsMultiplier = pointsMultiplier.max(activity.getPointsMultiplier()); // 取最大的翻倍倍率
            }
        }

        // 实付款 1元 = 100积分 * 倍率
        int rewardPoints = order.getPayAmount().multiply(new BigDecimal("100")).multiply(pointsMultiplier).intValue();

        User user = userService.getById(userId);
        if (rewardPoints > 0) {
            user.setPoints(user.getPoints() + rewardPoints);
        }
        // 每次成功购物增加 2 点信誉分 (最高100)
        user.setCreditScore(Math.min(100, user.getCreditScore() + 2));

        userService.updateById(user);
    }

    // 新增：延长收货时间
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void extendReceiveTime(String orderNo, Long userId) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo).eq(Order::getUserId, userId));

        if (order == null || order.getStatus() != 2) throw new RuntimeException("订单状态不支持延长收货");
        if (order.getIsExtended() == 1) throw new RuntimeException("每笔订单只能延长一次收货时间");

        User user = userService.getById(userId);
        // 信誉分机制：低于60分不允许延长收货
        if (user.getCreditScore() < 60) throw new RuntimeException("您的信誉分过低，无法申请延长收货");

        order.setAutoReceiveTime(order.getAutoReceiveTime().plusDays(5)); // 延长5天，最多15天
        order.setIsExtended(1);
        this.updateById(order);
    }

    // 取消订单 (未付款时)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo, Long userId) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo).eq(Order::getUserId, userId));
        if (order == null || order.getStatus() != 0) throw new RuntimeException("只能取消待付款的订单");

        order.setStatus(4); // 关闭
        order.setCloseTime(LocalDateTime.now());
        this.updateById(order);

        // 恢复库存
        orderItemService.restoreInventoryByOrderId(order.getId());
    }
}