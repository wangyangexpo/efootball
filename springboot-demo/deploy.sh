#!/bin/bash
# Deployment script for Alibaba Cloud ECS
# Usage: ./deploy.sh

set -e

IMAGE_NAME="${IMAGE_NAME:?请设置 IMAGE_NAME 环境变量，如：export IMAGE_NAME=your-registry/your-repo}"
SERVER_IP="${SERVER_IP:?请设置 SERVER_IP 环境变量，如：export SERVER_IP=your.server.ip}"
SERVER_USER="${SERVER_USER:?请设置 SERVER_USER 环境变量，如：export SERVER_USER=your-username}"
VERSION="${VERSION:-latest}"

echo "=== Building amd64 image and pushing to Alibaba Cloud Registry ==="
docker buildx build --no-cache --platform linux/amd64 \
  -t ${IMAGE_NAME}:${VERSION} \
  --push .

echo "=== Deployment completed ==="