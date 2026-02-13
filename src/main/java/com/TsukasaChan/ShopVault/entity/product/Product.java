package com.TsukasaChan.ShopVault.entity.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 商品表
 * @TableName pms_product
 */
@TableName(value ="pms_product")
@Data
public class Product implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类ID
     */
    @TableField(value = "category_id")
    private Long category_id;

    /**
     * 商品名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 副标题/卖点
     */
    @TableField(value = "sub_title")
    private String sub_title;

    /**
     * 主图
     */
    @TableField(value = "main_image")
    private String main_image;

    /**
     * 销售价格
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 库存数量
     */
    @TableField(value = "stock")
    private Integer stock;

    /**
     * 库存预警阈值
     */
    @TableField(value = "stock_warning")
    private Integer stock_warning;

    /**
     * 状态 1:上架 0:下架
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 销量 (用于热销推荐)
     */
    @TableField(value = "sales")
    private Integer sales;

    /**
     * 商品详情(富文本)
     */
    @TableField(value = "detail_html")
    private String detail_html;

    /**
     * 
     */
    @TableField(value = "create_time")
    private LocalDateTime create_time;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Product other = (Product) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCategory_id() == null ? other.getCategory_id() == null : this.getCategory_id().equals(other.getCategory_id()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getSub_title() == null ? other.getSub_title() == null : this.getSub_title().equals(other.getSub_title()))
            && (this.getMain_image() == null ? other.getMain_image() == null : this.getMain_image().equals(other.getMain_image()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getStock() == null ? other.getStock() == null : this.getStock().equals(other.getStock()))
            && (this.getStock_warning() == null ? other.getStock_warning() == null : this.getStock_warning().equals(other.getStock_warning()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getSales() == null ? other.getSales() == null : this.getSales().equals(other.getSales()))
            && (this.getDetail_html() == null ? other.getDetail_html() == null : this.getDetail_html().equals(other.getDetail_html()))
            && (this.getCreate_time() == null ? other.getCreate_time() == null : this.getCreate_time().equals(other.getCreate_time()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCategory_id() == null) ? 0 : getCategory_id().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getSub_title() == null) ? 0 : getSub_title().hashCode());
        result = prime * result + ((getMain_image() == null) ? 0 : getMain_image().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getStock() == null) ? 0 : getStock().hashCode());
        result = prime * result + ((getStock_warning() == null) ? 0 : getStock_warning().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getSales() == null) ? 0 : getSales().hashCode());
        result = prime * result + ((getDetail_html() == null) ? 0 : getDetail_html().hashCode());
        result = prime * result + ((getCreate_time() == null) ? 0 : getCreate_time().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", category_id=").append(category_id);
        sb.append(", name=").append(name);
        sb.append(", sub_title=").append(sub_title);
        sb.append(", main_image=").append(main_image);
        sb.append(", price=").append(price);
        sb.append(", stock=").append(stock);
        sb.append(", stock_warning=").append(stock_warning);
        sb.append(", status=").append(status);
        sb.append(", sales=").append(sales);
        sb.append(", detail_html=").append(detail_html);
        sb.append(", create_time=").append(create_time);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}