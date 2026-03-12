package com.TsukasaChan.ShopVault.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 售前/售后客服聊天表
 * @TableName sys_chat_message
 */
@Data
@TableName("sys_chat_message")
public class ChatMessage implements Serializable {


    @TableField(exist = false)
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送方ID (用户或客服)
     */
    private Long senderId;

    /**
     * 接收方ID (客服或用户)
     */
    private Long receiverId;

    /**
     * 关联的订单编号(可选)
     */
    private String orderNo;

    /**
     * 聊天内容/图片URL
     */
    private String content;

    /**
     * 消息类型: 1文字 2图片 3系统消息
     */
    private Integer msgType;

    /**
     * 是否已读: 0未读 1已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}