package com.TsukasaChan.ShopVault.service.system.impl;

import com.TsukasaChan.ShopVault.entity.system.Address;
import com.TsukasaChan.ShopVault.mapper.system.AddressMapper;
import com.TsukasaChan.ShopVault.service.system.AddressService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    /**
     * 设置默认地址
     * @Transactional 保证数据一致性
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long addressId, Long userId) {
        // 1. 将该用户所有的地址 is_default 设为 0 (非默认)
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getUserId, userId)
                .set(Address::getIsDefault, false);
        this.update(updateWrapper);

        // 2. 将指定的地址 is_default 设为 1 (默认)
        Address targetAddress = new Address();
        targetAddress.setId(addressId);
        targetAddress.setIsDefault(true);
        this.updateById(targetAddress);
    }
}