package com.TsukasaChan.ShopVault.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.TsukasaChan.ShopVault.entity.system.Log;
import com.TsukasaChan.ShopVault.system.service.LogService;
import com.TsukasaChan.ShopVault.system.mapper.LogMapper;
import org.springframework.stereotype.Service;

/**
* @author Error1962
* @description 针对表【sys_log(操作日志表)】的数据库操作Service实现
* @createDate 2026-03-04 23:54:52
*/
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log>
    implements LogService{

}




