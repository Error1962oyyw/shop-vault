package com.TsukasaChan.ShopVault.service.system;

import com.TsukasaChan.ShopVault.entity.system.Address;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AddressService extends IService<Address> {
    void setDefaultAddress(Long addressId, Long userId);

    void addAddress(Address address, Long userId);

    List<Address> listMyAddresses(Long userId);

    void updateAddress(Address address, Long userId);

    void deleteAddress(Long id, Long userId);
}
