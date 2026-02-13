package com.TsukasaChan.ShopVault.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 全局统一返回结果类
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;    // 状态码：200 成功，500 失败，401 未登录等
    private String message;  // 提示信息
    private T data;          // 返回的数据主体

    // 成功（无数据）
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    // 成功（有数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    // 失败
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 默认失败
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}