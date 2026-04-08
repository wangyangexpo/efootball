# efootball 球员管理系统实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 springboot-demo 改造为球员管理后台，frontend-efootball 改造为从 API 获取数据的前端系统

**Architecture:** 后端采用 COLA 分层架构新增 Player 模块，前端通过 Vite 代理调用后端 API，新增管理页面带密码验证

**Tech Stack:** Spring Boot 2.7.18, MyBatis Plus, React 18, Ant Design 5, Axios, Vite 5

---

## 文件结构概览

### 后端新增文件

| 文件路径 | 职责 |
|----------|------|
| `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/PlayerDTO.java` | 球员数据传输对象 |
| `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/PlayerEnumsResponse.java` | 枚举值响应对象 |
| `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request/PlayerQueryRequest.java` | 分页查询请求 |
| `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request/PlayerCreateRequest.java` | 新增球员请求 |
| `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request/PlayerUpdateRequest.java` | 修改球员请求 |
| `springboot-demo/springboot-demo-domain/src/main/java/com/example/demo/domain/entity/PlayerEntity.java` | 球员实体类 |
| `springboot-demo/springboot-demo-domain/src/main/java/com/example/demo/domain/service/PlayerService.java` | 球员服务接口 |
| `springboot-demo/springboot-demo-repository/src/main/java/com/example/demo/repository/mapper/PlayerMapper.java` | MyBatis Mapper |
| `springboot-demo/springboot-demo-repository/src/main/java/com/example/demo/repository/PlayerRepository.java` | 球员数据访问层 |
| `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor/PlayerQueryExe.java` | 查询执行器 |
| `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor/PlayerCreateExe.java` | 新增执行器 |
| `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor/PlayerUpdateExe.java` | 更新执行器 |
| `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/impl/PlayerServiceImpl.java` | 服务实现 |
| `springboot-demo/springboot-demo-start/src/main/java/com/example/demo/controller/PlayerController.java` | REST 控制器 |

### 后端修改文件

| 文件路径 | 修改内容 |
|----------|----------|
| `springboot-demo/springboot-demo-common/src/main/java/com/example/demo/common/ResultCode.java` | 新增 PLAYER 相关错误码 |
| `springboot-demo/sql/init.sql` | 新增 player 表和初始数据 |

### 前端新增文件

| 文件路径 | 职责 |
|----------|------|
| `frontend-efootball/src/api/player.js` | API 请求封装 |
| `frontend-efootball/src/pages/PlayerManage.jsx` | 球员管理页面 |
| `frontend-efootball/src/components/PasswordModal.jsx` | 密码验证弹窗 |

### 前端修改文件

| 文件路径 | 修改内容 |
|----------|----------|
| `frontend-efootball/package.json` | 新增 axios 依赖 |
| `frontend-efootball/vite.config.js` | 新增 API 代理 |
| `frontend-efootball/src/App.jsx` | 路由管理、API 数据获取 |
| `frontend-efootball/src/App.css` | 新增管理页面样式 |
| `frontend-efootball/src/components/FilterPanel.jsx` | 枚举从 API 获取 |
| `frontend-efootball/src/components/PlayerTable.jsx` | 后端分页适配 |

---

## Phase 1: 后端 API 层

### Task 1: 新增 PLAYER 错误码

**Files:**
- Modify: `springboot-demo/springboot-demo-common/src/main/java/com/example/demo/common/ResultCode.java:15-16`

- [ ] **Step 1: 在 ResultCode.java 中新增 PLAYER 错误码**

```java
package com.example.demo.common;

import lombok.Getter;

/**
 * Response code enumeration
 */
@Getter
public enum ResultCode {

    SUCCESS("200", "Success"),
    FAIL("500", "Internal Server Error"),
    NOT_FOUND("404", "Resource Not Found"),
    BAD_REQUEST("400", "Bad Request"),
    USER_NOT_FOUND("1001", "User Not Found"),
    USER_ALREADY_EXISTS("1002", "User Already Exists"),
    PLAYER_NOT_FOUND("2001", "Player Not Found"),
    PLAYER_PASSWORD_INVALID("2002", "密码验证失败");

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

---

### Task 2: 创建 PlayerDTO

**Files:**
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/PlayerDTO.java`

- [ ] **Step 1: 创建 PlayerDTO.java**

```java
package com.example.demo.api;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Player Data Transfer Object
 */
@Data
public class PlayerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String position;
    private String status;
    private Integer number;
    private String club;
    private String league;
    private String country;
    private Integer height;
    private String foot;
    private String cardImage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

---

### Task 3: 创建 PlayerEnumsResponse

**Files:**
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/PlayerEnumsResponse.java`

- [ ] **Step 1: 创建 PlayerEnumsResponse.java**

```java
package com.example.demo.api;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Player enums response for filter options
 */
@Data
public class PlayerEnumsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> positions;
    private List<String> statuses;
    private List<String> leagues;
    private List<String> clubs;
    private List<String> countries;
    private List<String> foots;
}
```

---

### Task 4: 创建 PlayerQueryRequest

**Files:**
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request/PlayerQueryRequest.java`

- [ ] **Step 1: 创建 PlayerQueryRequest.java**

```java
package com.example.demo.api.request;

import lombok.Data;
import java.io.Serializable;

/**
 * Player query request with pagination and filters
 */
@Data
public class PlayerQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // 球员位置
    private String position;

    // 现役状态：现役/历史
    private String status;

    // 球衣号码
    private Integer number;

    // 所在联赛
    private String league;

    // 俱乐部
    private String club;

    // 国家队
    private String country;

    // 惯用脚
    private String foot;

    // 身高
    private Integer height;

    // 身高条件：=、+、-
    private String heightOperator;

    // 分页参数
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
```

---

### Task 5: 创建 PlayerCreateRequest

**Files:**
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request/PlayerCreateRequest.java`

- [ ] **Step 1: 创建 PlayerCreateRequest.java**

```java
package com.example.demo.api.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Player creation request
 */
@Data
public class PlayerCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "球员姓名不能为空")
    @Size(min = 1, max = 100, message = "球员姓名长度必须在1-100之间")
    private String name;

    @NotBlank(message = "球员位置不能为空")
    private String position;

    @NotBlank(message = "现役状态不能为空")
    private String status;

    private Integer number;

    private String club;

    private String league;

    private String country;

    private Integer height;

    private String foot;

    private String cardImage;

    // 安全验证密码
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

---

### Task 6: 创建 PlayerUpdateRequest

**Files:**
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request/PlayerUpdateRequest.java`

