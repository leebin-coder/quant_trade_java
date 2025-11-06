# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy all project files
COPY pom.xml .
COPY quant-parent ./quant-parent
COPY quant-common ./quant-common
COPY quant-database ./quant-database
COPY quant-cache ./quant-cache
COPY quant-message ./quant-message
COPY quant-storage ./quant-storage
COPY quant-gateway ./quant-gateway

# Build from parent using reactor (builds all modules in correct order)
RUN cd quant-parent && mvn clean package -DskipTests -pl com.quant:quant-gateway -am

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy jar from builder
COPY --from=builder /build/quant-gateway/target/quant-gateway-*.jar app.jar

# Create non-root user
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
