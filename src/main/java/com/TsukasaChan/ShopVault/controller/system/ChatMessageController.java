package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.ChatMessage;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.ChatMessageService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;

    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    private Long getAdminId() {
        User admin = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getRole, "ADMIN").last("LIMIT 1"));
        if (admin == null) throw new RuntimeException("系统暂无客服人员");
        return admin.getId();
    }

    /**
     * @param myId 当前请求人的ID
     * @param peerId 对方的ID
     * @param unreadSenderId 需要被标记为已读的消息的发送人ID
     * @param unreadReceiverId 需要被标记为已读的消息的接收人ID
     */
    private Result<List<ChatMessage>> fetchHistoryAndMarkRead(Long myId, Long peerId, Long unreadSenderId, Long unreadReceiverId) {
        List<ChatMessage> history = chatMessageService.fetchHistoryAndMarkRead(myId, peerId, unreadSenderId, unreadReceiverId);

        // 3. 直接组装返回结果
        return Result.success(history);
    }

    // ================== 用户端接口 ==================

    @PostMapping("/send")
    public Result<String> sendToAdmin(@RequestBody ChatMessage msg) {
        msg.setSenderId(getCurrentUserId());
        msg.setReceiverId(getAdminId());
        msg.setIsRead(0); // 0代表未读
        if (msg.getMsgType() == null) msg.setMsgType(1);

        chatMessageService.save(msg);
        return Result.success("发送成功");
    }

    @GetMapping("/history")
    public Result<List<ChatMessage>> getUserHistory() {
        Long userId = getCurrentUserId();
        Long adminId = getAdminId();
        // 用户查历史：把 客服(adminId) 发给 用户(userId) 的消息标为已读
        return fetchHistoryAndMarkRead(userId, adminId, adminId, userId);
    }

    // ================== 管理端接口 ==================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reply")
    public Result<String> replyToUser(@RequestBody ChatMessage msg) {
        if (msg.getReceiverId() == null) return Result.error(400, "必须指定回复的用户ID");

        msg.setSenderId(getCurrentUserId());
        msg.setIsRead(0);
        if (msg.getMsgType() == null) msg.setMsgType(1);

        chatMessageService.save(msg);
        return Result.success("回复成功");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/history/{userId}")
    public Result<List<ChatMessage>> getAdminHistory(@PathVariable Long userId) {
        Long adminId = getCurrentUserId();
        // 客服查历史：把 用户(userId) 发给 客服(adminId) 的消息标为已读
        return fetchHistoryAndMarkRead(adminId, userId, userId, adminId);
    }
}