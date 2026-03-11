package com.TsukasaChan.ShopVault.service.product;

import com.TsukasaChan.ShopVault.entity.product.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CommentService extends IService<Comment> {
    void addComment(Comment comment, Long userId);

    void likeComment(Long commentId);

    void reportComment(Long commentId);

    void adminDeleteComment(Long commentId);
}
