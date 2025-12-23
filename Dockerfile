# Dockerfile multi-stage para Sistema Ticketero
# Optimizado para producción con imagen mínima

# Etapa 1: Build
FROM openjdk:17-jdk-slim AS builder

# Instalar Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM openjdk:17-jre-slim

# Crear usuario no-root para seguridad
RUN addgroup --system ticketero && adduser --system --group ticketero

# Directorio de trabajo
WORKDIR /app

# Copiar JAR desde etapa de build
COPY --from=builder /app/target/ticketero-*.jar app.jar

# Cambiar propietario
RUN chown -R ticketero:ticketero /app

# Cambiar a usuario no-root
USER ticketero

# Configuración JVM optimizada
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Puerto de la aplicación
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]