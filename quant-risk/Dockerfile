# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy all pom files first for better dependency caching
COPY pom.xml .
COPY quant-parent/pom.xml ./quant-parent/
COPY quant-common/pom.xml ./quant-common/
COPY quant-database/pom.xml ./quant-database/
COPY quant-cache/pom.xml ./quant-cache/
COPY quant-message/pom.xml ./quant-message/
COPY quant-storage/pom.xml ./quant-storage/
COPY quant-risk/pom.xml ./quant-risk/

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN cd quant-parent && mvn dependency:go-offline -DskipTests || true

# Now copy the actual source code
COPY quant-common ./quant-common
COPY quant-database ./quant-database
COPY quant-cache ./quant-cache
COPY quant-message ./quant-message
COPY quant-storage ./quant-storage
COPY quant-risk ./quant-risk

# Build the application
RUN cd quant-parent && mvn clean install -N -DskipTests && \
    cd /build/quant-common && mvn clean install -DskipTests && \
    cd /build/quant-database && mvn clean install -DskipTests && \
    cd /build/quant-cache && mvn clean install -DskipTests && \
    cd /build/quant-message && mvn clean install -DskipTests && \
    cd /build/quant-storage && mvn clean install -DskipTests && \
    cd /build/quant-risk && mvn clean package -DskipTests

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
