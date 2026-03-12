package com.TsukasaChan.ShopVault.service.order;

import com.TsukasaChan.ShopVault.entity.order.AfterSales;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

public interface AfterSalesService extends IService<AfterSales> {
    void applyAfterSales(AfterSales afterSales, Long userId);

    void resolveAfterSales(String orderNo, boolean isAgree, String reply, BigDecimal refundAmount);

    List<AfterSales> getMyAfterSalesList(Long userId);

    List<AfterSales> getAllAfterSalesList();
}
