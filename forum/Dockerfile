FROM openjdk:21-jdk-slim

WORKDIR /forum

COPY /build/libs/*.jar forum.jar

EXPOSE ${APP_PORT}

CMD ["java", "-jar", "forum.jar"]