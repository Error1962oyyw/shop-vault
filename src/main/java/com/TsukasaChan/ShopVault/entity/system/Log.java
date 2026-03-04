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
 * 操作日志表
 * @TableName sys_log
 */
@Data
@TableName(value ="sys_log")
public class Log implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long userId;

    /**
     * 操作人用户名
     */
    private String username;

    /**
     * 角色(USER/ADMIN)
     */
    private String role;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 具体操作描述
     */
    private String action;

    /**
     * 操作IP
     */
    private String ipAddress;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}