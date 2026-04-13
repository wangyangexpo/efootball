### 集成Umami数据分析 ###
在球员筛选系统中集成 Umami 自托管数据分析，通过 Docker Compose 新增 Umami 服务，并在前端埋点追踪页面访问、查询、翻页、筛选条件使用等关键事件，最终通过 Umami 看板查看数据。


# 集成 Umami 数据分析

为球员筛选系统接入 Umami 开源分析平台，实现每日访问流量统计和关键操作事件追踪，通过 Umami 自带看板查看数据。

## Proposed Changes

### Docker Compose — 新增 Umami 服务

> [!NOTE]
> Umami 需要一个独立的 PostgreSQL（或 MySQL）数据库存储分析数据。为避免污染业务 MySQL，新增一个独立的 `umami-db`（PostgreSQL）容器。Umami 官方镜像为 `ghcr.io/umami-software/umami:postgresql-latest`，完全免费开源。

#### [MODIFY] [docker-compose.yml](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/docker-compose.yml)

新增两个 service：
- `umami-db`：PostgreSQL 15，存储 Umami 分析数据，数据持久化到 `umami_db_data` volume
- `umami`：Umami 应用，监听 `3001` 端口，依赖 `umami-db`，通过环境变量 `DATABASE_URL` 连接数据库

#### [MODIFY] [docker-compose.prod.yml](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/docker-compose.prod.yml)

同步新增 `umami-db` 和 `umami` 两个 service，并为 `umami-db` 和 `umami` 配置内存限制（各 256M），`restart: always`。

#### [MODIFY] [.env.example](file:///Users/alsc/Documents/shared/fullstack/springboot-demo/.env.example)

新增 Umami 相关环境变量说明：
```
UMAMI_DB_PASSWORD=your_umami_db_password
UMAMI_APP_SECRET=your_random_secret_string
```

---

### 前端埋点 — 封装 Umami 工具函数

> [!NOTE]
> Umami 提供两种接入方式：① Script 标签自动追踪页面访问；② `window.umami.track()` JS API 手动上报自定义事件。本方案同时使用两种方式：Script 标签负责页面 PV/UV，JS API 负责自定义事件。

#### [NEW] [analytics.js](file:///Users/alsc/Documents/shared/fullstack/frontend-efootball/src/utils/analytics.js)

封装 Umami 事件上报工具函数，对外暴露：
```js
// 安全调用 window.umami.track，避免 umami 未加载时报错
export const trackEvent = (eventName, eventData) => { ... }
```

预定义的事件名常量：
```js
export const EVENTS = {
  QUERY_PLAYER: 'query_player',       // 查询球员（筛选条件变化触发）
  PAGE_CHANGE: 'page_change',         // 翻页
  FILTER_POSITION: 'filter_position', // 筛选位置
  FILTER_LEAGUE: 'filter_league',     // 筛选联赛
  FILTER_CLUB: 'filter_club',         // 筛选俱乐部
  FILTER_COUNTRY: 'filter_country',   // 筛选国家
  FILTER_FOOT: 'filter_foot',         // 筛选惯用脚
  FILTER_STATUS: 'filter_status',     // 筛选现役状态
  FILTER_HEIGHT: 'filter_height',     // 筛选身高
  FILTER_RESET: 'filter_reset',       // 重置筛选
  VIEW_CARD_IMAGE: 'view_card_image', // 查看球员卡面
  MANAGE_PAGE_VISIT: 'manage_page_visit', // 访问管理页面
}
```

---

### 前端埋点 — 注入 Umami Script 标签

#### [MODIFY] [index.html](file:///Users/alsc/Documents/shared/fullstack/frontend-efootball/index.html)

在 `<head>` 中注入 Umami 脚本标签，用于自动追踪页面 PV/UV：
```html
<script
  defer
  src="http://YOUR_ECS_IP:3001/script.js"
  data-website-id="UMAMI_WEBSITE_ID"
></script>
```

> [!IMPORTANT]
> `data-website-id` 需要在 Umami 后台创建网站后获取，部署完成后需手动填入。`src` 中的域名/IP 替换为实际 ECS 地址。

---

### 前端埋点 — FilterPanel 筛选事件

#### [MODIFY] [FilterPanel.jsx](file:///Users/alsc/Documents/shared/fullstack/frontend-efootball/src/components/FilterPanel.jsx)

在 `handleValuesChange` 回调中，针对每个变化的字段上报对应的筛选事件：
- `position` 变化 → `trackEvent(EVENTS.FILTER_POSITION, { value })`
- `league` 变化 → `trackEvent(EVENTS.FILTER_LEAGUE, { value })`
- `club` 变化 → `trackEvent(EVENTS.FILTER_CLUB, { value })`
- `country` 变化 → `trackEvent(EVENTS.FILTER_COUNTRY, { value })`
- `foot` 变化 → `trackEvent(EVENTS.FILTER_FOOT, { value })`
- `status` 变化 → `trackEvent(EVENTS.FILTER_STATUS, { value })`
- `height` 变化 → `trackEvent(EVENTS.FILTER_HEIGHT, { value, operator })`

在 `handleReset` 中上报 `EVENTS.FILTER_RESET`。

在 `handleValuesChange` 末尾上报 `EVENTS.QUERY_PLAYER`（携带当前所有非空筛选条件作为 eventData）。

---

### 前端埋点 — PlayerTable 翻页 & 查看卡面事件

#### [MODIFY] [PlayerTable.jsx](file:///Users/alsc/Documents/shared/fullstack/frontend-efootball/src/components/PlayerTable.jsx)

- 在 `Table` 的 `onChange` 回调中上报 `EVENTS.PAGE_CHANGE`，携带 `{ page, pageSize }`
- 在 `cardImage` 列的 `render` 函数中，为 `<Image>` 添加 `onPreviewClose` / `onClick` 回调，上报 `EVENTS.VIEW_CARD_IMAGE`，携带球员姓名

---

### 前端埋点 — 管理页面访问事件

#### [MODIFY] [PlayerManage.jsx](file:///Users/alsc/Documents/shared/fullstack/frontend-efootball/src/pages/PlayerManage.jsx)

在组件 `useEffect`（mount 时）上报 `EVENTS.MANAGE_PAGE_VISIT`。

---

## Verification Plan

### Automated Tests
无自动化测试，通过手动验证。

### Manual Verification

1. **启动 Umami 服务**：在 ECS 上执行 `docker compose up -d umami-db umami`，访问 `http://ECS_IP:3001`，使用默认账号 `admin / umami` 登录并修改密码
2. **创建网站**：在 Umami 后台「设置 → 网站 → 添加网站」，填入网站名称和域名，获取 `Website ID`
3. **填入 Website ID**：将 `index.html` 中的 `UMAMI_WEBSITE_ID` 替换为实际值，重新构建前端
4. **触发事件验证**：打开前端页面，操作筛选、翻页等，在 Umami 看板「实时」页面确认事件上报成功
5. **看板查看**：在 Umami 看板查看「概览」（PV/UV/访客）、「事件」（各自定义事件触发次数）


updateAtTime: 2026/4/13 19:26:30

planId: 799c5ee1-2a54-4768-8e71-85e6286f79ae