# 第一阶段：编译（容器内完成，本地无需安装 JDK 和 Maven）
FROM maven:3.8.6-eclipse-temurin-11 AS builder

WORKDIR /build

# 配置阿里云 Maven 镜像源，解决国内网络无法访问 Maven 中央仓库的问题
RUN mkdir -p /root/.m2 && cat > /root/.m2/settings.xml << 'EOF'
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <name>Aliyun Maven Mirror</name>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
EOF

# 复制所有源码并编译打包
COPY . .
RUN mvn clean package -DskipTests -B

# 第二阶段：运行（只保留 JRE，镜像更小）
FROM eclipse-temurin:11-jre
LABEL maintainer="demo@example.com"

WORKDIR /app

COPY --from=builder /build/springboot-demo-start/target/springboot-demo-start-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]