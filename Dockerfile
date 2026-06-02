FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :ai-analytics-service:build -x test

CMD ["java", "-jar", "ai-analytics-service/build/libs/ai-analytics-service-0.0.1-SNAPSHOT.jar"]
