# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy parent and shared library modules
COPY pom.xml .
COPY quant-parent ./quant-parent
COPY quant-common ./quant-common
COPY quant-database ./quant-database
COPY quant-cache ./quant-cache
COPY quant-message ./quant-message
COPY quant-storage ./quant-storage

# Copy current service
COPY quant-trade ./quant-trade

# 设置 Maven 内存和超时参数
ENV MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=512m -Dmaven.wagon.http.retryHandler.count=3 -Dmaven.wagon.httpconnectionManager.ttlSeconds=120"


# Build shared libraries first, then build the service
RUN cd quant-parent && mvn clean install -DskipTests && \
    cd /build/quant-trade && mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy jar from builder
COPY --from=builder /build/quant-trade/target/quant-trade-*.jar app.jar

# Create non-root user
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app

USER appuser

EXPOSE 8082

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
