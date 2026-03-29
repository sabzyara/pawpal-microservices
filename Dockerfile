FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :notificcation-service:build -x test

CMD ["java", "-jar", "notification-service/build/libs/notification-service-0.0.1-SNAPSHOT.jar"]