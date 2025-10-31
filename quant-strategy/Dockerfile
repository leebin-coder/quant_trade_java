FROM openjdk:8-jre-alpine

WORKDIR /app

COPY target/quant-strategy-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
