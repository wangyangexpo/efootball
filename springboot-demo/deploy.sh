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