- [ ] **Step 1: 创建 PlayerUpdateRequest.java**

```java
package com.example.demo.api.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Player update request
 */
@Data
public class PlayerUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(min = 1, max = 100, message = "球员姓名长度必须在1-100之间")
    private String name;

    private String position;

    private String status;

    private Integer number;

    private String club;

    private String league;

    private String country;

    private Integer height;

    private String foot;

    private String cardImage;

    // 安全验证密码
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

---

## Phase 2: 后端 Domain 层

### Task 7: 创建 PlayerEntity

**Files:**
- Create: `springboot-demo/springboot-demo-domain/src/main/java/com/example/demo/domain/entity/PlayerEntity.java`

- [ ] **Step 1: 创建 PlayerEntity.java**

```java
package com.example.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Player domain entity
 */
@Data
@TableName("player")
public class PlayerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String position;

    private String status;

    private Integer number;

    private String club;

    private String league;

    private String country;

    private Integer height;

    private String foot;

    private String cardImage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
```

---

### Task 8: 创建 PlayerService 接口

**Files:**
- Create: `springboot-demo/springboot-demo-domain/src/main/java/com/example/demo/domain/service/PlayerService.java`

- [ ] **Step 1: 创建 PlayerService.java**

```java
package com.example.demo.domain.service;

import com.example.demo.api.PlayerDTO;
import com.example.demo.api.PlayerEnumsResponse;
import com.example.demo.api.request.PlayerQueryRequest;
import com.example.demo.api.request.PlayerCreateRequest;
import com.example.demo.api.request.PlayerUpdateRequest;
import com.example.demo.api.Response;
import com.example.demo.common.PageResult;

/**
 * Player service interface
 */
public interface PlayerService {

    /**
     * Get all enums for filter options
     */
    Response<PlayerEnumsResponse> getEnums();

    /**
     * List players with pagination and filters
     */
    Response<PageResult<PlayerDTO>> list(PlayerQueryRequest request);

    /**
     * Get player by ID
     */
    Response<PlayerDTO> getById(Long id);

    /**
     * Create player
     */
    Response<PlayerDTO> create(PlayerCreateRequest request);

    /**
     * Update player
     */
    Response<PlayerDTO> update(Long id, PlayerUpdateRequest request);

    /**
     * Delete player
     */
    Response<Void> delete(Long id, String password);
}
```

---

## Phase 3: 后端 Repository 层

### Task 9: 创建 PlayerMapper

**Files:**
- Create: `springboot-demo/springboot-demo-repository/src/main/java/com/example/demo/repository/mapper/PlayerMapper.java`

- [ ] **Step 1: 创建 PlayerMapper.java**

```java
package com.example.demo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.domain.entity.PlayerEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Player MyBatis mapper interface
 */
@Mapper
public interface PlayerMapper extends BaseMapper<PlayerEntity> {

}
```

---

### Task 10: 创建 PlayerRepository

**Files:**
- Create: `springboot-demo/springboot-demo-repository/src/main/java/com/example/demo/repository/PlayerRepository.java`

- [ ] **Step 1: 创建 PlayerRepository.java**

```java
package com.example.demo.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.mapper.PlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Player repository abstraction
 */
@Repository
public class PlayerRepository {

    @Autowired
    private PlayerMapper playerMapper;

    public PlayerEntity selectById(Long id) {
        return playerMapper.selectById(id);
    }

    public int insert(PlayerEntity entity) {
        return playerMapper.insert(entity);
    }

    public int updateById(PlayerEntity entity) {
        return playerMapper.updateById(entity);
    }

    public int deleteById(Long id) {
        return playerMapper.deleteById(id);
    }

    public List<PlayerEntity> selectList(LambdaQueryWrapper<PlayerEntity> wrapper) {
        return playerMapper.selectList(wrapper);
    }

    public Page<PlayerEntity> selectPage(Page<PlayerEntity> page, LambdaQueryWrapper<PlayerEntity> wrapper) {
        return playerMapper.selectPage(page, wrapper);
    }

    /**
     * Select all players for enum extraction
     */
    public List<PlayerEntity> selectAll() {
        return playerMapper.selectList(new LambdaQueryWrapper<>());
    }
}
```

---

## Phase 4: 后端 Service 层

### Task 11: 创建 PlayerQueryExe

**Files:**
- Create: `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor/PlayerQueryExe.java`

- [ ] **Step 1: 创建 PlayerQueryExe.java**

```java
package com.example.demo.service.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.api.PlayerDTO;
import com.example.demo.api.PlayerEnumsResponse;
import com.example.demo.api.request.PlayerQueryRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.TreeSet;

/**
 * Player query executor
 */
@Component
public class PlayerQueryExe {

    @Autowired
    private PlayerRepository playerRepository;

    public PlayerDTO getById(Long id) {
        PlayerEntity entity = playerRepository.selectById(id);
        if (entity == null) {
            return null;
        }
        return convertToDTO(entity);
    }

