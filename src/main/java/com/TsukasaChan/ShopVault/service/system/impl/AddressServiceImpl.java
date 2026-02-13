package com.TsukasaChan.ShopVault.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.system.Address;
import com.TsukasaChan.ShopVault.service.system.AddressService;
import com.TsukasaChan.ShopVault.mapper.system.AddressMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【sys_address(收货地址表)】的数据库操作Service实现
* @createDate 2026-02-13 20:45:10
*/
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address>
    implements AddressService{

}




