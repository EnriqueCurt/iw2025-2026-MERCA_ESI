# syntax=docker/dockerfile:1

# Etapa de build
FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app

# Copia TODO lo que haya en el contexto
COPY . .

# Diagnóstico: deja claro si el contexto no es la raíz del proyecto
RUN ls -la && test -f pom.xml

# Compila (activa el perfil production para Vaadin)
RUN mvn -B -Pproduction -DskipTests clean package

# Etapa de runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

ENV PORT=8080
EXPOSE 8080

COPY --from=build /app/target/*.jar /app/app.jar
ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT} -jar /app/app.jar"]