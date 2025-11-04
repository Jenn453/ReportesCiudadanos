# Etapa 1: construir la app con Gradle
FROM gradle:8.7-jdk17 AS builder
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle build -x test

# Etapa 2: ejecutar la app
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]