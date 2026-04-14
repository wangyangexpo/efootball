package com.example.demo.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 阿里云 OSS 上传服务
 * 封装文件上传逻辑，按球员 ID 组织 OSS 目录，重复上传自动覆盖
 */
@Service
public class OssUploadService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.url-prefix}")
    private String urlPrefix;

    /**
     * 上传球员头像到 OSS
     * 文件路径格式：players/{playerId}/avatar.{ext}
     *
     * @param file     上传的图片文件
     * @param playerId 球员 ID
     * @return 图片的公网访问 URL
     */
    public String uploadPlayerAvatar(MultipartFile file, Long playerId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 按球员 ID 组织目录，文件名带时间戳，每次上传生成唯一文件名
        String objectKey = "players/" + playerId + "/avatar_" + System.currentTimeMillis() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        // 设置为 inline，让浏览器直接预览图片而不是触发下载
        metadata.setContentDisposition("inline");

        ossClient.putObject(bucketName, objectKey, file.getInputStream(), metadata);

        return urlPrefix + "/" + objectKey;
    }
}
