package com.TsukasaChan.ShopVault.service.system;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 负责与 Python YOLO 服务通信的客户端
 */
@Slf4j
@Service
public class YoloClientService {

    @Value("${shop-vault.YOLO.url}")
    private String yoloUrl;

    /**
     * 将图片发送给 Python 并获取识别出的英文标签
     */
    public List<String> detectImage(MultipartFile file) {
        List<String> labels = new ArrayList<>();
        try {
            // 使用 Hutool 构建包含文件的 POST 表单请求
            HttpResponse response = HttpRequest.post(yoloUrl)
                    .form("file", file.getBytes(), file.getOriginalFilename()) // 模拟 form-data 上传
                    .timeout(10000) // 10秒超时
                    .execute();

            if (response.isOk()) {
                String body = response.body();
                log.info("YOLO返回结果: {}", body);

                // 解析 Python 返回的 JSON: {"code":200, "labels":["cup", "backpack"], "message":"..."}
                JSONObject jsonObject = JSONUtil.parseObj(body);
                if (jsonObject.getInt("code") == 200) {
                    JSONArray labelsArray = jsonObject.getJSONArray("labels");
                    for (Object label : labelsArray) {
                        labels.add(label.toString());
                    }
                }
            } else {
                log.error("YOLO服务请求失败，状态码: {}", response.getStatus());
            }
        } catch (Exception e) {
            log.error("调用YOLO服务发生异常", e);
            throw new RuntimeException("AI图像识别服务暂时不可用");
        }
        return labels;
    }
}