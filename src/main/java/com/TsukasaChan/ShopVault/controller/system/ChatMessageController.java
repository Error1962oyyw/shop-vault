package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.entity.system.ChatMessage;
import com.TsukasaChan.ShopVault.service.system.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController extends BaseController {

    private final ChatMessageService chatMessageService;

    @PostMapping("/send")
    public Result<String> sendToAdmin(@RequestBody ChatMessage msg) {
        chatMessageService.sendToAdmin(getCurrentUserId(), msg);
        return Result.success("发送成功");
    }

    @GetMapping("/history")
    public Result<List<ChatMessage>> getUserHistory() {
        return Result.success(chatMessageService.getUserHistory(getCurrentUserId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reply")
    public Result<String> replyToUser(@RequestBody ChatMessage msg) {
        chatMessageService.replyToUser(getCurrentUserId(), msg);
        return Result.success("回复成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/history/{userId}")
    public Result<List<ChatMessage>> getAdminHistory(@PathVariable Long userId) {
        return Result.success(chatMessageService.getAdminHistory(getCurrentUserId(), userId));
    }
}