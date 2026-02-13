package com.TsukasaChan.ShopVault.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
@TableName("sys_user")
public class UserBACKUP implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String phone;

    private String email;

    /**
     * 钱包余额
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

    private LocalDateTime createTime;
}
