package com.TsukasaChan.ShopVault.service.system;

import com.TsukasaChan.ShopVault.entity.system.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {
    List<ChatMessage> fetchHistoryAndMarkRead(Long myId, Long peerId, Long unreadSenderId, Long unreadReceiverId);
}
