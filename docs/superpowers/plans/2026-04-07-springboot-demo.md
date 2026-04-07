# Spring Boot Demo Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a multi-module Spring Boot project with COLA architecture, implementing a User CRUD API with MySQL integration and Docker deployment.

**Architecture:** 6-module COLA layered architecture (api/common/domain/repository/service/start), removing all Alibaba internal dependencies, using standard Spring Boot + MyBatis Plus.

**Tech Stack:** Java 11, Spring Boot 2.7.18, MyBatis Plus 3.5.3.1, MySQL 8.0, Lombok, MapStruct

---

## File Structure

```
springboot-demo/
├── pom.xml                                    # Parent POM
├── springboot-demo-api/
│   ├── pom.xml
│   └── src/main/java/com/example/demo/api/
│       ├── Response.java                      # Generic response wrapper
│       ├── UserDTO.java                       # User data transfer object
│       └── request/
│           ├── UserCreateRequest.java         # Create request
│           ├── UserUpdateRequest.java         # Update request
│           └── UserQueryRequest.java          # Query request with pagination
├── springboot-demo-common/
│   ├── pom.xml
│   └── src/main/java/com/example/demo/common/
│       ├── Constants.java                     # Common constants
│       ├── ResultCode.java                    # Response code enum
│       └── PageResult.java                    # Pagination wrapper
├── springboot-demo-domain/
│   ├── pom.xml
│   └── src/main/java/com/example/demo/domain/
│       ├── entity/
│       │   └── UserEntity.java                # User domain entity
│       └── service/
│           └── UserService.java               # User service interface
├── springboot-demo-repository/
│   ├── pom.xml
│   └── src/main/java/com/example/demo/repository/
│       ├── mapper/
│       │   └── UserMapper.java                # MyBatis mapper interface
│       └── UserRepository.java                # Repository abstraction
│   └── src/main/resources/mappers/
│       └── UserMapper.xml                     # SQL mapping file
├── springboot-demo-service/
│   ├── pom.xml
│   └── src/main/java/com/example/demo/service/
│       ├── impl/
│       │   └── UserServiceImpl.java           # Service implementation
│       └── executor/
│           ├── UserCreateExe.java             # Create executor
│           ├── UserUpdateExe.java             # Update executor
│           └── UserQueryExe.java              # Query executor
├── springboot-demo-start/
│   ├── pom.xml
│   └── src/main/java/com/example/demo/
│       ├── Application.java                   # Spring Boot main class
│       └── controller/
│           └── UserController.java            # REST controller
│   └── src/main/resources/
│       ├── application.yml                    # Application config
│       └── application-dev.yml                # Dev environment config
├── sql/
│   └── init.sql                               # Database init script
├── Dockerfile                                 # Docker build file
├── docker-compose.yml                         # Local Docker orchestration
├── deploy.sh                                  # Alibaba Cloud deployment script
└── README.md                                  # Project documentation
```

---

### Task 1: Create Parent POM

**Files:**
- Create: `springboot-demo/pom.xml`

- [ ] **Step 1: Create project root directory and parent pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example.demo</groupId>
    <artifactId>springboot-demo</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>springboot-demo</name>
    <description>Spring Boot Demo Project with COLA Architecture</description>

    <properties>
        <java.version>11</java.version>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-boot.version>2.7.18</spring-boot.version>
        <mybatis-plus.version>3.5.3.1</mybatis-plus.version>
        <lombok.version>1.18.22</lombok.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
    </properties>

    <modules>
        <module>springboot-demo-api</module>
        <module>springboot-demo-common</module>
        <module>springboot-demo-domain</module>
        <module>springboot-demo-repository</module>
        <module>springboot-demo-service</module>
        <module>springboot-demo-start</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot Dependencies -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- MyBatis Plus -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>

            <!-- Lombok -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </dependency>

            <!-- MapStruct -->
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>

            <!-- Internal Modules -->
            <dependency>
                <groupId>com.example.demo</groupId>
                <artifactId>springboot-demo-api</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.example.demo</groupId>
                <artifactId>springboot-demo-common</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.example.demo</groupId>
                <artifactId>springboot-demo-domain</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.example.demo</groupId>
                <artifactId>springboot-demo-repository</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.example.demo</groupId>
                <artifactId>springboot-demo-service</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.8.1</version>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                            <path>
                                <groupId>org.mapstruct</groupId>
                                <artifactId>mapstruct-processor</artifactId>
                                <version>${mapstruct.version}</version>
                            </path>
                        </annotationProcessorPaths>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

