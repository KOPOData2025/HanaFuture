#!/bin/bash

# 스크립트 시작 로그
echo "===== Health Check Started ====="
date

# 변수 설정
CONTAINER_NAME="hanafuture-backend"
HEALTH_CHECK_URL="http://127.0.0.1:8080/actuator/health"  # IPv4 명시적 사용 (IPv6 문제 방지)
MAX_ATTEMPTS=40  # 최대 40번 시도 (40 * 10초 = 6.5분)
SLEEP_TIME=10    # 10초마다 체크

# 컨테이너 실행 확인
echo "Checking if container is running..."
if [ ! "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
    echo "ERROR: Container $CONTAINER_NAME is not running!"
    exit 1
fi

echo "Container is running. Checking application health..."

# Health Check 반복
for i in $(seq 1 $MAX_ATTEMPTS); do
    echo "Attempt $i/$MAX_ATTEMPTS: Checking $HEALTH_CHECK_URL"
    
    # HTTP 요청
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_CHECK_URL)
    
    if [ "$HTTP_STATUS" = "200" ]; then
        echo " Health check passed! Application is healthy."
        
        # 추가 정보 출력
        echo "Health check response:"
        curl -s $HEALTH_CHECK_URL
        
        echo "===== Health Check Completed Successfully ====="
        date
        exit 0
    fi
    
    echo "Health check returned status: $HTTP_STATUS (expected: 200)"
    
    if [ $i -lt $MAX_ATTEMPTS ]; then
        echo "Waiting ${SLEEP_TIME} seconds before next attempt..."
        sleep $SLEEP_TIME
    fi
done

# 모든 시도 실패
echo " ERROR: Health check failed after $MAX_ATTEMPTS attempts."
echo "Container logs:"
docker logs --tail 50 $CONTAINER_NAME

echo "===== Health Check Failed ====="
date

exit 1

