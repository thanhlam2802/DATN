FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copy source code
COPY src src

# Make mvnw executable and build
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Switch to JRE for runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy built JAR from build stage
COPY --from=0 /app/target/Notification-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9821

ENV PORT=9821

ENTRYPOINT ["java", "-jar", "app.jar"] 