- [ ] **Step 2: Verify directory structure**

Run: `mkdir -p springboot-demo && ls springboot-demo`
Expected: Empty directory ready for modules

---

### Task 2: Create API Module

**Files:**
- Create: `springboot-demo/springboot-demo-api/pom.xml`
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/Response.java`
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/UserDTO.java`
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request/UserCreateRequest.java`
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request/UserUpdateRequest.java`
- Create: `springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request/UserQueryRequest.java`

- [ ] **Step 1: Create api module pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example.demo</groupId>
        <artifactId>springboot-demo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>springboot-demo-api</artifactId>
    <packaging>jar</packaging>
    <name>springboot-demo-api</name>
    <description>API definitions - DTOs and Request/Response objects</description>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create Response.java**

```java
package com.example.demo.api;

import lombok.Data;
import java.io.Serializable;

/**
 * Generic API Response wrapper
 */
@Data
public class Response<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String message;
    private T data;

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode("200");
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> Response<T> success() {
        return success(null);
    }

    public static <T> Response<T> fail(String code, String message) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static <T> Response<T> fail(String message) {
        return fail("500", message);
    }
}
```

- [ ] **Step 3: Create UserDTO.java**

```java
package com.example.demo.api;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Data Transfer Object
 */
@Data
public class UserDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String email;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

- [ ] **Step 4: Create UserCreateRequest.java**

```java
package com.example.demo.api.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * User creation request
 */
@Data
public class UserCreateRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 2, max = 50, message = "Username length must be between 2 and 50")
    private String username;
    
    @Email(message = "Email format is invalid")
    private String email;
}
```

- [ ] **Step 5: Create UserUpdateRequest.java**

```java
package com.example.demo.api.request;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * User update request
 */
@Data
public class UserUpdateRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Size(min = 2, max = 50, message = "Username length must be between 2 and 50")
    private String username;
    
    @Email(message = "Email format is invalid")
    private String email;
    
    private Integer status;
}
```

- [ ] **Step 6: Create UserQueryRequest.java**

```java
package com.example.demo.api.request;

import lombok.Data;
import java.io.Serializable;

/**
 * User query request with pagination
 */
@Data
public class UserQueryRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String username;
    private Integer status;
    
    // Pagination
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
```

---

### Task 3: Create Common Module

**Files:**
- Create: `springboot-demo/springboot-demo-common/pom.xml`
- Create: `springboot-demo/springboot-demo-common/src/main/java/com/example/demo/common/Constants.java`
- Create: `springboot-demo/springboot-demo-common/src/main/java/com/example/demo/common/ResultCode.java`
- Create: `springboot-demo/springboot-demo-common/src/main/java/com/example/demo/common/PageResult.java`

- [ ] **Step 1: Create common module pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example.demo</groupId>
        <artifactId>springboot-demo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>springboot-demo-common</artifactId>
    <packaging>jar</packaging>
    <name>springboot-demo-common</name>
    <description>Common utilities and constants</description>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create Constants.java**

```java
package com.example.demo.common;

/**
 * Common constants
 */
public class Constants {
    
    public static final String SUCCESS_CODE = "200";
    public static final String FAIL_CODE = "500";
    
    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_INACTIVE = 0;
    
    public static final String DEFAULT_PAGE_NUM = "1";
    public static final String DEFAULT_PAGE_SIZE = "10";
}
```

- [ ] **Step 3: Create ResultCode.java**

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
    USER_ALREADY_EXISTS("1002", "User Already Exists");
    
    private final String code;
    private final String message;
    
    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

- [ ] **Step 4: Create PageResult.java**

```java
package com.example.demo.common;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Pagination result wrapper
 */
