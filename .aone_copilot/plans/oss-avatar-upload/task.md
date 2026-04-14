### oss-avatar-upload ###
# 任务清单

- [ ] 后端依赖与配置
  - [ ] `springboot-demo-start/pom.xml` 添加 OSS SDK 依赖
  - [ ] `application.yml` 追加 multipart 文件大小限制
  - [ ] `application-dev.yml` 追加 OSS 配置项
  - [ ] `.env.example` 追加 OSS 环境变量示例
  - [ ] `docker-compose.yml` 追加 OSS 环境变量透传
- [ ] 后端新增 OSS 配置类与上传 Service
  - [ ] 新建 `OssConfig.java`
  - [ ] 新建 `OssUploadService.java`
- [ ] 后端接口层改动
  - [ ] `PlayerService.java` 新增 `updateCardImage` 方法声明
  - [ ] `PlayerServiceImpl.java` 实现 `updateCardImage`
  - [ ] `PlayerController.java` 新增 `POST /{id}/avatar` 接口
- [ ] 前端改动
  - [ ] `player.js` 新增 `uploadPlayerAvatar` 函数
  - [ ] `PlayerManage.jsx` 编辑弹窗新增头像上传组件与预览


updateAtTime: 2026/4/14 16:27:29

planId: 50984acf-ab90-4804-a2c4-748234c2dd81