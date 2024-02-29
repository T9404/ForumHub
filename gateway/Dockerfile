FROM openjdk:21-jdk-slim

WORKDIR /gateway

COPY /build/libs/*.jar gateway.jar

EXPOSE ${APP_PORT}

CMD ["java", "-jar", "gateway.jar"]