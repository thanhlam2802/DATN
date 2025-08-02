FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/Notification-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9821

ENV PORT=9821

ENTRYPOINT ["java", "-jar", "app.jar"] 