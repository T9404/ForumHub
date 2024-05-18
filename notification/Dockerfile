FROM openjdk:21-jdk-slim

WORKDIR /notification

COPY /build/libs/*.jar notification.jar

EXPOSE ${APP_PORT}

CMD ["java", "-jar", "notification.jar"]
