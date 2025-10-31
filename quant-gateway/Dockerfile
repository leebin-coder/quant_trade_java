FROM openjdk:8-jre-alpine

WORKDIR /app

COPY target/quant-gateway-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
