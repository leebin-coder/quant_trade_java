FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/quant-strategy-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8085

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
