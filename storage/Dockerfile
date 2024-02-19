FROM openjdk:21-jdk-slim AS build
WORKDIR /storage
COPY . .
RUN ./gradlew build -x test

FROM openjdk:21-jdk-slim
WORKDIR /storage
COPY --from=build /storage/build/libs/*.jar /storage/app.jar
EXPOSE ${APP_PORT}
CMD ["java", "-jar", "app.jar"]