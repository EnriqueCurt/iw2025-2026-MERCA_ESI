# syntax=docker/dockerfile:1.6

# Etapa de construcción
FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app

# Copia mínima para cachear dependencias
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw mvnw
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests dependency:go-offline

# Copia el resto del proyecto y compila
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests clean package

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Render suele inyectar PORT; Spring escucha 8080 por defecto, lo ajustamos
ENV PORT=8080
EXPOSE 8080

COPY --from=build /app/target/*.jar /app/app.jar
ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT} -jar /app/app.jar"]