# Etapa 1: construir el JAR
FROM gradle:8.7-jdk17 AS builder
WORKDIR /home/gradle/src
COPY . .
RUN gradle build --no-daemon

# Etapa 2: ejecutar la app
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
