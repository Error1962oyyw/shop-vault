package com.TsukasaChan.ShopVault.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.system.ChatMessage;
import com.TsukasaChan.ShopVault.system.service.ChatMessageService;
import com.TsukasaChan.ShopVault.system.mapper.ChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【sys_chat_message(售前/售后客服聊天表)】的数据库操作Service实现
* @createDate 2026-03-04 23:54:52
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{

}




