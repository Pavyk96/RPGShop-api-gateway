FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/api-gateway-0.0.1-SNAPSHOT-plain.jar /app/api-gateway.jar

ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]
