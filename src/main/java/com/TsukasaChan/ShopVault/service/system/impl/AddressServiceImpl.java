package com.TsukasaChan.ShopVault.service.system.impl;

import com.TsukasaChan.ShopVault.entity.system.Address;
import com.TsukasaChan.ShopVault.mapper.system.AddressMapper;
import com.TsukasaChan.ShopVault.service.system.AddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public void addAddress(Address address, Long userId) {
        address.setUserId(userId);
        // 如果是该用户的第一个地址，自动设为默认
        long count = this.count(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        address.setIsDefault(count == 0 ? 1 : 0);
        this.save(address);
    }

    @Override
    public List<Address> listMyAddresses(Long userId) {
        return this.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime));
    }

    @Override
    public void updateAddress(Address address, Long userId) {
        address.setUserId(userId); // 防越权
        this.updateById(address);
    }

    @Override
    public void deleteAddress(Long id, Long userId) {
        this.remove(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, id)
                .eq(Address::getUserId, userId)); // 必须是自己的
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long addressId, Long userId) {
        // 你原本写好的代码保持不变
        LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Address::getUserId, userId).set(Address::getIsDefault, false);
        this.update(updateWrapper);

        Address targetAddress = new Address();
        targetAddress.setId(addressId);
        targetAddress.setIsDefault(1);
        this.updateById(targetAddress);
    }
}