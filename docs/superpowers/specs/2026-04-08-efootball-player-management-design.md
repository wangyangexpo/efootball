# efootball 球员管理系统设计文档

## 设计概述

将 springboot-demo 改造为 efootball 球员管理后台服务，并将 frontend-efootball 改造为从后端 API 获取数据的完整前后端分离系统。

### 改造范围

| 项目 | 改造内容 |
|------|----------|
| **springboot-demo** | 新增 Player 模块，提供球员 CRUD API |
| **frontend-efootball** | 改造为从 API 获取数据，新增管理页面 |

---

## 后端设计

### 整体架构

在现有 COLA 架构基础上新增 Player 模块，保持与 User 模块相同的分层结构：

| 层级 | 新增类 | 职责 |
|------|--------|------|
| **api** | PlayerDTO, PlayerQueryRequest, PlayerCreateRequest, PlayerUpdateRequest, PlayerEnumsResponse | 对外接口定义 |
| **common** | 无新增 | 复用现有 PageResult、Response |
| **domain** | PlayerEntity, PlayerService | 领域模型和服务接口 |
| **repository** | PlayerMapper, PlayerRepository | 数据访问层 |
| **service** | PlayerServiceImpl, PlayerQueryExe, PlayerCreateExe, PlayerUpdateExe | 业务逻辑实现 |
| **start** | PlayerController | REST 控制器 |

---

### 数据库设计

#### player 表结构

```sql
CREATE TABLE player (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '球员姓名',
    position VARCHAR(50) NOT NULL COMMENT '位置：门将/中后卫/后腰/中前卫/中锋等',
    status VARCHAR(20) NOT NULL COMMENT '现役状态：现役/历史',
    number INT COMMENT '球衣号码',
    club VARCHAR(100) COMMENT '所属俱乐部',
    league VARCHAR(50) COMMENT '所在联赛',
    country VARCHAR(50) COMMENT '国家队',
    height INT COMMENT '身高',
    foot VARCHAR(10) COMMENT '惯用脚：左/右',
    card_image VARCHAR(500) COMMENT '球员卡面图片URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='球员表';
```

#### 初始数据导入

- 从 `players.json` 提取所有球员数据生成 INSERT 语句
- 更新 `init.sql` 文件，包含建表语句和数据插入

---

### API 接口设计

#### 接口列表

| 接口 | 方法 | 路径 | 功能 |
|------|------|------|------|
| 获取枚举 | GET | `/api/player/enums` | 返回所有筛选枚举值 |
| 分页查询 | GET | `/api/player` | 分页查询球员列表，支持筛选 |
| 查询详情 | GET | `/api/player/{id}` | 查询单个球员详情 |
| 新增球员 | POST | `/api/player` | 新增球员 |
| 修改球员 | POST | `/api/player/{id}` | 修改球员信息 |
| 删除球员 | POST | `/api/player/delete/{id}` | 删除球员 |

#### 接口详情

**1. 获取枚举 `GET /api/player/enums`**

```json
// 响应
{
  "code": 200,
  "message": "success",
  "data": {
    "positions": ["门将", "中后卫", "后腰", "中前卫", "中锋", ...],
    "statuses": ["现役", "历史"],
    "leagues": ["意甲", "英超", "西甲", ...],
    "clubs": ["AC米兰", "阿森纳", "巴萨", ...],
    "countries": ["荷兰", "巴西", "意大利", ...],
    "foots": ["左", "右"]
  }
}
```

**枚举值获取逻辑：** 从数据库查询所有球员数据，去重提取各字段唯一值作为枚举。

---

**2. 分页查询 `GET /api/player`**

```json
// 请求参数（Query）
{
  "position": "后腰",        // 可选
  "status": "现役",          // 可选
  "number": 8,               // 可选
  "league": "意甲",          // 可选
  "club": "AC米兰",          // 可选
  "country": "意大利",       // 可选
  "foot": "右",              // 可选
  "height": 180,             // 可选
  "heightOperator": "=",     // 可选：=、+、-
  "pageNum": 1,
  "pageSize": 20
}

// 响应
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 20,
    "records": [
      {
        "id": 1,
        "name": "里杰卡尔德",
        "position": "后腰",
        "status": "历史",
        "number": 8,
        "club": "AC米兰",
        "league": "意甲",
        "country": "荷兰",
        "height": 190,
        "foot": "右",
        "cardImage": null
      }
    ]
  }
}
```

---

**3. 查询详情 `GET /api/player/{id}`**

```json
// 响应
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "里杰卡尔德",
    "position": "后腰",
    "status": "历史",
    "number": 8,
    "club": "AC米兰",
    "league": "意甲",
    "country": "荷兰",
    "height": 190,
    "foot": "右",
    "cardImage": null
  }
}
```

---

**4. 新增球员 `POST /api/player`**

```json
// 请求体
{
  "name": "梅西",
  "position": "右边锋",
  "status": "现役",
  "number": 10,
  "club": "迈阿密国际",
  "league": "美职联",
  "country": "阿根廷",
  "height": 170,
  "foot": "左",
  "cardImage": "https://xxx.com/messi.jpg",  // 可选
  "password": "wangyangexpo"  // 安全验证密码
}

// 响应
{
  "code": 200,
  "message": "success",
  "data": { ...新增后的球员信息 }
}
```

---

**5. 修改球员 `POST /api/player/{id}`**

```json
// URL: /api/player/1
// 请求体（字段均可选，只传需要修改的字段）
{
  "name": "梅西",
  "club": "巴萨",
  "league": "西甲",
  "cardImage": "https://xxx.com/messi_new.jpg",
  "password": "wangyangexpo"  // 安全验证密码
}

// 响应
{
  "code": 200,
  "message": "success",
  "data": { ...修改后的球员信息 }
}
```

