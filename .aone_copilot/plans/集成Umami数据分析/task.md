### 集成Umami数据分析 ###

# 任务清单

## Docker Compose 配置

- [ ] 修改 `springboot-demo/docker-compose.yml`，新增 `umami-db`（PostgreSQL 15）和 `umami` 两个 service，新增 `umami_db_data` volume
- [ ] 修改 `springboot-demo/docker-compose.prod.yml`，同步新增 `umami-db` 和 `umami` service，配置内存限制和 `restart: always`
- [ ] 修改 `springboot-demo/.env.example`，新增 `UMAMI_DB_PASSWORD` 和 `UMAMI_APP_SECRET` 环境变量说明

## 前端埋点工具函数

- [ ] 新建 `frontend-efootball/src/utils/analytics.js`，封装 `trackEvent` 函数和 `EVENTS` 常量

## 前端 Script 标签注入

- [ ] 修改 `frontend-efootball/index.html`，在 `<head>` 中注入 Umami script 标签（含占位符说明注释）

## 前端组件埋点

- [ ] 修改 `frontend-efootball/src/components/FilterPanel.jsx`，在筛选变化和重置时上报对应事件
- [ ] 修改 `frontend-efootball/src/components/PlayerTable.jsx`，在翻页和查看卡面时上报事件
- [ ] 修改 `frontend-efootball/src/pages/PlayerManage.jsx`，在组件挂载时上报管理页面访问事件


updateAtTime: 2026/4/13 19:26:30

planId: 799c5ee1-2a54-4768-8e71-85e6286f79ae