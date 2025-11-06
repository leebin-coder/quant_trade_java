# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy parent pom and quant-parent first for better caching
COPY ../pom.xml .
COPY ../quant-parent ./quant-parent

# Copy common modules
COPY ../quant-common ./quant-common

# Copy current service
COPY pom.xml ./quant-user/pom.xml
COPY src ./quant-user/src

# Build the application (skip tests for faster builds)
RUN mvn clean package -pl quant-user -am -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy jar from builder
COPY --from=builder /build/quant-user/target/quant-user-*.jar app.jar

# Create non-root user
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app

USER appuser

EXPOSE 8081

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