    public PageResult<PlayerDTO> list(PlayerQueryRequest request) {
        LambdaQueryWrapper<PlayerEntity> wrapper = new LambdaQueryWrapper<>();

        // Filter conditions
        if (request.getPosition() != null) {
            wrapper.eq(PlayerEntity::getPosition, request.getPosition());
        }
        if (request.getStatus() != null) {
            wrapper.eq(PlayerEntity::getStatus, request.getStatus());
        }
        if (request.getNumber() != null) {
            wrapper.eq(PlayerEntity::getNumber, request.getNumber());
        }
        if (request.getLeague() != null) {
            wrapper.eq(PlayerEntity::getLeague, request.getLeague());
        }
        if (request.getClub() != null) {
            wrapper.eq(PlayerEntity::getClub, request.getClub());
        }
        if (request.getCountry() != null) {
            wrapper.eq(PlayerEntity::getCountry, request.getCountry());
        }
        if (request.getFoot() != null) {
            wrapper.eq(PlayerEntity::getFoot, request.getFoot());
        }

        // Height filter with operator
        if (request.getHeight() != null) {
            String operator = request.getHeightOperator();
            if (operator == null || operator.equals("=")) {
                wrapper.eq(PlayerEntity::getHeight, request.getHeight());
            } else if (operator.equals("+")) {
                wrapper.gt(PlayerEntity::getHeight, request.getHeight());
            } else if (operator.equals("-")) {
                wrapper.lt(PlayerEntity::getHeight, request.getHeight());
            }
        }

        // Pagination
        Page<PlayerEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<PlayerEntity> result = playerRepository.selectPage(page, wrapper);

        // Convert to DTOs
        List<PlayerDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return PageResult.of(dtoList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    /**
     * Get all enums from player data
     */
    public PlayerEnumsResponse getEnums() {
        List<PlayerEntity> allPlayers = playerRepository.selectAll();

        PlayerEnumsResponse response = new PlayerEnumsResponse();

        // Use TreeSet for sorted unique values
        response.setPositions(allPlayers.stream()
                .map(PlayerEntity::getPosition)
                .filter(p -> p != null)
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().collect(Collectors.toList()));

        response.setStatuses(List.of("现役", "历史"));

        response.setLeagues(allPlayers.stream()
                .map(PlayerEntity::getLeague)
                .filter(l -> l != null && !l.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().collect(Collectors.toList()));

        response.setClubs(allPlayers.stream()
                .map(PlayerEntity::getClub)
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().collect(Collectors.toList()));

        response.setCountries(allPlayers.stream()
                .map(PlayerEntity::getCountry)
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().collect(Collectors.toList()));

        response.setFoots(List.of("左", "右"));

        return response;
    }

    public void deleteById(Long id) {
        playerRepository.deleteById(id);
    }

    private PlayerDTO convertToDTO(PlayerEntity entity) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPosition(entity.getPosition());
        dto.setStatus(entity.getStatus());
        dto.setNumber(entity.getNumber());
        dto.setClub(entity.getClub());
        dto.setLeague(entity.getLeague());
        dto.setCountry(entity.getCountry());
        dto.setHeight(entity.getHeight());
        dto.setFoot(entity.getFoot());
        dto.setCardImage(entity.getCardImage());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}
```

---

### Task 12: 创建 PlayerCreateExe

**Files:**
- Create: `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor/PlayerCreateExe.java`

- [ ] **Step 1: 创建 PlayerCreateExe.java**

```java
package com.example.demo.service.executor;

import com.example.demo.api.PlayerDTO;
import com.example.demo.api.request.PlayerCreateRequest;
import com.example.demo.common.Constants;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Player create command executor
 */
@Component
public class PlayerCreateExe {

    @Autowired
    private PlayerRepository playerRepository;

    public PlayerDTO execute(PlayerCreateRequest request) {
        // Create entity
        PlayerEntity entity = new PlayerEntity();
        entity.setName(request.getName());
        entity.setPosition(request.getPosition());
        entity.setStatus(request.getStatus());
        entity.setNumber(request.getNumber());
        entity.setClub(request.getClub());
        entity.setLeague(request.getLeague());
        entity.setCountry(request.getCountry());
        entity.setHeight(request.getHeight());
        entity.setFoot(request.getFoot());
        entity.setCardImage(request.getCardImage());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);

        // Save
        playerRepository.insert(entity);

        // Convert to DTO
        PlayerDTO dto = new PlayerDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPosition(entity.getPosition());
        dto.setStatus(entity.getStatus());
        dto.setNumber(entity.getNumber());
        dto.setClub(entity.getClub());
        dto.setLeague(entity.getLeague());
        dto.setCountry(entity.getCountry());
        dto.setHeight(entity.getHeight());
        dto.setFoot(entity.getFoot());
        dto.setCardImage(entity.getCardImage());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());

        return dto;
    }
}
```

---

### Task 13: 创建 PlayerUpdateExe

**Files:**
- Create: `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor/PlayerUpdateExe.java`

- [ ] **Step 1: 创建 PlayerUpdateExe.java**

```java
package com.example.demo.service.executor;

import com.example.demo.api.PlayerDTO;
import com.example.demo.api.request.PlayerUpdateRequest;
import com.example.demo.domain.entity.PlayerEntity;
import com.example.demo.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Player update command executor
 */
@Component
public class PlayerUpdateExe {

    @Autowired
    private PlayerRepository playerRepository;

    public PlayerDTO execute(Long id, PlayerUpdateRequest request) {
        PlayerEntity entity = playerRepository.selectById(id);
        if (entity == null) {
            throw new RuntimeException("Player not found with id: " + id);
        }

        // Update fields if provided
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getPosition() != null) {
            entity.setPosition(request.getPosition());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getNumber() != null) {
            entity.setNumber(request.getNumber());
        }
        if (request.getClub() != null) {
            entity.setClub(request.getClub());
        }
        if (request.getLeague() != null) {
            entity.setLeague(request.getLeague());
        }
        if (request.getCountry() != null) {
            entity.setCountry(request.getCountry());
        }
        if (request.getHeight() != null) {
            entity.setHeight(request.getHeight());
        }
        if (request.getFoot() != null) {
            entity.setFoot(request.getFoot());
        }
        if (request.getCardImage() != null) {
            entity.setCardImage(request.getCardImage());
        }
        entity.setUpdateTime(LocalDateTime.now());

        // Save
        playerRepository.updateById(entity);

        // Convert to DTO
        PlayerDTO dto = new PlayerDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPosition(entity.getPosition());
        dto.setStatus(entity.getStatus());
        dto.setNumber(entity.getNumber());
        dto.setClub(entity.getClub());
        dto.setLeague(entity.getLeague());
        dto.setCountry(entity.getCountry());
        dto.setHeight(entity.getHeight());
        dto.setFoot(entity.getFoot());
        dto.setCardImage(entity.getCardImage());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());

        return dto;
    }
}
```

---

### Task 14: 创建 PlayerServiceImpl

**Files:**
- Create: `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/impl/PlayerServiceImpl.java`

- [ ] **Step 1: 创建 PlayerServiceImpl.java**

```java
package com.example.demo.service.impl;

import com.example.demo.api.Response;
import com.example.demo.api.PlayerDTO;
import com.example.demo.api.PlayerEnumsResponse;
import com.example.demo.api.request.PlayerQueryRequest;
import com.example.demo.api.request.PlayerCreateRequest;
import com.example.demo.api.request.PlayerUpdateRequest;
import com.example.demo.common.PageResult;
import com.example.demo.common.ResultCode;
import com.example.demo.domain.service.PlayerService;
import com.example.demo.service.executor.PlayerCreateExe;
import com.example.demo.service.executor.PlayerQueryExe;
import com.example.demo.service.executor.PlayerUpdateExe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Player service implementation
 */
