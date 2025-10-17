#!/bin/bash

# 스크립트 시작 로그
echo "===== Starting Application ====="
date

# 변수 설정
CONTAINER_NAME="hanafuture-backend"
IMAGE_NAME="629143593521.dkr.ecr.ap-northeast-2.amazonaws.com/hanafuture-backend:latest"
APP_PORT=8080

# ECR 로그인
echo "Logging in to ECR..."
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 629143593521.dkr.ecr.ap-northeast-2.amazonaws.com

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to login to ECR"
    exit 1
fi

echo "ECR login successful."

# Docker 이미지 Pull
echo "Pulling Docker image: $IMAGE_NAME"
docker pull $IMAGE_NAME

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to pull Docker image"
    exit 1
fi

echo "Docker image pulled successfully."

# 컨테이너 실행
echo "Starting container: $CONTAINER_NAME"
docker run -d \
    --name $CONTAINER_NAME \
    --restart on-failure:3 \
    -p $APP_PORT:8080 \
    -e SPRING_PROFILES_ACTIVE=oracle \
    -e TZ=Asia/Seoul \
    --log-driver=json-file \
    --log-opt max-size=10m \
    --log-opt max-file=3 \
    $IMAGE_NAME

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to start container"
    exit 1
fi

echo "Container started successfully."

# 컨테이너 상태 확인
echo "Checking container status..."
sleep 5
docker ps -f name=$CONTAINER_NAME

echo "===== Start Application Completed ====="
date

exit 0

