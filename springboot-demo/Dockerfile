# 第一阶段：编译（容器内完成，本地无需安装 JDK 和 Maven）
FROM maven:3.8.6-eclipse-temurin-11 AS builder

WORKDIR /build

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