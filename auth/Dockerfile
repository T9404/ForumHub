FROM openjdk:21-jdk-slim

WORKDIR /auth

COPY /build/libs/*.jar auth.jar

EXPOSE ${APP_PORT}

CMD ["java", "-jar", "auth.jar"]
