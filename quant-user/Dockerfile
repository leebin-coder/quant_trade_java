FROM openjdk:8-jre-alpine

WORKDIR /app

COPY target/quant-user-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
