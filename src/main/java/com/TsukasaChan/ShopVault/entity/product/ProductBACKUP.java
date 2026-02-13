package com.TsukasaChan.ShopVault.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品信息表
 */
@Data
@TableName("pms_product")
public class ProductBACKUP implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String name;

    private String subTitle;

    private String mainImage;

    private BigDecimal price;

    private Integer stock;

    private Integer stockWarning;

    /**
     * 状态 1:上架 0:下架
     */
    private Integer status;

    private Integer sales;

    /**
     * 商品详情(富文本)
     */
    private String detailHtml;

    private LocalDateTime createTime;
}
