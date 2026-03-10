package com.TsukasaChan.ShopVault.integration;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssService {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    /**
     * 上传文件并返回可访问的 URL
     */
    public String uploadFile(MultipartFile file) {
        try {
            // 生成唯一的文件名，防止覆盖
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "shopvault/" + UUID.randomUUID().toString() + extension;

            // 创建 OSS 客户端
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 上传文件流
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(bucketName, fileName, inputStream);
            ossClient.shutdown();

            // 拼接返回外网可以访问的图片 URL
            // 格式: https://bucketName.endpoint/fileName
            return "https://" + bucketName + "." + endpoint + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传至阿里云OSS失败", e);
        }
    }
}