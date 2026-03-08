package com.TsukasaChan.ShopVault.service.system;

import com.TsukasaChan.ShopVault.entity.system.Address;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AddressService extends IService<Address> {
    void setDefaultAddress(Long addressId, Long userId);

}
