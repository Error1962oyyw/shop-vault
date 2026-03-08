package com.TsukasaChan.ShopVault.service.system.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.system.Log;
import com.TsukasaChan.ShopVault.service.system.LogService;
import com.TsukasaChan.ShopVault.mapper.system.LogMapper;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log>
    implements LogService{

}