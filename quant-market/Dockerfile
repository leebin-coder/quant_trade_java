FROM openjdk:8-jre-alpine

WORKDIR /app

COPY target/quant-market-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
