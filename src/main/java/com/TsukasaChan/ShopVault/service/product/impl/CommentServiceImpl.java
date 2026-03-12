package com.TsukasaChan.ShopVault.service.product.impl;

import com.TsukasaChan.ShopVault.entity.order.Order;
import com.TsukasaChan.ShopVault.entity.product.Comment;
import com.TsukasaChan.ShopVault.mapper.product.CommentMapper;
import com.TsukasaChan.ShopVault.service.order.OrderService;
import com.TsukasaChan.ShopVault.service.product.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final OrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComment(Comment comment, Long userId) {
        // 1. 校验订单是否属于该用户且已完成
        Order order = orderService.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, comment.getOrderId())
                .eq(Order::getUserId, userId));

        if (order == null) {
            throw new RuntimeException("非法操作：未找到该订单");
        }
        if (order.getStatus() != 3) {
            throw new RuntimeException("必须在确认收货后才能评价！");
        }

        // 2. 校验是否重复评价 (防刷)
        long count = this.count(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getOrderId, comment.getOrderId())
                .eq(Comment::getProductId, comment.getProductId()));
        if (count > 0) {
            throw new RuntimeException("您已经评价过该商品，请勿重复提交");
        }

        // 3. 补充信息并保存 (默认待审核状态 0，如果是自动过审可以设为 1)
        comment.setUserId(userId);
        comment.setAuditStatus(0);
        this.save(comment);
    }

    @Override
    public void likeComment(Long commentId) {
        Comment comment = this.getById(commentId);
        if (comment != null) {
            comment.setLikes(comment.getLikes() + 1);
            this.updateById(comment);
        }
    }

    @Override
    public void reportComment(Long commentId) {
        Comment comment = this.getById(commentId);
        if (comment != null) {
            comment.setIsReported(1);
            this.updateById(comment);
        }
    }

    @Override
    public void adminDeleteComment(Long commentId) {
        Comment comment = this.getById(commentId);
        if (comment != null) {
            comment.setAuditStatus(2);
            this.updateById(comment);
        }
    }

    @Override
    public List<Comment> getCommentsByProductId(Long productId) {
        return this.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Comment>()
                .eq(Comment::getProductId, productId)
                .eq(Comment::getAuditStatus, 1)
                .orderByDesc(Comment::getLikes)
                .orderByDesc(Comment::getCreateTime));
    }
}