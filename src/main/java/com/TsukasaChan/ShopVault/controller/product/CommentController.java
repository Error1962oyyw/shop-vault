package com.TsukasaChan.ShopVault.controller.product;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.product.Comment;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.product.CommentService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    @PostMapping("/add")
    public Result<String> addComment(@RequestBody Comment comment) {
        commentService.addComment(comment, getCurrentUserId());
        return Result.success("评价发布成功");
    }

    /**
     * 获取某商品的评价列表 (按点赞数降序，再按时间降序)
     */
    @GetMapping("/list/{productId}")
    public Result<List<Comment>> getComments(@PathVariable Long productId) {
        List<Comment> list = commentService.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getProductId, productId)
                .eq(Comment::getAuditStatus, 1) // 只查正常的
                .orderByDesc(Comment::getLikes)
                .orderByDesc(Comment::getCreateTime));
        return Result.success(list);
    }

    /**
     * 点赞评价
     */
    @PostMapping("/like/{commentId}")
    public Result<String> likeComment(@PathVariable Long commentId) {
        Comment comment = commentService.getById(commentId);
        if (comment != null) {
            comment.setLikes(comment.getLikes() + 1);
            commentService.updateById(comment);
        }
        return Result.success("点赞成功");
    }

    /**
     * 举报评价
     */
    @PostMapping("/report/{commentId}")
    public Result<String> reportComment(@PathVariable Long commentId) {
        Comment comment = commentService.getById(commentId);
        if (comment != null) {
            comment.setIsReported(1);
            commentService.updateById(comment);
        }
        return Result.success("已收到您的举报，我们将尽快处理");
    }

    /**
     * 管理员删除违规评价
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/{commentId}")
    public Result<String> deleteComment(@PathVariable Long commentId) {
        Comment comment = commentService.getById(commentId);
        if (comment != null) {
            comment.setAuditStatus(2); // 标记为被管理员删除
            commentService.updateById(comment);
        }
        return Result.success("已删除该违规评价");
    }
}