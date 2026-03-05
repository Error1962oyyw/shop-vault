package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.ChatMessage;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.ChatMessageService;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
     * ★ 提取的公共方法：获取两人之间的聊天记录，消除重复代码警告
     */
    private List<ChatMessage> getChatHistory(Long userA, Long userB) {
        return chatMessageService.list(new LambdaQueryWrapper<ChatMessage>()
                .and(wrapper -> wrapper.eq(ChatMessage::getSenderId, userA).eq(ChatMessage::getReceiverId, userB)
                        .or()
                        .eq(ChatMessage::getSenderId, userB).eq(ChatMessage::getReceiverId, userA))
                .orderByAsc(ChatMessage::getCreateTime));
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

        List<ChatMessage> history = getChatHistory(userId, adminId);

        // 标记客服发给我的消息为已读 (1)
        chatMessageService.update(new LambdaUpdateWrapper<ChatMessage>()
                .eq(ChatMessage::getSenderId, adminId)
                .eq(ChatMessage::getReceiverId, userId)
                .eq(ChatMessage::getIsRead, 0)
                .set(ChatMessage::getIsRead, 1)); // 1代表已读

        return Result.success(history);
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

        List<ChatMessage> history = getChatHistory(userId, adminId);

        // 标记用户发给客服的消息为已读 (1)
        chatMessageService.update(new LambdaUpdateWrapper<ChatMessage>()
                .eq(ChatMessage::getSenderId, userId)
                .eq(ChatMessage::getReceiverId, adminId)
                .eq(ChatMessage::getIsRead, 0)
                .set(ChatMessage::getIsRead, 1));

        return Result.success(history);
    }
}