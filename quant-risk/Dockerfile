# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy parent pom and quant-parent first for better caching
COPY pom.xml .
COPY quant-parent ./quant-parent

# Copy all common modules
COPY quant-common ./quant-common
COPY quant-database ./quant-database
COPY quant-cache ./quant-cache
COPY quant-message ./quant-message
COPY quant-storage ./quant-storage

# Copy current service
COPY quant-risk ./quant-risk

# Build the application (skip tests for faster builds)
RUN cd quant-parent && mvn clean install -DskipTests && \
    cd /build && mvn clean package -pl quant-risk -am -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy jar from builder
COPY --from=builder /build/quant-risk/target/quant-risk-*.jar app.jar

# Create non-root user
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app

USER appuser

EXPOSE 8083

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
