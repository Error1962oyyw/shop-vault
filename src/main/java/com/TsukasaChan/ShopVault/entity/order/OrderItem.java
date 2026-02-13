package com.TsukasaChan.ShopVault.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 订单商品详情表
 * @TableName oms_order_item
 */
@TableName(value ="oms_order_item")
@Data
public class OrderItem implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @TableField(value = "order_id")
    private Long order_id;

    /**
     * 订单编号
     */
    @TableField(value = "order_no")
    private String order_no;

    /**
     * 
     */
    @TableField(value = "product_id")
    private Long product_id;

    /**
     * 
     */
    @TableField(value = "product_name")
    private String product_name;

    /**
     * 
     */
    @TableField(value = "product_img")
    private String product_img;

    /**
     * 购买时的单价
     */
    @TableField(value = "product_price")
    private BigDecimal product_price;

    /**
     * 购买数量
     */
    @TableField(value = "quantity")
    private Integer quantity;

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
        OrderItem other = (OrderItem) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrder_id() == null ? other.getOrder_id() == null : this.getOrder_id().equals(other.getOrder_id()))
            && (this.getOrder_no() == null ? other.getOrder_no() == null : this.getOrder_no().equals(other.getOrder_no()))
            && (this.getProduct_id() == null ? other.getProduct_id() == null : this.getProduct_id().equals(other.getProduct_id()))
            && (this.getProduct_name() == null ? other.getProduct_name() == null : this.getProduct_name().equals(other.getProduct_name()))
            && (this.getProduct_img() == null ? other.getProduct_img() == null : this.getProduct_img().equals(other.getProduct_img()))
            && (this.getProduct_price() == null ? other.getProduct_price() == null : this.getProduct_price().equals(other.getProduct_price()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrder_id() == null) ? 0 : getOrder_id().hashCode());
        result = prime * result + ((getOrder_no() == null) ? 0 : getOrder_no().hashCode());
        result = prime * result + ((getProduct_id() == null) ? 0 : getProduct_id().hashCode());
        result = prime * result + ((getProduct_name() == null) ? 0 : getProduct_name().hashCode());
        result = prime * result + ((getProduct_img() == null) ? 0 : getProduct_img().hashCode());
        result = prime * result + ((getProduct_price() == null) ? 0 : getProduct_price().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", order_id=").append(order_id);
        sb.append(", order_no=").append(order_no);
        sb.append(", product_id=").append(product_id);
        sb.append(", product_name=").append(product_name);
        sb.append(", product_img=").append(product_img);
        sb.append(", product_price=").append(product_price);
        sb.append(", quantity=").append(quantity);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}