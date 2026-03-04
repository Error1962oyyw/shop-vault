package com.TsukasaChan.ShopVault.controller.product;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.product.Comment;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.product.CommentService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        return Result.success("评价发布成功，等待审核中");
    }
}