@Data
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<T> list;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private Integer pages;

    public static <T> PageResult<T> of(List<T> list, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages((int) Math.ceil((double) total / pageSize));
        return result;
    }
}
```

---

### Task 4: Create Domain Module

**Files:**
- Create: `springboot-demo/springboot-demo-domain/pom.xml`
- Create: `springboot-demo/springboot-demo-domain/src/main/java/com/example/demo/domain/entity/UserEntity.java`
- Create: `springboot-demo/springboot-demo-domain/src/main/java/com/example/demo/domain/service/UserService.java`

- [ ] **Step 1: Create domain module pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example.demo</groupId>
        <artifactId>springboot-demo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>springboot-demo-domain</artifactId>
    <packaging>jar</packaging>
    <name>springboot-demo-domain</name>
    <description>Domain models and service interfaces</description>

    <dependencies>
        <dependency>
            <groupId>com.example.demo</groupId>
            <artifactId>springboot-demo-api</artifactId>
        </dependency>
        <dependency>
            <groupId>com.example.demo</groupId>
            <artifactId>springboot-demo-common</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create UserEntity.java**

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
 * User domain entity
 */
@Data
@TableName("user")
public class UserEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String email;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

- [ ] **Step 3: Create UserService.java**

```java
package com.example.demo.domain.service;

import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserCreateRequest;
import com.example.demo.api.request.UserQueryRequest;
import com.example.demo.api.request.UserUpdateRequest;
import com.example.demo.api.Response;
import com.example.demo.common.PageResult;

/**
 * User service interface
 */
public interface UserService {
    
    /**
     * Create user
     */
    Response<UserDTO> create(UserCreateRequest request);
    
    /**
     * Get user by ID
     */
    Response<UserDTO> getById(Long id);
    
    /**
     * Update user
     */
    Response<UserDTO> update(Long id, UserUpdateRequest request);
    
    /**
     * Delete user
     */
    Response<Void> delete(Long id);
    
    /**
     * List users with pagination
     */
    Response<PageResult<UserDTO>> list(UserQueryRequest request);
}
```

---

### Task 5: Create Repository Module

**Files:**
- Create: `springboot-demo/springboot-demo-repository/pom.xml`
- Create: `springboot-demo/springboot-demo-repository/src/main/java/com/example/demo/repository/mapper/UserMapper.java`
- Create: `springboot-demo/springboot-demo-repository/src/main/java/com/example/demo/repository/UserRepository.java`
- Create: `springboot-demo/springboot-demo-repository/src/main/resources/mappers/UserMapper.xml`

- [ ] **Step 1: Create repository module pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example.demo</groupId>
        <artifactId>springboot-demo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>springboot-demo-repository</artifactId>
    <packaging>jar</packaging>
    <name>springboot-demo-repository</name>
    <description>Data access layer - Mappers and Repository</description>

    <dependencies>
        <dependency>
            <groupId>com.example.demo</groupId>
            <artifactId>springboot-demo-domain</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create UserMapper.java**

```java
package com.example.demo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.domain.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * User MyBatis mapper interface
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    
}
```

- [ ] **Step 3: Create UserRepository.java**

```java
package com.example.demo.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User repository abstraction
 */
@Repository
public class UserRepository {
    
    @Autowired
    private UserMapper userMapper;
    
    public UserEntity selectById(Long id) {
        return userMapper.selectById(id);
    }
    
    public int insert(UserEntity entity) {
        return userMapper.insert(entity);
    }
    
    public int updateById(UserEntity entity) {
        return userMapper.updateById(entity);
    }
    
    public int deleteById(Long id) {
        return userMapper.deleteById(id);
    }
    
    public List<UserEntity> selectList(LambdaQueryWrapper<UserEntity> wrapper) {
        return userMapper.selectList(wrapper);
    }
    