@Service
public class PlayerServiceImpl implements PlayerService {

    private static final String MANAGE_PASSWORD = "wangyangexpo";

    @Autowired
    private PlayerCreateExe playerCreateExe;

    @Autowired
    private PlayerUpdateExe playerUpdateExe;

    @Autowired
    private PlayerQueryExe playerQueryExe;

    @Override
    public Response<PlayerEnumsResponse> getEnums() {
        PlayerEnumsResponse enums = playerQueryExe.getEnums();
        return Response.success(enums);
    }

    @Override
    public Response<PageResult<PlayerDTO>> list(PlayerQueryRequest request) {
        PageResult<PlayerDTO> result = playerQueryExe.list(request);
        return Response.success(result);
    }

    @Override
    public Response<PlayerDTO> getById(Long id) {
        PlayerDTO dto = playerQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.PLAYER_NOT_FOUND.getCode(), ResultCode.PLAYER_NOT_FOUND.getMessage());
        }
        return Response.success(dto);
    }

    @Override
    public Response<PlayerDTO> create(PlayerCreateRequest request) {
        // Validate password
        if (!MANAGE_PASSWORD.equals(request.getPassword())) {
            return Response.fail(ResultCode.PLAYER_PASSWORD_INVALID.getCode(), ResultCode.PLAYER_PASSWORD_INVALID.getMessage());
        }

        PlayerDTO dto = playerCreateExe.execute(request);
        return Response.success(dto);
    }

    @Override
    public Response<PlayerDTO> update(Long id, PlayerUpdateRequest request) {
        // Validate password
        if (!MANAGE_PASSWORD.equals(request.getPassword())) {
            return Response.fail(ResultCode.PLAYER_PASSWORD_INVALID.getCode(), ResultCode.PLAYER_PASSWORD_INVALID.getMessage());
        }

        try {
            PlayerDTO dto = playerUpdateExe.execute(id, request);
            return Response.success(dto);
        } catch (RuntimeException e) {
            return Response.fail(ResultCode.PLAYER_NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @Override
    public Response<Void> delete(Long id, String password) {
        // Validate password
        if (!MANAGE_PASSWORD.equals(password)) {
            return Response.fail(ResultCode.PLAYER_PASSWORD_INVALID.getCode(), ResultCode.PLAYER_PASSWORD_INVALID.getMessage());
        }

        PlayerDTO dto = playerQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.PLAYER_NOT_FOUND.getCode(), ResultCode.PLAYER_NOT_FOUND.getMessage());
        }

        playerQueryExe.deleteById(id);
        return Response.success();
    }
}
```

---

## Phase 5: 后端 Controller 层

### Task 15: 创建 PlayerController

**Files:**
- Create: `springboot-demo/springboot-demo-start/src/main/java/com/example/demo/controller/PlayerController.java`

- [ ] **Step 1: 创建 PlayerController.java**

```java
package com.example.demo.controller;

import com.example.demo.api.Response;
import com.example.demo.api.PlayerDTO;
import com.example.demo.api.PlayerEnumsResponse;
import com.example.demo.api.request.PlayerQueryRequest;
import com.example.demo.api.request.PlayerCreateRequest;
import com.example.demo.api.request.PlayerUpdateRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Player REST Controller
 */
@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    /**
     * Get all enums for filter options
     */
    @GetMapping("/enums")
    public Response<PlayerEnumsResponse> getEnums() {
        return playerService.getEnums();
    }

    /**
     * List players with pagination and filters
     */
    @GetMapping
    public Response<PageResult<PlayerDTO>> list(@ModelAttribute PlayerQueryRequest request) {
        return playerService.list(request);
    }

    /**
     * Get player by ID
     */
    @GetMapping("/{id}")
    public Response<PlayerDTO> getById(@PathVariable Long id) {
        return playerService.getById(id);
    }

    /**
     * Create player
     */
    @PostMapping
    public Response<PlayerDTO> create(@Valid @RequestBody PlayerCreateRequest request) {
        return playerService.create(request);
    }

    /**
     * Update player
     */
    @PostMapping("/{id}")
    public Response<PlayerDTO> update(@PathVariable Long id, @Valid @RequestBody PlayerUpdateRequest request) {
        return playerService.update(id, request);
    }

    /**
     * Delete player
     */
    @PostMapping("/delete/{id}")
    public Response<Void> delete(@PathVariable Long id, @RequestBody(required = false) java.util.Map<String, String> body) {
        String password = body != null ? body.get("password") : null;
        return playerService.delete(id, password);
    }
}
```

---

## Phase 6: 数据库初始化

### Task 16: 更新 init.sql 添加 player 表和数据

**Files:**
- Modify: `springboot-demo/sql/init.sql`

- [ ] **Step 1: 添加 player 表结构到 init.sql**

在现有 init.sql 文件末尾追加以下内容：

```sql
-- Create database (if not exists)
CREATE DATABASE IF NOT EXISTS springboot_demo DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE springboot_demo;

-- Create user table (existing)
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key',
    username VARCHAR(50) NOT NULL COMMENT 'Username',
    email VARCHAR(100) COMMENT 'Email address',
    status INT DEFAULT 1 COMMENT 'Status: 1-active, 0-inactive',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted INT DEFAULT 0 COMMENT 'Logical delete flag: 0-not deleted, 1-deleted'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User table';

-- Insert demo data for user
INSERT INTO user (username, email, status) VALUES ('demo_user', 'demo@example.com', 1);
INSERT INTO user (username, email, status) VALUES ('test_user', 'test@example.com', 1);

