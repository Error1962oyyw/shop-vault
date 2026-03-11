package com.TsukasaChan.ShopVault.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.system.ChatMessage;
import com.TsukasaChan.ShopVault.service.system.ChatMessageService;
import com.TsukasaChan.ShopVault.mapper.system.ChatMessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {
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
}