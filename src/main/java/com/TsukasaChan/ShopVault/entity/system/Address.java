package com.TsukasaChan.ShopVault.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 收货地址表
 * @TableName sys_address
 */
@TableName(value ="sys_address")
@Data
public class Address implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long user_id;

    /**
     * 收货人姓名
     */
    @TableField(value = "receiver_name")
    private String receiver_name;

    /**
     * 收货人电话
     */
    @TableField(value = "receiver_phone")
    private String receiver_phone;

    /**
     * 省
     */
    @TableField(value = "province")
    private String province;

    /**
     * 市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 区
     */
    @TableField(value = "region")
    private String region;

    /**
     * 详细地址
     */
    @TableField(value = "detail_address")
    private String detail_address;

    /**
     * 是否默认地址
     */
    @TableField(value = "is_default")
    private Integer is_default;

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
        Address other = (Address) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUser_id() == null ? other.getUser_id() == null : this.getUser_id().equals(other.getUser_id()))
            && (this.getReceiver_name() == null ? other.getReceiver_name() == null : this.getReceiver_name().equals(other.getReceiver_name()))
            && (this.getReceiver_phone() == null ? other.getReceiver_phone() == null : this.getReceiver_phone().equals(other.getReceiver_phone()))
            && (this.getProvince() == null ? other.getProvince() == null : this.getProvince().equals(other.getProvince()))
            && (this.getCity() == null ? other.getCity() == null : this.getCity().equals(other.getCity()))
            && (this.getRegion() == null ? other.getRegion() == null : this.getRegion().equals(other.getRegion()))
            && (this.getDetail_address() == null ? other.getDetail_address() == null : this.getDetail_address().equals(other.getDetail_address()))
            && (this.getIs_default() == null ? other.getIs_default() == null : this.getIs_default().equals(other.getIs_default()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUser_id() == null) ? 0 : getUser_id().hashCode());
        result = prime * result + ((getReceiver_name() == null) ? 0 : getReceiver_name().hashCode());
        result = prime * result + ((getReceiver_phone() == null) ? 0 : getReceiver_phone().hashCode());
        result = prime * result + ((getProvince() == null) ? 0 : getProvince().hashCode());
        result = prime * result + ((getCity() == null) ? 0 : getCity().hashCode());
        result = prime * result + ((getRegion() == null) ? 0 : getRegion().hashCode());
        result = prime * result + ((getDetail_address() == null) ? 0 : getDetail_address().hashCode());
        result = prime * result + ((getIs_default() == null) ? 0 : getIs_default().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", user_id=").append(user_id);
        sb.append(", receiver_name=").append(receiver_name);
        sb.append(", receiver_phone=").append(receiver_phone);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", region=").append(region);
        sb.append(", detail_address=").append(detail_address);
        sb.append(", is_default=").append(is_default);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}