-- Create player table
CREATE TABLE IF NOT EXISTS player (
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

- [ ] **Step 2: 生成球员数据 INSERT 语句**

需要从 `frontend-efootball/src/data/players.json` 解析球员数据并生成 INSERT 语句。由于数据量较大（806条），建议使用脚本生成或手动提取。

在 init.sql 文件末尾追加球员数据 INSERT 语句（格式示例）：

```sql
-- Insert player data
INSERT INTO player (name, position, status, number, club, league, country, height, foot) VALUES 
('里杰卡尔德', '后腰', '历史', 8, 'AC米兰', '意甲', '荷兰', 190, '右'),
('贝利', '中锋', '历史', 10, '桑托斯', '巴西甲', '巴西', 173, '右'),
('阿尔贝蒂尼', '后腰', '历史', 4, 'AC米兰', '意甲', '意大利', 180, '右');
-- ... 继续插入所有806条球员数据
```

---

## Phase 7: 前端基础配置

### Task 17: 添加 axios 依赖

**Files:**
- Modify: `frontend-efootball/package.json`

- [ ] **Step 1: 更新 package.json 添加 axios**

```json
{
  "name": "efootball-player-system",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite --port 3000",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "antd": "^5.12.0",
    "axios": "^1.6.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.43",
    "@types/react-dom": "^18.2.17",
    "@vitejs/plugin-react": "^4.2.1",
    "vite": "^5.0.8"
  }
}
```

- [ ] **Step 2: 安装依赖**

Run: `cd frontend-efootball && npm install`

---

### Task 18: 配置 Vite 代理

**Files:**
- Modify: `frontend-efootball/vite.config.js`

- [ ] **Step 1: 更新 vite.config.js 添加代理**

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
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

## Phase 8: 前端 API 模块

### Task 19: 创建 API 封装模块

**Files:**
- Create: `frontend-efootball/src/api/player.js`

- [ ] **Step 1: 创建 src/api 目录和 player.js**

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
});

const MANAGE_PASSWORD = 'wangyangexpo';

// 获取枚举
export const getPlayerEnums = () => api.get('/player/enums');

// 分页查询球员
export const getPlayers = (params) => api.get('/player', { params });

// 查询球员详情
export const getPlayerById = (id) => api.get(`/player/${id}`);

// 新增球员
export const createPlayer = (data) => api.post('/player', {
  ...data,
  password: MANAGE_PASSWORD
});

// 修改球员
export const updatePlayer = (id, data) => api.post(`/player/${id}`, {
  ...data,
  password: MANAGE_PASSWORD
});

// 删除球员
export const deletePlayer = (id) => api.post(`/player/delete/${id}`, {
  password: MANAGE_PASSWORD
});

// 响应数据提取辅助函数
export const extractData = (response) => response.data.data;

export default api;
```

---

## Phase 9: 前端管理页面组件

### Task 20: 创建密码验证弹窗组件

**Files:**
- Create: `frontend-efootball/src/components/PasswordModal.jsx`

- [ ] **Step 1: 创建 PasswordModal.jsx**

```jsx
import React, { useState } from 'react';
import { Modal, Input, Button, message } from 'antd';

const PasswordModal = ({ onSuccess, onCancel }) => {
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = () => {
    setLoading(true);

    // 验证密码
    if (password === 'wangyangexpo') {
      sessionStorage.setItem('playerManageAuth', 'true');
      message.success('验证成功');
      onSuccess();
    } else {
      message.error('密码错误，请重新输入');
    }

    setLoading(false);
  };

  return (
    <Modal
      open={true}
      title="管理页面验证"
      onCancel={onCancel}
      footer={null}
      centered
    >
      <div style={{ padding: '20px 0' }}>
        <Input.Password
          placeholder="请输入管理密码"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          onPressEnter={handleSubmit}
          style={{ marginBottom: 16 }}
        />
        <Button
          type="primary"
          block
          onClick={handleSubmit}
          loading={loading}
        >
          验证
        </Button>
      </div>
    </Modal>
  );
};

export default PasswordModal;
```

---

### Task 21: 创建球员管理页面

**Files:**
- Create: `frontend-efootball/src/pages/PlayerManage.jsx`

- [ ] **Step 1: 创建 PlayerManage.jsx**

```jsx
import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  message,
  Popconfirm,
  Tag
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { getPlayers, getPlayerEnums, createPlayer, updatePlayer, deletePlayer, extractData } from '../api/player';

const { Option } = Select;

