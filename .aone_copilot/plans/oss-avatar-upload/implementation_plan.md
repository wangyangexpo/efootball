### oss-avatar-upload ###
为球员筛选系统接入阿里云 OSS 头像上传能力，包括后端 OSS 集成、上传接口、前端上传组件三部分。

# 接入阿里云 OSS 球员头像上传能力

为球员筛选系统新增头像上传功能，后端通过阿里云 OSS SDK 将图片上传至 OSS Bucket，前端在编辑弹窗中新增上传组件，支持点击上传并实时预览。

## User Review Required

> [!IMPORTANT]
> 执行前请先在本地 `.env` 文件中填入真实的 OSS 配置值：
> - `OSS_ACCESS_KEY_ID`
> - `OSS_ACCESS_KEY_SECRET`
> - `OSS_BUCKET_NAME`
> - `OSS_ENDPOINT`（如 `oss-cn-hangzhou.aliyuncs.com`）
> - `OSS_URL_PREFIX`（如 `https://your-bucket.oss-cn-hangzhou.aliyuncs.com`）

> [!NOTE]
> `PlayerEntity`、`PlayerDTO`、`PlayerCreateRequest`、`PlayerUpdateRequest` 中已有 `cardImage` 字段，`PlayerUpdateExe` 也已处理该字段的更新逻辑，无需改动数据库和这些文件。

## Proposed Changes

### 后端 - 依赖与配置

#### [MODIFY] [pom.xml](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/springboot-demo-start/pom.xml)
在 `springboot-demo-start` 的 `pom.xml` 中添加阿里云 OSS SDK 依赖：
```xml
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>
```

#### [MODIFY] [application.yml](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/springboot-demo-start/src/main/resources/application.yml)
追加文件上传大小限制配置：
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 10MB
```

#### [MODIFY] [application-dev.yml](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/springboot-demo-start/src/main/resources/application-dev.yml)
追加 OSS 配置项（通过环境变量注入）：
```yaml
aliyun:
  oss:
    endpoint: ${OSS_ENDPOINT:oss-cn-hangzhou.aliyuncs.com}
    access-key-id: ${OSS_ACCESS_KEY_ID}
    access-key-secret: ${OSS_ACCESS_KEY_SECRET}
    bucket-name: ${OSS_BUCKET_NAME}
    url-prefix: ${OSS_URL_PREFIX}
```

#### [MODIFY] [.env.example](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/.env.example)
追加 OSS 环境变量示例：
```bash
OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
OSS_ACCESS_KEY_ID=your_access_key_id
OSS_ACCESS_KEY_SECRET=your_access_key_secret
OSS_BUCKET_NAME=your_bucket_name
OSS_URL_PREFIX=https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com
```

#### [MODIFY] [docker-compose.yml](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/docker-compose.yml)
在 `app` 服务的 `environment` 中追加 OSS 环境变量透传。

---

### 后端 - 新增 OSS 配置类与上传 Service

#### [NEW] [OssConfig.java](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/springboot-demo-start/src/main/java/com/example/demo/config/OssConfig.java)
读取 `aliyun.oss.*` 配置，创建 `OSS` Bean：
```java
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
```

#### [NEW] [OssUploadService.java](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/OssUploadService.java)
封装上传逻辑，按球员 ID 组织 OSS 目录，重复上传自动覆盖：
```
players/{playerId}/avatar.{ext}
```

---

### 后端 - 接口层改动

#### [MODIFY] [PlayerService.java](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/springboot-demo-domain/src/main/java/com/example/demo/domain/service/PlayerService.java)
新增 `updateCardImage` 方法，供上传接口内部调用（绕过密码校验）：
```java
Response<Void> updateCardImage(Long id, String imageUrl);
```

#### [MODIFY] [PlayerServiceImpl.java](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/impl/PlayerServiceImpl.java)
实现 `updateCardImage`，直接更新 `cardImage` 字段并清除相关缓存。

#### [MODIFY] [PlayerController.java](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/springboot-demo-start/src/main/java/com/example/demo/controller/PlayerController.java)
新增 `POST /api/player/{id}/avatar` 接口，接收 `multipart/form-data`，校验文件类型（仅允许 image/*）和大小（≤2MB），调用 `OssUploadService` 上传后更新 `cardImage`。

---

### 前端改动

#### [MODIFY] [player.js](file:///Users/alsc/Documents/shared/fullstack/frontend-efootball/src/api/player.js)
新增 `uploadPlayerAvatar` 函数，使用 `FormData` 发送 `multipart/form-data` 请求。

#### [MODIFY] [PlayerManage.jsx](file:///Users/alsc/Documents/shared/fullstack/frontend-efootball/src/pages/PlayerManage.jsx)
在编辑球员的 Modal 中新增头像上传区域：
- 使用 Ant Design `Upload` 组件，`customRequest` 调用 `uploadPlayerAvatar`
- 上传成功后自动更新表单 `cardImage` 字段并显示预览图
- 新增球员时提示"请先保存后再上传头像"

## Verification Plan

### Automated Tests
- 启动后端服务，验证 `/api/player/{id}/avatar` 接口可正常接收文件并返回 OSS URL
- 检查 OSS 控制台 Bucket 中是否生成了 `players/{id}/avatar.jpg` 文件

### Manual Verification
- 在前端编辑弹窗中点击「上传头像」，选择图片，验证上传成功并显示预览
- 刷新页面，验证头像 URL 已持久化到数据库


updateAtTime: 2026/4/14 16:27:29

planId: 50984acf-ab90-4804-a2c4-748234c2dd81