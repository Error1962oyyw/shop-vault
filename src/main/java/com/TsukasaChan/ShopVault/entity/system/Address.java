package com.TsukasaChan.ShopVault.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收货地址表
 */
@Data
@TableName("sys_address")
public class Address implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String region;

    private String detailAddress;

    /**
     * 是否默认地址 true:是 false:否
     */
    private Boolean isDefault;

    private LocalDateTime createTime;
}