---

**6. 删除球员 `POST /api/player/delete/{id}`**

```json
// URL: /api/player/delete/1
// 请求体
{
  "password": "wangyangexpo"  // 安全验证密码
}

// 响应
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

### 安全机制

增删改接口需校验密码：
- 接口接收 `password` 参数
- 校验 `password == "wangyangexpo"` 才允许操作
- 校验失败返回 `code: 401, message: "密码验证失败"`

---

## 前端设计

### 改动文件列表

| 文件 | 改动内容 |
|------|----------|
| `vite.config.js` | 新增后端 API 代理配置 |
| `package.json` | 新增 axios 依赖 |
| `src/App.jsx` | 新增页面路由管理，主页面和管管理页面切换 |
| `src/api/player.js` | 新增 API 请求封装模块 |
| `src/components/FilterPanel.jsx` | 枚举值从后端 `/api/player/enums` 获取 |
| `src/components/PlayerTable.jsx` | 改为后端分页模式，适配 API 数据结构 |
| `src/pages/PlayerManage.jsx` | 新增球员管理页面（新增/修改/删除功能） |
| `src/components/PasswordModal.jsx` | 新增密码验证弹窗组件 |

---

### 页面结构

```
App.jsx (主应用)
├── 主页面 (球员筛选展示)
│   ├── FilterPanel.jsx
│   └── PlayerTable.jsx
│
└── 管理页面 (PlayerManage.jsx)
    ├── 密码验证入口 (PasswordModal.jsx)
    └── 球员管理功能 (CRUD 操作)
```

---

### 路由与导航设计

在 App.jsx 中实现简单路由切换：

```javascript
// App.jsx 状态管理
const [currentPage, setCurrentPage] = useState('main');  // 'main' 或 'manage'
const [isAuthenticated, setIsAuthenticated] = useState(
  sessionStorage.getItem('playerManageAuth') === 'true'
);

// Header 导航
<Header>
  <Button onClick={() => setCurrentPage('main')}>球员查询</Button>
  <Button onClick={() => setCurrentPage('manage')}>球员管理</Button>
</Header>

// 页面切换
{currentPage === 'main' && <MainPage />}
{currentPage === 'manage' && (
  isAuthenticated ? <PlayerManage /> : <PasswordModal />
)}
```

---

### 密码验证流程

```
用户点击"球员管理"
    ↓
显示 PasswordModal 弹窗
    ↓
用户输入密码
    ↓
校验密码是否为 "wangyangexpo"
    ↓
正确 → setIsAuthenticated(true)，存入 sessionStorage，显示 PlayerManage 页面
错误 → 提示密码错误，可重新输入
```

**密码存储方式：**
- 前端：验证成功后将状态存入 `sessionStorage`，页面刷新后需重新验证
- 后端：增删改接口需携带密码参数，后端二次校验（双重保护）

---

### PlayerManage.jsx 管理页面功能

1. **球员列表表格** - 展示所有球员，支持分页和简单筛选
2. **新增球员按钮** - 弹出新增表单 Modal
3. **修改球员按钮** - 每行数据带修改按钮，弹出修改表单 Modal
4. **删除球员按钮** - 每行数据带删除按钮，确认后删除

**新增/修改表单字段：**
- 姓名（必填）、位置（必填）、现役状态（必填）、球衣号码、俱乐部、联赛、国家队、身高、惯用脚、卡面图片URL

---

### Vite 代理配置

```javascript
// vite.config.js
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

---

### API 封装模块

```javascript
// src/api/player.js
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
});

const PASSWORD = 'wangyangexpo';

// 获取枚举
export const getPlayerEnums = () => api.get('/player/enums');

// 分页查询球员
export const getPlayers = (params) => api.get('/player', { params });

// 查询球员详情
export const getPlayerById = (id) => api.get(`/player/${id}`);

// 新增球员
export const createPlayer = (data) => api.post('/player', { 
  ...data, 
  password: PASSWORD 
});

// 修改球员
export const updatePlayer = (id, data) => api.post(`/player/${id}`, { 
  ...data, 
  password: PASSWORD 
});

// 删除球员
export const deletePlayer = (id) => api.post(`/player/delete/${id}`, { 
  password: PASSWORD 
});
```

---

## 数据流

```
前端 FilterPanel/App.jsx
    ↓ HTTP请求 (带筛选参数)
后端 PlayerController
    ↓ 调用 Service
PlayerServiceImpl
    ↓ 调用 Executor/Repository
PlayerRepository → PlayerMapper → MySQL
    ↓ 返回结果
前端 PlayerTable 展示分页数据
```

---

## 设计决策记录

| 决策点 | 选择 | 理由 |
|--------|------|------|
| 球员唯一标识 | 自增 ID | 数据库标准做法，查询修改更便捷 |
| 初始数据导入 | SQL 脚本 | 可通过 Docker 初始化或手动执行，可控性强 |
| 前端请求方式 | Vite 代理 | 开发环境统一代理，避免 CORS 问题 |
| 篮选逻辑位置 | 后端筛选 | 数据量大时性能更好，分页准确 |
| 管理模块方式 | 完全新建 Player 模块 | 保持 User 模块参考价值，结构清晰 |
| 密码验证位置 | 页面入口验证 | 进入时一次验证，后续操作流畅 |
| HTTP 方法规范 | 查询用 GET，增删改用 POST | 遵循用户要求，语义清晰 |