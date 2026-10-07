FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY . .

# Збираємо лише notification-service
RUN ./mvnw clean package -pl notification-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/notification-service/target/notification-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]