    public Page<UserEntity> selectPage(Page<UserEntity> page, LambdaQueryWrapper<UserEntity> wrapper) {
        return userMapper.selectPage(page, wrapper);
    }
    
    public UserEntity selectByUsername(String username) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
}
```

- [ ] **Step 4: Create UserMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.demo.repository.mapper.UserMapper">

    <resultMap id="BaseResultMap" type="com.example.demo.domain.entity.UserEntity">
        <id column="id" property="id"/>
        <result column="username" property="username"/>
        <result column="email" property="email"/>
        <result column="status" property="status"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
        <result column="deleted" property="deleted"/>
    </resultMap>

</mapper>
```

---

### Task 6: Create Service Module

**Files:**
- Create: `springboot-demo/springboot-demo-service/pom.xml`
- Create: `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/impl/UserServiceImpl.java`
- Create: `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor/UserCreateExe.java`
- Create: `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor/UserUpdateExe.java`
- Create: `springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor/UserQueryExe.java`

- [ ] **Step 1: Create service module pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example.demo</groupId>
        <artifactId>springboot-demo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>springboot-demo-service</artifactId>
    <packaging>jar</packaging>
    <name>springboot-demo-service</name>
    <description>Business service layer</description>

    <dependencies>
        <dependency>
            <groupId>com.example.demo</groupId>
            <artifactId>springboot-demo-domain</artifactId>
        </dependency>
        <dependency>
            <groupId>com.example.demo</groupId>
            <artifactId>springboot-demo-repository</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create UserCreateExe.java**

```java
package com.example.demo.service.executor;

import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserCreateRequest;
import com.example.demo.common.Constants;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * User create command executor
 */
@Component
public class UserCreateExe {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserDTO execute(UserCreateRequest request) {
        // Check if username already exists
        UserEntity existing = userRepository.selectByUsername(request.getUsername());
        if (existing != null) {
            throw new RuntimeException("User already exists with username: " + request.getUsername());
        }
        
        // Create entity
        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setStatus(Constants.STATUS_ACTIVE);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(0);
        
        // Save
        userRepository.insert(entity);
        
        // Convert to DTO
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        
        return dto;
    }
}
```

- [ ] **Step 3: Create UserUpdateExe.java**

```java
package com.example.demo.service.executor;

import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserUpdateRequest;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * User update command executor
 */
@Component
public class UserUpdateExe {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserDTO execute(Long id, UserUpdateRequest request) {
        UserEntity entity = userRepository.selectById(id);
        if (entity == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        
        // Update fields if provided
        if (request.getUsername() != null) {
            entity.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        entity.setUpdateTime(LocalDateTime.now());
        
        // Save
        userRepository.updateById(entity);
        
        // Convert to DTO
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        
        return dto;
    }
}
```

- [ ] **Step 4: Create UserQueryExe.java**

```java
package com.example.demo.service.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserQueryRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User query executor
 */
@Component
public class UserQueryExe {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserDTO getById(Long id) {
        UserEntity entity = userRepository.selectById(id);
        if (entity == null) {
            return null;
        }
        return convertToDTO(entity);
    }
    
    public PageResult<UserDTO> list(UserQueryRequest request) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        
        // Filter conditions
        if (request.getUsername() != null) {
            wrapper.like(UserEntity::getUsername, request.getUsername());
        }
        if (request.getStatus() != null) {
            wrapper.eq(UserEntity::getStatus, request.getStatus());
        }
        
        // Pagination
        Page<UserEntity> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<UserEntity> result = userRepository.selectPage(page, wrapper);
        
        // Convert to DTOs
        List<UserDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(dtoList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }
    
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    
    private UserDTO convertToDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus());
        dto.setCreateTime(entity.getCreateTime());
        dto.setUpdateTime(entity.getUpdateTime());
        return dto;
    }
}
```

- [ ] **Step 5: Create UserServiceImpl.java**

