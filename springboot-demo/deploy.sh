#!/bin/bash
# Deployment script for Alibaba Cloud ECS
# Usage: ./deploy.sh

set -e

# 自动加载同目录下的 .env 文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [ -f "${SCRIPT_DIR}/.env" ]; then
  source "${SCRIPT_DIR}/.env"
fi

IMAGE_NAME="${IMAGE_NAME:?请设置 IMAGE_NAME 环境变量，或在 .env 文件中配置}"
SERVER_IP="${SERVER_IP:?请设置 SERVER_IP 环境变量，或在 .env 文件中配置}"
SERVER_USER="${SERVER_USER:?请设置 SERVER_USER 环境变量，或在 .env 文件中配置}"
VERSION="${VERSION:-latest}"

echo "=== Building amd64 image and pushing to Alibaba Cloud Registry ==="
docker buildx build --no-cache --platform linux/amd64 \
  -t ${IMAGE_NAME}:${VERSION} \
  --push .

echo "=== Deployment completed ==="