const PlayerManage = () => {
  const [players, setPlayers] = useState([]);
  const [enums, setEnums] = useState({});
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 20, total: 0 });
  const [modalOpen, setModalOpen] = useState(false);
  const [editingPlayer, setEditingPlayer] = useState(null);
  const [form] = Form.useForm();

  // 加载枚举
  useEffect(() => {
    loadEnums();
  }, []);

  // 加载球员列表
  useEffect(() => {
    loadPlayers();
  }, [pagination.current, pagination.pageSize]);

  const loadEnums = async () => {
    try {
      const response = await getPlayerEnums();
      setEnums(extractData(response));
    } catch (error) {
      message.error('加载枚举失败');
    }
  };

  const loadPlayers = async () => {
    setLoading(true);
    try {
      const response = await getPlayers({
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      });
      const data = extractData(response);
      setPlayers(data.records || []);
      setPagination({ ...pagination, total: data.total });
    } catch (error) {
      message.error('加载球员列表失败');
    }
    setLoading(false);
  };

  const handleTableChange = (newPagination) => {
    setPagination({
      ...pagination,
      current: newPagination.current,
      pageSize: newPagination.pageSize
    });
  };

  const handleAdd = () => {
    setEditingPlayer(null);
    form.resetFields();
    setModalOpen(true);
  };

  const handleEdit = (record) => {
    setEditingPlayer(record);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleDelete = async (id) => {
    try {
      await deletePlayer(id);
      message.success('删除成功');
      loadPlayers();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      if (editingPlayer) {
        await updatePlayer(editingPlayer.id, values);
        message.success('修改成功');
      } else {
        await createPlayer(values);
        message.success('新增成功');
      }

      setModalOpen(false);
      loadPlayers();
    } catch (error) {
      if (error.response?.data?.message) {
        message.error(error.response.data.message);
      } else if (error.errorFields) {
        // Form validation error, ignore
      } else {
        message.error('操作失败');
      }
    }
  };

  const columns = [
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 120,
      render: (text) => <strong>{text}</strong>
    },
    {
      title: '位置',
      dataIndex: 'position',
      key: 'position',
      width: 80
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Tag color={status === '现役' ? 'green' : 'orange'}>{status}</Tag>
      )
    },
    {
      title: '号码',
      dataIndex: 'number',
      key: 'number',
      width: 60,
      align: 'center'
    },
    {
      title: '俱乐部',
      dataIndex: 'club',
      key: 'club',
      width: 120
    },
    {
      title: '联赛',
      dataIndex: 'league',
      key: 'league',
      width: 80
    },
    {
      title: '国家队',
      dataIndex: 'country',
      key: 'country',
      width: 80
    },
    {
      title: '身高',
      dataIndex: 'height',
      key: 'height',
      width: 60,
      align: 'center'
    },
    {
      title: '惯用脚',
      dataIndex: 'foot',
      key: 'foot',
      width: 60,
      align: 'center'
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            修改
          </Button>
          <Popconfirm
            title="确定删除该球员？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增球员
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={players}
        rowKey="id"
        loading={loading}
        pagination={pagination}
        onChange={handleTableChange}
        scroll={{ x: 900 }}
      />

      <Modal
        open={modalOpen}
        title={editingPlayer ? '修改球员' : '新增球员'}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="球员姓名"
            rules={[{ required: true, message: '请输入球员姓名' }]}
          >
            <Input placeholder="请输入球员姓名" />
          </Form.Item>

          <Form.Item
            name="position"
            label="位置"
            rules={[{ required: true, message: '请选择位置' }]}
          >
            <Select placeholder="请选择位置" showSearch>
              {(enums.positions || []).map(pos => (
                <Option key={pos} value={pos}>{pos}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="status"
            label="现役状态"
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select placeholder="请选择状态">
              {(enums.statuses || []).map(s => (
                <Option key={s} value={s}>{s}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="number" label="球衣号码">
            <InputNumber min={1} max={99} style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="club" label="俱乐部">
            <Select placeholder="请选择俱乐部" showSearch allowClear>
              {(enums.clubs || []).map(c => (
                <Option key={c} value={c}>{c}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="league" label="联赛">
            <Select placeholder="请选择联赛" showSearch allowClear>
              {(enums.leagues || []).map(l => (
                <Option key={l} value={l}>{l}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="country" label="国家队">
            <Select placeholder="请选择国家队" showSearch allowClear>
              {(enums.countries || []).map(c => (
                <Option key={c} value={c}>{c}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="height" label="身高(cm)">
            <InputNumber min={150} max={220} style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="foot" label="惯用脚">
            <Select placeholder="请选择惯用脚" allowClear>
              {(enums.foots || []).map(f => (
                <Option key={f} value={f}>{f}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="cardImage" label="卡面图片URL">
            <Input placeholder="请输入图片URL地址" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default PlayerManage;
```

---

## Phase 10: 前端组件改造

### Task 22: 改造 FilterPanel 组件

**Files:**
- Modify: `frontend-efootball/src/components/FilterPanel.jsx`

- [ ] **Step 1: 更新 FilterPanel.jsx 使用 API 枚举**

```jsx
import React, { useEffect, useState } from "react";
import { Form, Select, InputNumber, Radio, Row, Col, Button, Spin } from "antd";
import { getPlayerEnums, extractData } from "../api/player";

const { Option } = Select;

const FilterPanel = ({ filters, onFilterChange }) => {
  const [form] = Form.useForm();
  const [enums, setEnums] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadEnums();
  }, []);

  const loadEnums = async () => {
    try {
      const response = await getPlayerEnums();
      setEnums(extractData(response));
    } catch (error) {
      console.error('加载枚举失败', error);
    }
    setLoading(false);
  };

  const handleValuesChange = (changedValues, allValues) => {
    onFilterChange(allValues);
  };

  const handleReset = () => {
    form.resetFields();
    onFilterChange({
      position: undefined,
      status: undefined,
      number: undefined,
      league: undefined,
      club: undefined,
      country: undefined,
      foot: undefined,
      height: undefined,
      heightOperator: "=",
    });
  };

  if (loading) {
    return (
      <div style={{ background: "#fff", padding: "24px", textAlign: "center" }}>
        <Spin />
      </div>
    );
  }

  return (
    <div
      style={{
        background: "#fff",
        padding: "24px",
        marginBottom: "24px",
        borderRadius: "12px",
        boxShadow:
          "0 4px 12px rgba(102, 126, 234, 0.08), 0 2px 6px rgba(102, 126, 234, 0.04)",
        border: "1px solid rgba(102, 126, 234, 0.1)",
      }}
    >
      <Form
        form={form}
        layout="vertical"
        onValuesChange={handleValuesChange}
        initialValues={{
          heightOperator: "=",
        }}
      >
        <Row gutter={16}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="球员位置" name="position">
              <Select
                placeholder="请选择位置"
                allowClear
                showSearch
                optionFilterProp="children"
              >
                {(enums.positions || []).map((pos) => (
                  <Option key={pos} value={pos}>
                    {pos}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="是否现役" name="status">
              <Select placeholder="请选择状态" allowClear>
                {(enums.statuses || []).map((s) => (
                  <Option key={s} value={s}>
                    {s}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="球衣号码" name="number">
              <InputNumber
                placeholder="请输入号码"
                min={1}
                max={99}
                precision={0}
                style={{ width: "100%" }}
              />
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="所在联赛" name="league">
              <Select
                placeholder="请选择联赛"
                allowClear
                showSearch
                optionFilterProp="children"
              >
                {(enums.leagues || []).map((league) => (
                  <Option key={league} value={league}>
                    {league}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="俱乐部" name="club">
              <Select
                placeholder="请选择俱乐部"
                allowClear
                showSearch
                optionFilterProp="children"
              >
                {(enums.clubs || []).map((club) => (
                  <Option key={club} value={club}>
                    {club}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="国家队" name="country">
              <Select
                placeholder="请选择国家队"
                allowClear
                showSearch
                optionFilterProp="children"
              >
                {(enums.countries || []).map((country) => (
                  <Option key={country} value={country}>
                    {country}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="精确身高(cm)" name="height">
              <InputNumber
                placeholder="请输入身高"
                min={150}
                max={220}
                style={{ width: "100%" }}
              />
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="身高条件">
              <Form.Item name="heightOperator" noStyle>
                <Radio.Group>
                  <Radio value="=">等于</Radio>
                  <Radio value="+">超过</Radio>
                  <Radio value="-">低于</Radio>
                </Radio.Group>
              </Form.Item>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="惯用脚" name="foot">
              <Select placeholder="请选择惯用脚" allowClear>
                {(enums.foots || []).map((foot) => (
                  <Option key={foot} value={foot}>
                    {foot}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={24} md={24} lg={6}>
            <Form.Item label=" ">
              <Button type="primary" danger onClick={handleReset} block>
                重置筛选
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </div>
  );
};

export default FilterPanel;
```

---

### Task 23: 改造 PlayerTable 组件

**Files:**
- Modify: `frontend-efootball/src/components/PlayerTable.jsx`

- [ ] **Step 1: 更新 PlayerTable.jsx 支持后端分页**

```jsx
import React from 'react';
import { Table, Tag, Image } from 'antd';

const PlayerTable = ({ players, loading, pagination, onPageChange }) => {
  const columns = [
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 150,
      fixed: 'left',
      render: (text) => <strong>{text}</strong>
    },
    {
      title: '位置',
      dataIndex: 'position',
      key: 'position',
      width: 100,
      render: (position) => {
        const colorMap = {
          '门将': '#f59e0b',
          '中后卫': '#667eea',
          '左后卫': '#667eea',
          '右后卫': '#667eea',
          '后腰': '#06b6d4',
          '中前卫': '#10b981',
          '左前卫': '#10b981',
          '右前卫': '#10b981',
          '前腰': '#8b5cf6',
          '左边锋': '#ec4899',
          '右边锋': '#ec4899',
          '影锋': '#f97316',
          '中锋': '#ef4444'
        };
        return <Tag color={colorMap[position] || 'default'}>{position}</Tag>;
      }
    },
    {
      title: '现役状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => (
        <Tag color={status === '现役' ? '#667eea' : '#f59e0b'}>
          {status}
        </Tag>
      )
    },
    {
      title: '球衣号码',
      dataIndex: 'number',
      key: 'number',
      width: 100,
      align: 'center'
    },
    {
      title: '俱乐部',
      dataIndex: 'club',
      key: 'club',
      width: 150
    },
    {
      title: '联赛',
      dataIndex: 'league',
      key: 'league',
      width: 120
    },
    {
      title: '国家队',
      dataIndex: 'country',
      key: 'country',
      width: 120
    },
    {
      title: '身高(cm)',
      dataIndex: 'height',
      key: 'height',
      width: 100,
      align: 'center',
      sorter: true
    },
    {
      title: '惯用脚',
      dataIndex: 'foot',
      key: 'foot',
      width: 100,
      align: 'center',
      render: (foot) => (
        <Tag color={foot === '左' ? '#667eea' : '#10b981'}>{foot}</Tag>
      )
    },
    {
      title: '卡面',
      dataIndex: 'cardImage',
      key: 'cardImage',
      width: 100,
      align: 'center',
      render: (url) => url ? (
        <Image src={url} width={60} height={80} style={{ objectFit: 'cover' }} />
      ) : null
    }
  ];

  const tablePagination = pagination ? {
    current: pagination.current || 1,
    pageSize: pagination.pageSize || 20,
    total: pagination.total || 0,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 名球员`,
    position: ['bottomCenter']
  } : false;

  return (
    <div style={{ 
      background: '#fff', 
      padding: '24px',
      borderRadius: '12px',
      boxShadow: '0 4px 12px rgba(102, 126, 234, 0.08), 0 2px 6px rgba(102, 126, 234, 0.04)',
      border: '1px solid rgba(102, 126, 234, 0.1)'
    }}>
      <Table
        columns={columns}
        dataSource={players}
        rowKey="id"
        loading={loading}
        pagination={tablePagination}
        onChange={(newPagination) => onPageChange && onPageChange(newPagination)}
        scroll={{ x: 1200 }}
      />
    </div>
  );
};

export default PlayerTable;
```

---

## Phase 11: 前端主应用改造

### Task 24: 改造 App.jsx 主应用

**Files:**
- Modify: `frontend-efootball/src/App.jsx`

- [ ] **Step 1: 完全重构 App.jsx 实现路由和 API 数据获取**

```jsx
import React, { useState, useEffect, useCallback } from 'react';
import { Layout, Typography, Space, Button, FloatButton, Popover, message } from 'antd';
import { MessageOutlined, SearchOutlined, SettingOutlined } from '@ant-design/icons';
import FilterPanel from './components/FilterPanel';
import PlayerTable from './components/PlayerTable';
import PasswordModal from './components/PasswordModal';
import PlayerManage from './pages/PlayerManage';
import { getPlayers, extractData } from './api/player';
import qrcodeImage from './assets/douyin.jpeg';
import './App.css';

const { Header, Content } = Layout;
const { Title } = Typography;

function App() {
  const [currentPage, setCurrentPage] = useState('main');
  const [isAuthenticated, setIsAuthenticated] = useState(
    sessionStorage.getItem('playerManageAuth') === 'true'
  );

  // 主页面状态
  const [filters, setFilters] = useState({
    position: undefined,
    status: undefined,
    number: undefined,
    league: undefined,
    club: undefined,
    country: undefined,
    foot: undefined,
    height: undefined,
    heightOperator: '='
  });
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 20, total: 0 });

  // 加载球员数据
  const loadPlayers = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        ...filters,
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      };
      // 移除 undefined 值
      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === null) {
          delete params[key];
        }
      });

      const response = await getPlayers(params);
      const data = extractData(response);
      setPlayers(data.records || []);
      setPagination(prev => ({ ...prev, total: data.total }));
    } catch (error) {
      message.error('加载球员数据失败');
    }
    setLoading(false);
  }, [filters, pagination.current, pagination.pageSize]);

  useEffect(() => {
    if (currentPage === 'main') {
      loadPlayers();
    }
  }, [currentPage, loadPlayers]);

  // 篮选条件变化
  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
    setPagination(prev => ({ ...prev, current: 1 })); // 重置到第一页
  };

  // 分页变化
  const handlePageChange = (newPagination) => {
    setPagination({
      current: newPagination.current,
      pageSize: newPagination.pageSize,
      total: pagination.total
    });
  };

  // 页面切换
  const handlePageSwitch = (page) => {
    setCurrentPage(page);
  };

  // 密码验证成功
  const handleAuthSuccess = () => {
    setIsAuthenticated(true);
  };

  // 返回主页面
  const handleBackToMain = () => {
    setCurrentPage('main');
  };

  const feedbackContent = (
    <div style={{ textAlign: 'center' }}>
      <img 
        src={qrcodeImage} 
        alt="反馈二维码" 
        style={{ width: 200, height: 'auto', marginBottom: 8 }}
      />
      <div style={{ fontSize: 12, color: '#666' }}>
        反馈球员bug或者数据错误，请加抖音
      </div>
    </div>
  );

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ 
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 
        padding: '0 50px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <Space>
          <Title level={3} style={{ color: '#fff', margin: 0 }}>
            ⚽ 实况足球球员筛选系统
          </Title>
        </Space>
        <Space>
          <Button 
            type={currentPage === 'main' ? 'primary' : 'default'}
            icon={<SearchOutlined />}
            onClick={() => handlePageSwitch('main')}
          >
            球员查询
          </Button>
          <Button 
            type={currentPage === 'manage' ? 'primary' : 'default'}
            icon={<SettingOutlined />}
            onClick={() => handlePageSwitch('manage')}
          >
            球员管理
          </Button>
        </Space>
      </Header>

      <Content style={{ padding: '24px 50px' }}>
        {currentPage === 'main' && (
          <>
            <FilterPanel 
              filters={filters}
              onFilterChange={handleFilterChange}
            />
            <PlayerTable 
              players={players}
              loading={loading}
              pagination={pagination}
              onPageChange={handlePageChange}
            />
          </>
        )}

        {currentPage === 'manage' && (
          isAuthenticated ? (
            <PlayerManage />
          ) : (
            <PasswordModal 
              onSuccess={handleAuthSuccess}
              onCancel={handleBackToMain}
            />
          )
        )}
      </Content>
      
      {currentPage === 'main' && (
        <Popover 
          content={feedbackContent} 
          title="问题反馈"
          trigger="hover"
          placement="leftBottom"
        >
          <FloatButton 
            icon={<MessageOutlined />}
            type="primary"
            style={{ right: 24, bottom: 24 }}
            tooltip="反馈问题"
          />
        </Popover>
      )}
    </Layout>
  );
}

export default App;
```

---

### Task 25: 更新 App.css 样式

**Files:**
- Modify: `frontend-efootball/src/App.css`

- [ ] **Step 1: 更新 App.css 添加管理页面相关样式**

```css
#root {
  width: 100%;
}

.ant-layout-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.ant-layout-header .ant-btn {
  margin-left: 8px;
}

/* 管理页面样式 */
.player-manage-page {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.08);
}

/* 表格操作按钮 */
.ant-table-cell .ant-btn-link {
  padding: 0 4px;
}

@media (max-width: 768px) {
  .ant-layout-content {
    padding: 16px 16px !important;
  }
  
  .ant-layout-header {
    padding: 0 16px !important;
  }

  .ant-layout-header .ant-btn {
    margin-left: 4px;
    font-size: 12px;
  }
}
```

---

## Phase 12: 数据迁移脚本

### Task 26: 生成球员数据 SQL 脚本

**Files:**
- Create: `springboot-demo/sql/generate-player-data.sh`

- [ ] **Step 1: 创建数据生成脚本**

```bash
#!/bin/bash

# 从 players.json 提取球员数据并生成 SQL INSERT 语句
# 输出文件: springboot-demo/sql/player_data.sql

JSON_FILE="../frontend-efootball/src/data/players.json"
OUTPUT_FILE="player_data.sql"

echo "-- Player data INSERT statements" > $OUTPUT_FILE
echo "-- Generated from players.json" >> $OUTPUT_FILE
echo "" >> $OUTPUT_FILE

# 使用 jq 解析 JSON 并生成 SQL
# 注意: 需要安装 jq 工具

cat $JSON_FILE | jq -r '.players[] | 
  "INSERT INTO player (name, position, status, number, club, league, country, height, foot) VALUES (\''\(.name | gsub("'" ; "\\'"))\'', '\''\(.position | gsub("'" ; "\\'"))\'', '\''\(.status | gsub("'" ; "\\'"))\'', \(.number // "NULL"), '\''\(.club // "" | gsub("'" ; "\\'"))\'', '\''\(.league // "" | gsub("'" ; "\\'"))\'', '\''\(.country // "" | gsub("'" ; "\\'"))\'', \(.height // "NULL"), '\''\(.foot // "" | gsub("'" ; "\\'"))\'');"'
' >> $OUTPUT_FILE

echo "Generated player_data.sql with $(wc -l < $OUTPUT_FILE) lines"
```

- [ ] **Step 2: 执行脚本生成数据**

Run: `cd springboot-demo/sql && ./generate-player-data.sh`

- [ ] **Step 3: 将生成的数据合并到 init.sql**

将 `player_data.sql` 的内容追加到 `init.sql` 文件末尾。

---

## Phase 13: 集成测试与验证

### Task 27: 后端编译与启动测试

- [ ] **Step 1: 编译后端项目**

Run: `cd springboot-demo && mvn clean compile`

Expected: BUILD SUCCESS

- [ ] **Step 2: 启动后端服务**

Run: `cd springboot-demo/springboot-demo-start && mvn spring-boot:run`

Expected: Spring Boot 启动成功，端口 8080

- [ ] **Step 3: 测试 API 接口**

Run: `curl http://localhost:8080/api/player/enums`

Expected: 返回枚举数据 JSON

---

### Task 28: 前端启动测试

- [ ] **Step 1: 启动前端开发服务器**

Run: `cd frontend-efootball && npm run dev`

Expected: Vite 启动成功，端口 3000

- [ ] **Step 2: 测试前端页面**

在浏览器访问 http://localhost:3000

Expected: 
- 主页面显示球员筛选功能
- 数据从后端 API 加载
- 管理页面需要密码验证

---

### Task 29: 提交代码

- [ ] **Step 1: 提交所有更改**

```bash
git add .
git commit -m "feat: 实现球员管理系统前后端功能

- 后端新增 Player 模块 (COLA架构)
- 实现球员 CRUD API (带密码验证)
- 前端改造为 API 数据源
- 新增管理页面带密码保护
- 数据库新增 player 表和初始数据

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>"
```

---

## 自我审查清单

**Spec 覆盖检查:**
- ✅ Player 模块 COLA 分层架构 - Tasks 1-15
- ✅ 数据库 player 表设计 - Task 16
- ✅ API 接口规范 (GET查询, POST增删改) - Tasks 15, 19
- ✅ 密码验证机制 - Tasks 5, 6, 14, 20
- ✅ 前端枚举从 API 获取 - Task 22
- ✅ 前端分页筛选 - Tasks 23, 24
- ✅ 管理页面新增/修改/删除 - Tasks 21, 24
- ✅ Vite 代理配置 - Task 18

**占位符扫描:**
- ✅ 无 TBD 或 TODO
- ✅ 所有代码完整
- ✅ 无 "类似 Task N" 描述

**类型一致性:**
- ✅ PlayerDTO 字段与 PlayerEntity 一致
- ✅ API 参数名与前端一致 (heightOperator, pageNum, pageSize)
- ✅ Response 结构统一使用 `code`, `message`, `data`