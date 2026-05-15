# 球员数据修改提议系统 设计文档

日期: 2026-05-15

## 背景

当前球员数据只有管理员能改(MD5 密码守卫 `/manage`),社区无法参与修订。本设计加入「修改提议 + 投票 + 审批」流程,在不破坏现有管理员能力的前提下,让所有访问者都能贡献数据修订建议。

## 决策汇总

| 项 | 决定 |
|---|---|
| 用户身份 | 匿名 + 浏览器 localStorage 中生成 UUID(visitorId) |
| 修改单粒度 | 每次提交一张独立卡片 |
| 现有 /manage | 保留(管理员仍可直接增删改),另加 /admin/review |
| 审批动作 | 采纳 + 驳回(都让卡片从瀑布流消失,但用 status 字段保留记录) |
| 排序 | 仅按 approve_count 降序,id 降序为次排序键 |
| 可改字段 | name / position / status / number / club / league / country / height / foot(共 9 个,不含 cardImage) |
| 防滥用 | (player_id, submitter_id, status='pending') 唯一,同一访客对同一球员只能挂 1 条待审批 |
| 投票 | upvote / downvote 二选一,可切换;score = approve_count 单值 |
| 管理员鉴权 | 复用 `playerManageAuth` sessionStorage + 同一 MD5 密码 |
| 兜底图 | 前端 public 目录放静态占位图 `default-card.png` |
| 分页 | 后端 page/pageSize,前端 IntersectionObserver 触底加载 |

## 数据模型

### `player_change_request`

```sql
CREATE TABLE IF NOT EXISTS player_change_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    player_id BIGINT NOT NULL,
    submitter_id VARCHAR(64) NOT NULL COMMENT '匿名访客 UUID',
    proposed_name VARCHAR(100),
    proposed_position VARCHAR(50),
    proposed_status VARCHAR(20),
    proposed_number INT,
    proposed_club VARCHAR(100),
    proposed_league VARCHAR(50),
    proposed_country VARCHAR(50),
    proposed_height INT,
    proposed_foot VARCHAR(10),
    approve_count INT DEFAULT 0,
    disapprove_count INT DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT 'pending/approved/rejected',
    reject_reason VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    INDEX idx_status_score (status, approve_count DESC, id DESC),
    INDEX idx_player_submitter (player_id, submitter_id)
);
```
注:不使用唯一索引(MySQL 唯一索引把 NULL 当作不同的特性无法直接表达"仅 pending 唯一"语义,且会限制同一访客对同一球员只能各有 1 条 approved/rejected,与业务期望不符)。**唯一性靠业务层校验**:写入前 SELECT 同 (player_id, submitter_id) 且 status='pending' 的记录,有则返回 409。低流量场景这种竞态可接受;若并发增加再加 SELECT FOR UPDATE。

### `player_change_vote`

```sql
CREATE TABLE IF NOT EXISTS player_change_vote (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    voter_id VARCHAR(64) NOT NULL,
    vote_type TINYINT NOT NULL COMMENT '1=approve, -1=disapprove',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_voter (request_id, voter_id)
);
```

## 后端设计

### 模块文件

| 模块 | 新增文件 |
|---|---|
| api | `ChangeRequestDTO`, `ChangeVoteDTO`, `request/ChangeRequestSubmitRequest`, `request/ChangeRequestQueryRequest`, `request/VoteRequest`, `request/ReviewRequest` |
| domain | `entity/ChangeRequestEntity`, `entity/ChangeVoteEntity`, `service/ChangeRequestService` |
| repository | `mapper/ChangeRequestMapper`, `mapper/ChangeVoteMapper`, `ChangeRequestRepository`, `ChangeVoteRepository` |
| service | `impl/ChangeRequestServiceImpl`, `executor/ChangeRequestSubmitExe`, `executor/ChangeRequestQueryExe`, `executor/ChangeRequestVoteExe`, `executor/ChangeRequestReviewExe` |
| start | `controller/ChangeRequestController`, `controller/AdminReviewController` |

