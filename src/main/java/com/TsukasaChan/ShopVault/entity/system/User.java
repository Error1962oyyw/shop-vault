package com.TsukasaChan.ShopVault.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户表
 * @TableName sys_user
 */
@TableName(value ="sys_user")
@Data
public class User implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 钱包余额(模拟支付用)
     */
    private BigDecimal balance;

    /**
     * 当前积分
     */
    private Integer points;

    /**
     * 状态 1:正常 0:冻结
     */
    private Integer status;

    /**
     * 角色: USER/ADMIN
     */
    private String role;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 信誉分(影响售后审批)
     */
    private Integer creditScore;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}