package com.TsukasaChan.ShopVault.service.marketing.impl;

import cn.hutool.core.util.IdUtil;
import com.TsukasaChan.ShopVault.entity.marketing.Activity;
import com.TsukasaChan.ShopVault.entity.marketing.PointsRecord;
import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.entity.order.OrderItem;
import com.TsukasaChan.ShopVault.entity.product.Product;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.mapper.marketing.ActivityMapper;
import com.TsukasaChan.ShopVault.service.marketing.ActivityService;
import com.TsukasaChan.ShopVault.service.marketing.PointsRecordService;
import com.TsukasaChan.ShopVault.service.order.OrderItemService;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.product.ProductService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final PointsRecordService pointsRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String exchangeProduct(Long userId, Long activityId) {
        // 1. 校验活动合法性
        Activity activity = this.getById(activityId);
        LocalDateTime now = LocalDateTime.now();
        if (activity == null || activity.getStatus() != 1 || activity.getType() != 2) {
            throw new RuntimeException("该积分兑换活动不存在或已下线");
        }
        if (now.isBefore(activity.getStartTime()) || now.isAfter(activity.getEndTime())) {
            throw new RuntimeException("不在活动时间范围内");
        }

        // 2. 校验商品库存与用户积分
        Product product = productService.getById(activity.getProductId());
        if (product == null || product.getStock() < 1) {
            throw new RuntimeException("抱歉，该兑换商品已被抢空");
        }

        User user = userService.getById(userId);
        if (user.getPoints() < activity.getPointCost()) {
            throw new RuntimeException("积分不足，兑换需要 " + activity.getPointCost() + " 积分");
        }

        // 3. 扣除积分与库存
        user.setPoints(user.getPoints() - activity.getPointCost());
        userService.updateById(user);

        product.setStock(product.getStock() - 1);
        productService.updateById(product);

        // 4. 记录积分流水
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(3); // 3: 兑换消耗
        record.setAmount(-activity.getPointCost());
        record.setDescription("参与活动: " + activity.getName() + " 兑换商品");
        pointsRecordService.save(record);

        // 5. 生成一笔0元发货订单，融入现有订单体系
        String orderNo = IdUtil.getSnowflakeNextIdStr();
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(product.getPrice());
        order.setPayAmount(BigDecimal.ZERO); // 实付 0 元
        order.setStatus(1); // 直接跳过待付款，进入 [待发货] 状态
        orderService.save(order);

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setOrderNo(orderNo);
        item.setProductId(product.getId());
        item.setProductName(product.getName() + " [积分兑换]");
        item.setProductImg(product.getMainImage());
        item.setProductPrice(product.getPrice());
        item.setQuantity(1);
        orderItemService.save(item);

        return orderNo;
    }
}