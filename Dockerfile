# syntax=docker/dockerfile:1

FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /repo

# Copia todo el repo (contexto)
COPY . .

# Entra al subdirectorio real donde vive el pom.xml
WORKDIR /repo/iw2025-2026-MERCA_ESI

# Diagnóstico claro
RUN ls -la && test -f pom.xml

# Vaadin en producción
RUN mvn -B -Pproduction -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV PORT=8080
EXPOSE 8080

COPY --from=build /repo/iw2025-2026-MERCA_ESI/target/*.jar /app/app.jar
ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT} -jar /app/app.jar"]