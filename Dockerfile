# Etapa 1: construir la app con Gradle (usa Java 21)
FROM gradle:8.7-jdk21 AS builder
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle build -x test

# Etapa 2: ejecutar la app (también usa Java 21)
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
