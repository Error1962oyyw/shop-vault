package com.TsukasaChan.ShopVault.controller.product;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.entity.product.Comment;
import com.TsukasaChan.ShopVault.service.product.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController extends BaseController {

    private final CommentService commentService;

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
        return Result.success(commentService.getCommentsByProductId(productId));
    }

    /**
     * 点赞评价
     */
    @PostMapping("/like/{commentId}")
    public Result<String> likeComment(@PathVariable Long commentId) {
        commentService.likeComment(commentId);
        return Result.success("点赞成功");
    }

    /**
     * 举报评价
     */
    @PostMapping("/report/{commentId}")
    public Result<String> reportComment(@PathVariable Long commentId) {
        commentService.reportComment(commentId);
        return Result.success("已收到您的举报，我们将尽快处理");
    }

    /**
     * 管理员删除违规评价
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/{commentId}")
    public Result<String> deleteComment(@PathVariable Long commentId) {
        commentService.adminDeleteComment(commentId);
        return Result.success("已删除该违规评价");
    }
}