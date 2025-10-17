# Multi-stage build for Spring Boot application

# Build stage
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY src src

# Build the application
RUN gradle clean build -x test

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy the built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Create non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Environment variables
ENV SPRING_PROFILES_ACTIVE=oracle
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