```java
package com.example.demo.service.impl;

import com.example.demo.api.Response;
import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserCreateRequest;
import com.example.demo.api.request.UserQueryRequest;
import com.example.demo.api.request.UserUpdateRequest;
import com.example.demo.common.PageResult;
import com.example.demo.common.ResultCode;
import com.example.demo.domain.service.UserService;
import com.example.demo.service.executor.UserCreateExe;
import com.example.demo.service.executor.UserQueryExe;
import com.example.demo.service.executor.UserUpdateExe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service implementation
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserCreateExe userCreateExe;
    
    @Autowired
    private UserUpdateExe userUpdateExe;
    
    @Autowired
    private UserQueryExe userQueryExe;
    
    @Override
    public Response<UserDTO> create(UserCreateRequest request) {
        try {
            UserDTO dto = userCreateExe.execute(request);
            return Response.success(dto);
        } catch (RuntimeException e) {
            return Response.fail(ResultCode.USER_ALREADY_EXISTS.getCode(), e.getMessage());
        }
    }
    
    @Override
    public Response<UserDTO> getById(Long id) {
        UserDTO dto = userQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage());
        }
        return Response.success(dto);
    }
    
    @Override
    public Response<UserDTO> update(Long id, UserUpdateRequest request) {
        try {
            UserDTO dto = userUpdateExe.execute(id, request);
            return Response.success(dto);
        } catch (RuntimeException e) {
            return Response.fail(ResultCode.USER_NOT_FOUND.getCode(), e.getMessage());
        }
    }
    
    @Override
    public Response<Void> delete(Long id) {
        // Check if exists first
        UserDTO dto = userQueryExe.getById(id);
        if (dto == null) {
            return Response.fail(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMessage());
        }
        // Delete (logical delete via MyBatis Plus)
        userQueryExe.deleteById(id);
        return Response.success();
    }
    
    @Override
    public Response<PageResult<UserDTO>> list(UserQueryRequest request) {
        PageResult<UserDTO> result = userQueryExe.list(request);
        return Response.success(result);
    }
}
```

---

### Task 7: Create Start Module

**Files:**
- Create: `springboot-demo/springboot-demo-start/pom.xml`
- Create: `springboot-demo/springboot-demo-start/src/main/java/com/example/demo/Application.java`
- Create: `springboot-demo/springboot-demo-start/src/main/java/com/example/demo/controller/UserController.java`
- Create: `springboot-demo/springboot-demo-start/src/main/resources/application.yml`
- Create: `springboot-demo/springboot-demo-start/src/main/resources/application-dev.yml`

- [ ] **Step 1: Create start module pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example.demo</groupId>
        <artifactId>springboot-demo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>springboot-demo-start</artifactId>
    <packaging>jar</packaging>
    <name>springboot-demo-start</name>
    <description>Application startup module</description>

    <dependencies>
        <dependency>
            <groupId>com.example.demo</groupId>
            <artifactId>springboot-demo-service</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Create Application.java**

```java
package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application entry point
 */
@SpringBootApplication(scanBasePackages = "com.example.demo")
@MapperScan("com.example.demo.repository.mapper")
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

- [ ] **Step 3: Create UserController.java**

```java
package com.example.demo.controller;

import com.example.demo.api.Response;
import com.example.demo.api.UserDTO;
import com.example.demo.api.request.UserCreateRequest;
import com.example.demo.api.request.UserQueryRequest;
import com.example.demo.api.request.UserUpdateRequest;
import com.example.demo.common.PageResult;
import com.example.demo.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * User REST Controller
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public Response<UserDTO> create(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }
    
    @GetMapping("/{id}")
    public Response<UserDTO> getById(@PathVariable Long id) {
        return userService.getById(id);
    }
    
    @PutMapping("/{id}")
    public Response<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }
    
    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        return userService.delete(id);
    }
    
    @GetMapping
    public Response<PageResult<UserDTO>> list(@ModelAttribute UserQueryRequest request) {
        return userService.list(request);
    }
}
```

- [ ] **Step 4: Create application.yml**

```yaml
server:
  port: 8080

spring:
  application:
    name: springboot-demo
  profiles:
    active: dev

mybatis-plus:
  mapper-locations: classpath*:mappers/**/*.xml
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