### REST API

公开接口(任何访客):

| 方法 | 路径 | 用途 |
|---|---|---|
| POST | `/api/change-request` | 提交一条修改提议(body 含 visitorId, playerId, proposed_*) |
| GET | `/api/change-request?pageNum=1&pageSize=10&status=pending&voterId=xxx` | 分页查 pending 列表;返回项内置 player 当前快照(currentName 等)+ 当前 voterId 的投票状态(myVote: 1/-1/null);按 approve_count 排序 |
| GET | `/api/change-request/{id}?voterId=xxx` | 查单条(同样附带快照与 myVote) |
| POST | `/api/change-request/{id}/vote` | 投票(body: voterId, voteType: 1 / -1, 同 voterId 重复点同方向 = 取消;切方向 = 改投) |

管理员接口(`/api/admin/**`,走 `AdminAuthInterceptor`):

| 方法 | 路径 | 用途 |
|---|---|---|
| POST | `/api/admin/change-request/{id}/approve` | 采纳:把 proposed_* 写回 player 表,status=approved |
| POST | `/api/admin/change-request/{id}/reject` | 驳回:status=rejected,可带 reason |

`AdminAuthInterceptor`:校验请求头 `X-Admin-Password`(前端从 sessionStorage 取 MD5 密码,作为请求头传)。MD5 不匹配返回 403。

### 业务规则

**提交**:
- 校验 player 存在;
- 校验该 (player_id, submitter_id) 没有 status='pending' 的记录;
- 校验至少改动一个字段(全部字段对比 player 现有值,若全相同拒绝);
- 落库,初始 approve_count=0, disapprove_count=0;
- 清理相关缓存。

**投票**:
- 查 vote 表是否已有该 (request_id, voter_id):
  - 没有 → 插入,对应计数 +1;
  - 已有同 vote_type → 删除,对应计数 -1(再点取消);
  - 已有不同 vote_type → update,旧计数 -1, 新计数 +1;
- 用 `@Transactional` 包裹,DB 层 update 原子;
- 投票请求会回落计数到 request 表的 approve_count / disapprove_count(冗余字段供排序);
- 仅对 status='pending' 的 request 允许投票。

**采纳**:
- `@Transactional`:
  1. 查 request,要 status='pending';
  2. 把 proposed_* 中**非空**字段覆盖到 player 表对应字段;
  3. 更新 player.update_time;
  4. request.status='approved';
- 清理 player 缓存 + change-request 列表缓存。

**驳回**:
- request.status='rejected', 可写 reason;
- 清理列表缓存。

### 缓存

- 列表查询缓存键:`changeRequestList:status:pageNum:pageSize`,采纳/驳回/提交/投票时全清(`@CacheEvict allEntries`);
- 投票频率较高,**不做缓存**列表查询(让排序实时反映),或将 TTL 设为 30s 平衡 QPS 与实时性。**初版选不缓存**,实测有压力再加。

## 前端设计

### 路由(App.jsx)

```
/                 HomePage(改:每行加「提议修改」按钮)
/changes          ChangeRequestsPage(新:瀑布流)
/manage           PlayerManage(沿用)
/admin/review     AdminReviewPage(新:审批,走 ManageRoute 守卫)
```

顶部 Header 加导航链接:首页 / 修改提议 / 管理。

### 关键组件

`utils/visitorId.js`:
```js
export const getVisitorId = () => {
  let id = localStorage.getItem('visitorId');
  if (!id) {
    id = crypto.randomUUID();
    localStorage.setItem('visitorId', id);
  }
  return id;
};
```

`api/changeRequest.js`:封装上述 5 个公开接口 + 2 个管理员接口。管理员接口的 axios 实例自动从 sessionStorage 读 MD5 写入 `X-Admin-Password` 请求头。

`components/ChangeRequestModal.jsx`:从 HomePage 唤起,预填当前球员的 9 个字段。提交前 diff 出实际改动,空改动按钮 disabled。

