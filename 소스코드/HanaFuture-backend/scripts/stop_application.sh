#!/bin/bash

# 스크립트 시작 로그
echo "===== Stopping Application ====="
date

# 실행 중인 컨테이너 확인
CONTAINER_NAME="hanafuture-backend"

if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
    echo "Container $CONTAINER_NAME is running. Stopping..."
    
    # 컨테이너 중지 (30초 타임아웃)
    docker stop $CONTAINER_NAME
    
    echo "Container stopped successfully."
else
    echo "Container $CONTAINER_NAME is not running. Nothing to stop."
fi

# 중지된 컨테이너 제거
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
    echo "Removing stopped container $CONTAINER_NAME..."
    docker rm $CONTAINER_NAME
    echo "Container removed successfully."
fi

# 사용하지 않는 이미지 정리 (선택사항)
echo "Cleaning up unused Docker images..."
docker image prune -f

echo "===== Stop Application Completed ====="
date

exit 0

