package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.annotation.LogOperation;
import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.controller.BaseController;
import com.TsukasaChan.ShopVault.entity.system.Address;
import com.TsukasaChan.ShopVault.service.system.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController extends BaseController {

    private final AddressService addressService;

    /**
     * 1. 添加地址
     */
    @LogOperation(module = "收货地址", action = "新增收货地址")
    @PostMapping("/add")
    public Result<String> addAddress(@RequestBody Address address) {
        addressService.addAddress(address, getCurrentUserId());
        return Result.success("地址添加成功");
    }

    /**
     * 2. 获取我的地址列表
     */
    @GetMapping("/list")
    public Result<List<Address>> listMyAddresses() {
        return Result.success(addressService.listMyAddresses(getCurrentUserId()));
    }

    /**
     * 3. 修改地址
     */
    @PutMapping("/update")
    public Result<String> updateAddress(@RequestBody Address address) {
        addressService.updateAddress(address, getCurrentUserId());
        return Result.success("地址修改成功");
    }

    /**
     * 4. 删除地址
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id, getCurrentUserId());
        return Result.success("删除成功");
    }

    /**
     * 5. 设为默认地址
     */
    @PutMapping("/default/{id}")
    public Result<String> setDefaultAddress(@PathVariable Long id) {
        addressService.setDefaultAddress(id, getCurrentUserId());
        return Result.success("已设为默认地址");
    }
}