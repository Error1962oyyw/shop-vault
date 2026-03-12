package com.TsukasaChan.ShopVault.service.system.impl;

import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.system.ChatMessage;
import com.TsukasaChan.ShopVault.service.system.ChatMessageService;
import com.TsukasaChan.ShopVault.mapper.system.ChatMessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    private final UserService userService;

    public List<ChatMessage> fetchHistoryAndMarkRead(Long myId, Long peerId, Long unreadSenderId, Long unreadReceiverId) {
        // 1. 获取聊天记录
        List<ChatMessage> history = list(new LambdaQueryWrapper<ChatMessage>()
                .and(wrapper -> wrapper.eq(ChatMessage::getSenderId, myId).eq(ChatMessage::getReceiverId, peerId)
                        .or()
                        .eq(ChatMessage::getSenderId, peerId).eq(ChatMessage::getReceiverId, myId))
                .orderByAsc(ChatMessage::getCreateTime));

        // 2. 将对方发给我的未读消息标记为已读
        this.update(new LambdaUpdateWrapper<ChatMessage>()
                .eq(ChatMessage::getSenderId, unreadSenderId)
                .eq(ChatMessage::getReceiverId, unreadReceiverId)
                .eq(ChatMessage::getIsRead, 0)
                .set(ChatMessage::getIsRead, 1));

        return history;
    }

    // 私有方法：获取管理员ID
    private Long getAdminId() {
        User admin = userService.getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>().eq(User::getRole, "ADMIN").last("LIMIT 1"));
        if (admin == null) throw new RuntimeException("系统暂无客服人员");
        return admin.getId();
    }

    @Override
    public void sendToAdmin(Long userId, ChatMessage msg) {
        msg.setSenderId(userId);
        msg.setReceiverId(getAdminId());
        msg.setIsRead(0);
        if (msg.getMsgType() == null) msg.setMsgType(1);
        this.save(msg);
    }

    @Override
    public List<ChatMessage> getUserHistory(Long userId) {
        Long adminId = getAdminId();
        return this.fetchHistoryAndMarkRead(userId, adminId, adminId, userId);
    }

    @Override
    public void replyToUser(Long adminId, ChatMessage msg) {
        if (msg.getReceiverId() == null) throw new RuntimeException("必须指定回复的用户ID");
        msg.setSenderId(adminId);
        msg.setIsRead(0);
        if (msg.getMsgType() == null) msg.setMsgType(1);
        this.save(msg);
    }

    @Override
    public List<ChatMessage> getAdminHistory(Long adminId, Long userId) {
        return this.fetchHistoryAndMarkRead(adminId, userId, userId, adminId);
    }
}