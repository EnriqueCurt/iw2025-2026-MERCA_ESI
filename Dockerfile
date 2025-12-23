# Dockerfile

# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar TODO el repo al contenedor (asegura que entra el pom.xml)
COPY . /app

# Verificación rápida: falla si no existe pom.xml (diagnóstico claro)
RUN test -f /app/pom.xml

# Build
RUN mvn -f /app/pom.xml clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]