logging:
  level:
    com.example.demo.repository.mapper: debug
```

- [ ] **Step 5: Create application-dev.yml**

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:3306/${DB_NAME:springboot_demo}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USER:root}
    password: ${DB_PASS:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

### Task 8: Create Database Init Script

**Files:**
- Create: `springboot-demo/sql/init.sql`

- [ ] **Step 1: Create init.sql**

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS springboot_demo DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE springboot_demo;

-- Create user table
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key',
    username VARCHAR(50) NOT NULL COMMENT 'Username',
    email VARCHAR(100) COMMENT 'Email address',
    status INT DEFAULT 1 COMMENT 'Status: 1-active, 0-inactive',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    deleted INT DEFAULT 0 COMMENT 'Logical delete flag: 0-not deleted, 1-deleted'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User table';

-- Insert demo data
INSERT INTO user (username, email, status) VALUES ('demo_user', 'demo@example.com', 1);
INSERT INTO user (username, email, status) VALUES ('test_user', 'test@example.com', 1);
```

---

### Task 9: Create Docker Configuration

**Files:**
- Create: `springboot-demo/Dockerfile`
- Create: `springboot-demo/docker-compose.yml`

- [ ] **Step 1: Create Dockerfile**

```dockerfile
FROM openjdk:11-jre-slim
LABEL maintainer="demo@example.com"

WORKDIR /app

# Copy the built jar
COPY springboot-demo-start/target/springboot-demo-start-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

- [ ] **Step 2: Create docker-compose.yml**

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: demo-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: springboot_demo
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    command: --default-authentication-plugin=mysql_native_password

  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: demo-app
    ports:
      - "8080:8080"
    environment:
      DB_HOST: mysql
      DB_NAME: springboot_demo
      DB_USER: root
      DB_PASS: root
      SPRING_PROFILES_ACTIVE: dev
    depends_on:
      - mysql
    restart: on-failure

volumes:
  mysql_data:
```

---

### Task 10: Create Deployment Script

**Files:**
- Create: `springboot-demo/deploy.sh`

- [ ] **Step 1: Create deploy.sh**

```bash
#!/bin/bash
# Deployment script for Alibaba Cloud ECS

set -e

APP_NAME="springboot-demo"
IMAGE_NAME="registry.cn-hangzhou.aliyuncs.com/your-namespace/${APP_NAME}"
SERVER_IP="${SERVER_IP:-your-server-ip}"
VERSION="${VERSION:-latest}"

echo "=== Building project ==="
mvn clean package -DskipTests

echo "=== Building Docker image ==="
docker build -t ${IMAGE_NAME}:${VERSION} .

echo "=== Pushing to Alibaba Cloud Container Registry ==="
docker push ${IMAGE_NAME}:${VERSION}

echo "=== Deploying to server ${SERVER_IP} ==="
ssh root@${SERVER_IP} << EOF
  docker pull ${IMAGE_NAME}:${VERSION}
  docker stop ${APP_NAME} || true
  docker rm ${APP_NAME} || true
  docker run -d \
    --name ${APP_NAME} \
    -p 8080:8080 \
    -e DB_HOST=your-db-host \
    -e DB_NAME=springboot_demo \
    -e DB_USER=your-db-user \
    -e DB_PASS=your-db-password \
    ${IMAGE_NAME}:${VERSION}
EOF

echo "=== Deployment completed ==="
echo "Application is running at http://${SERVER_IP}:8080"
```

---

### Task 11: Create README

**Files:**
- Create: `springboot-demo/README.md`

- [ ] **Step 1: Create README.md**