`pages/ChangeRequestsPage.jsx`:
- 调 `/api/change-request?pageNum=N` 拿数据;
- 渲染为 CSS column-count 瀑布流(简单方案,不依赖 react-masonry);
- 触底(IntersectionObserver 哨兵)加载下一页;
- 每张卡片用 `ChangeRequestCard` 组件。

`components/ChangeRequestCard.jsx`:
- 默认正面:`<div class="card-front">`,展示 player.cardImage(或兜底图),左上角白底浮层球员姓名;
- 点击翻转(CSS transform: rotateY(180deg) + transform-style: preserve-3d);
- 反面:9 行 diff,格式 `字段名 | 原值 → 新值`。新旧相同 → 灰色;不同 → 原值灰、箭头红、新值红粗;
- 卡片底部 footer:👍 数字 / 👎 数字,点击切换状态。当前 voter 的选择高亮(从 my-votes 接口回填)。

`pages/AdminReviewPage.jsx`:
- 复用 ChangeRequestCard 但传 `mode='review'`(在 footer 区域换成「采纳」「驳回」按钮);
- 采纳:确认弹窗 → 调接口 → 卡片淡出移除;
- 驳回:Modal 输入 reason → 调接口 → 卡片移除;
- 也是瀑布流分页。

### 兜底图

`frontend-efootball/public/default-card.png`(空文件占位即可,后续替换成实际图)。

## 数据流(关键场景)

### 用户提议修改
1. 首页表格行点击「提议修改」 → 打开 Modal,9 个字段表单预填当前值;
2. 用户改其中若干字段 → 点提交 → axios POST `/api/change-request`,body 带 visitorId 和 proposed_* (未改的字段也传当前值或 null,后端只看 diff);
3. 后端校验通过 → 落库 → 返回 DTO;前端 toast 成功。

### 浏览 + 投票
1. 任何访客打开 `/changes`,visitorId 从 localStorage 来;
2. 前端先调列表接口 + `my-votes?voterId=...&requestIds=...` 拿到当前访客的投票回填;
3. 点击 👍:本地立即乐观更新,axios POST 投票接口,失败回滚;同 👎 互斥;
4. 排序由后端在分页时确定,翻页时用最新顺序。

### 管理员审批
1. 管理员打开 `/admin/review`,经 ManageRoute 守卫(密码已在 sessionStorage);
2. 列表与公开瀑布流一致,但 footer 是「采纳/驳回」;
3. 点采纳 → axios POST(自动带 X-Admin-Password) → 后端事务:写回 player + 标记 approved;
4. 卡片从列表移除,Toast「已采纳并落库」。

## 边界与失败模式

- 同访客同球员重复提交:后端 409,前端 Toast「您已对该球员有待审批的提议,请先撤回或等待审批」;
- 提议表单零改动:前端 Submit disabled + 后端兜底 400;
- 投票时 request 已不是 pending(被并发采纳):返回 410 Gone,前端刷新列表;
- 管理员密码失效(用户清了 sessionStorage):拦截器返回 403,前端跳回 PasswordModal;
- 默认图加载失败:`<img onError>` 兜底为透明背景 + 大号球员姓名占位。

## 未做的事(YAGNI)

- 不做撤回/编辑自己的提议(初版用「再提一条」即可);
- 不做评论/讨论功能;
- 不做实时推送(管理员审批后其他用户的页面靠下次翻页/刷新感知);
- 不做投票冷却/防机器人;
- 不做卡面图修改提议(避免 OSS 滥传);
- 不做并发乐观锁(采纳冲突极小)。

## 风险

- 同球员多条 pending 卡片可能"内容互相重叠",采纳第 1 条后第 2 条数据已陈旧 → 用户接受;管理员审批时可参考被采纳条目反向驳回剩余。
- 匿名 UUID 易被清缓存绕过 → 接受这个权衡;真正防刷需登录,超出本次范围。
