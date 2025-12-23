# Dockerfile

# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /iw2025-2026-MERCA_ESI

# Copiar TODO el repo al contenedor (asegura que entra el pom.xml)
COPY . /iw2025-2026-MERCA_ESI

# Verificación rápida: falla si no existe pom.xml (diagnóstico claro)
RUN test -f /iw2025-2026-MERCA_ESI/pom.xml

# Build
RUN mvn -f /iw2025-2026-MERCA_ESI/pom.xml clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /iw2025-2026-MERCA_ESI
COPY --from=build /iw2025-2026-MERCA_ESI/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/iw2025-2026-MERCA_ESI/app.jar"]