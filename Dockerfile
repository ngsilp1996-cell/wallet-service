# --- Stage 1: build ---
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Кэшируем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем
COPY src ./src
RUN mvn clean package -DskipTests && \
    ls -la target && \
    jar tf target/wallet-service-0.0.1-SNAPSHOT.jar | head && \
    jar xf target/wallet-service-0.0.1-SNAPSHOT.jar META-INF/MANIFEST.MF && \
    cat META-INF/MANIFEST.MF

# --- Stage 2: run ---
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/wallet-service-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]