# 멀티 스테이지 빌드를 사용한 효율적인 Docker 이미지 생성

# 1단계: 빌드 스테이지
FROM eclipse-temurin:17-jdk-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 설정 파일들 복사
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드 (캐시 최적화)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src/ src/

# application-local.yml 제거 (production 환경용)
RUN find src/main/resources -name "application-local.yml" -delete

# 애플리케이션 빌드
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 실행 스테이지
FROM eclipse-temurin:17-jre-alpine

# 필수 패키지 설치 (Alpine 방식)
RUN apk add --no-cache \
    curl \
    tzdata

# 애플리케이션 사용자 생성 (Alpine 방식)
RUN addgroup -g 1001 -S openbanking && \
    adduser -u 1001 -S openbanking -G openbanking

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 소유권 변경
RUN chown -R openbanking:openbanking /app

# 사용자 전환
USER openbanking

# 포트 노출
EXPOSE 8081

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# JVM 옵션 설정 (메모리 최적화 - EC2 최적화)
ENV JAVA_OPTS="-Xms256m -Xmx768m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
