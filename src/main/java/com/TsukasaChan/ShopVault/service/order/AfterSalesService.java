package com.TsukasaChan.ShopVault.service.order;

import com.TsukasaChan.ShopVault.entity.order.AfterSales;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
* @author Error1962
* @description 针对表【oms_after_sales(售后服务记录表)】的数据库操作Service
* @createDate 2026-03-05 00:02:02
*/
public interface AfterSalesService extends IService<AfterSales> {
    void applyAfterSales(AfterSales afterSales, Long userId);

    void resolveAfterSales(String orderNo, boolean isAgree, String reply, BigDecimal refundAmount);
}
