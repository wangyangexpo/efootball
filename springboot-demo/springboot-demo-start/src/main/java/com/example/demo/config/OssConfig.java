package com.example.demo.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置类
 * 读取 aliyun.oss.* 配置项，创建 OSS 客户端 Bean
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssConfig {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String urlPrefix;

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