```markdown
# Spring Boot Demo Project

A minimal Spring Boot demo project demonstrating COLA layered architecture and deployment to Alibaba Cloud.

## Architecture

This project follows COLA (Clean Object-oriented Layered Architecture) with 6 modules:

- **springboot-demo-api**: API definitions (DTOs, Request/Response)
- **springboot-demo-common**: Common utilities and constants
- **springboot-demo-domain**: Domain models and service interfaces
- **springboot-demo-repository**: Data access layer (MyBatis)
- **springboot-demo-service**: Business service implementations
- **springboot-demo-start**: Application entry point

## Tech Stack

- Java 11
- Spring Boot 2.7.18
- MyBatis Plus 3.5.3.1
- MySQL 8.0
- Lombok
- MapStruct

## Quick Start

### Prerequisites

- JDK 11+
- Maven 3.6+
- Docker & Docker Compose

### Local Development

1. Start MySQL with Docker:
   ```bash
   docker-compose up mysql
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   cd springboot-demo-start
   mvn spring-boot:run
   ```

4. Test API:
   ```bash
   curl http://localhost:8080/api/users
   ```

### Docker Deployment

1. Build and run all services:
   ```bash
   docker-compose up --build
   ```

2. Test API:
   ```bash
   curl http://localhost:8080/api/users
   ```

### Alibaba Cloud Deployment

1. Set environment variables:
   ```bash
   export SERVER_IP=your-server-ip
   ```

2. Run deployment script:
   ```bash
   chmod +x deploy.sh
   ./deploy.sh
   ```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/users | Create user |
| GET | /api/users/{id} | Get user by ID |
| PUT | /api/users/{id} | Update user |
| DELETE | /api/users/{id} | Delete user |
| GET | /api/users | List users (paginated) |

## License

MIT
```

---

### Task 12: Build and Test

**Files:**
- None (testing existing build)

- [ ] **Step 1: Create all directories**

```bash
mkdir -p springboot-demo/springboot-demo-api/src/main/java/com/example/demo/api/request
mkdir -p springboot-demo/springboot-demo-common/src/main/java/com/example/demo/common
mkdir -p springboot-demo/springboot-demo-domain/src/main/java/com/example/demo/domain/entity
mkdir -p springboot-demo/springboot-demo-domain/src/main/java/com/example/demo/domain/service
mkdir -p springboot-demo/springboot-demo-repository/src/main/java/com/example/demo/repository/mapper
mkdir -p springboot-demo/springboot-demo-repository/src/main/resources/mappers
mkdir -p springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/impl
mkdir -p springboot-demo/springboot-demo-service/src/main/java/com/example/demo/service/executor
mkdir -p springboot-demo/springboot-demo-start/src/main/java/com/example/demo/controller
mkdir -p springboot-demo/springboot-demo-start/src/main/resources
mkdir -p springboot-demo/sql
```

- [ ] **Step 2: Build the project**

Run: `cd springboot-demo && mvn clean install -DskipTests`
Expected: Build success

- [ ] **Step 3: Start MySQL container**

Run: `docker-compose up mysql -d`
Expected: MySQL container running

- [ ] **Step 4: Wait for MySQL to be ready**

Run: `sleep 30 && docker exec demo-mysql mysql -uroot -proot -e "SELECT 1"`
Expected: Query OK

- [ ] **Step 5: Run the application**

Run: `cd springboot-demo-start && mvn spring-boot:run`
Expected: Application starts on port 8080

- [ ] **Step 6: Test GET all users API**

Run: `curl http://localhost:8080/api/users`
Expected: JSON response with user list

- [ ] **Step 7: Test POST create user API**

Run: `curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"username":"newuser","email":"new@example.com"}'`
Expected: JSON response with created user

---

## Spec Coverage Review

| Spec Requirement | Covered Task |
|------------------|--------------|
| Parent POM with module definitions | Task 1 |
| API layer (Response, DTOs, Requests) | Task 2 |
| Common layer (Constants, ResultCode, PageResult) | Task 3 |
| Domain layer (Entity, Service interface) | Task 4 |
| Repository layer (Mapper, Repository) | Task 5 |
| Service layer (ServiceImpl, Executors) | Task 6 |
| Start layer (Application, Controller, Config) | Task 7 |
| Database init script | Task 8 |
| Dockerfile | Task 9 |
| docker-compose.yml | Task 9 |
| deploy.sh | Task 10 |
| README.md | Task 11 |
| Build & Test verification | Task 12 |

All spec requirements covered.