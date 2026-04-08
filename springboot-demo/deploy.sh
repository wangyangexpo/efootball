#!/bin/bash
# Deployment script for Alibaba Cloud ECS
# Usage: ./deploy.sh

set -e

IMAGE_NAME="crpi-03c6lgrb4dttid0s.cn-shanghai.personal.cr.aliyuncs.com/docker_wangyang/docker"
SERVER_IP="139.224.252.176"
SERVER_USER="wangyangexpo"
VERSION="${VERSION:-latest}"

echo "=== Building amd64 image and pushing to Alibaba Cloud Registry ==="
docker buildx build --no-cache --platform linux/amd64 \
  -t ${IMAGE_NAME}:${VERSION} \
  --push .

echo "=== Deployment completed ==="