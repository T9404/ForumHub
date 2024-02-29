FROM openjdk:21-jdk-slim

WORKDIR /storage

COPY /build/libs/*.jar storage.jar

EXPOSE ${APP_PORT}

CMD ["java", "-jar", "storage.jar"]