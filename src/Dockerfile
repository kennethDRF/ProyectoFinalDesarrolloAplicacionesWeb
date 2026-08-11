# ============================================================
# Dockerfile - barbex (deploy en Render)
# ============================================================
# Build en 2 etapas: la primera compila con Maven + JDK completo (pesada),
# la segunda copia SOLO el .jar ya compilado a una imagen con JRE (liviana).
# ============================================================

# Etapa 1: compilar el proyecto con Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -q clean package -DskipTests

# Etapa 2: imagen final, solo el JRE + el .jar ya compilado
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/barbex-0.0.1-SNAPSHOT.jar app.jar

# Render inyecta la variable de entorno PORT en runtime - application-prod.properties
# ya usa server.port=${PORT:8080} para escuchar en el puerto correcto.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]