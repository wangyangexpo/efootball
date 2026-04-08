#!/bin/bash
# Deployment script for Alibaba Cloud ECS
# Usage: ./deploy.sh

set -e

IMAGE_NAME="crpi-03c6lgrb4dttid0s.cn-shanghai.personal.cr.aliyuncs.com/docker_wangyang/docker"
SERVER_IP="139.224.252.176"
SERVER_USER="wangyangexpo"
VERSION="${VERSION:-latest}"

echo "=== Building amd64 image and pushing to Alibaba Cloud Registry ==="
docker buildx build --platform linux/amd64 \
  -t ${IMAGE_NAME}:${VERSION} \
  --push .

echo "=== Deploying to server ${SERVER_IP} ==="
ssh ${SERVER_USER}@${SERVER_IP} << EOF
  sudo docker compose -f /home/${SERVER_USER}/springboot-demo/docker-compose.yml pull app
  sudo docker compose -f /home/${SERVER_USER}/springboot-demo/docker-compose.yml up -d
EOF

echo "=== Deployment completed ==="
echo "Application is running at http://${SERVER_IP}:8080"