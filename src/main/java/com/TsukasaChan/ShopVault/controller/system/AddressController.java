package com.TsukasaChan.ShopVault.controller.system;

import com.TsukasaChan.ShopVault.common.Result;
import com.TsukasaChan.ShopVault.common.SecurityUtils;
import com.TsukasaChan.ShopVault.entity.system.User;
import com.TsukasaChan.ShopVault.entity.system.Address;
import com.TsukasaChan.ShopVault.service.system.UserService;
import com.TsukasaChan.ShopVault.service.system.AddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    private Long getCurrentUserId() {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, SecurityUtils.getCurrentUsername())).getId();
    }

    /**
     * 1. 添加地址
     */
    @PostMapping("/add")
    public Result<String> addAddress(@RequestBody Address address) {
        Long userId = getCurrentUserId();
        address.setUserId(userId);

        // 如果是该用户的第一个地址，自动设为默认
        long count = addressService.count(new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId));
        address.setIsDefault(count == 0 ? 1 : 0);

        addressService.save(address);
        return Result.success("地址添加成功");
    }

    /**
     * 2. 获取我的地址列表
     */
    @GetMapping("/list")
    public Result<List<Address>> listMyAddresses() {
        List<Address> list = addressService.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, getCurrentUserId())
                .orderByDesc(Address::getIsDefault) // 默认地址排在最前面
                .orderByDesc(Address::getCreateTime));
        return Result.success(list);
    }

    /**
     * 3. 修改地址
     */
    @PutMapping("/update")
    public Result<String> updateAddress(@RequestBody Address address) {
        // 安全校验：防止通过传别人的ID来修改别人的地址
        address.setUserId(getCurrentUserId());
        addressService.updateById(address);
        return Result.success("地址修改成功");
    }

    /**
     * 4. 删除地址
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteAddress(@PathVariable Long id) {
        addressService.remove(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, id)
                .eq(Address::getUserId, getCurrentUserId())); // 必须是自己的地址才能删
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