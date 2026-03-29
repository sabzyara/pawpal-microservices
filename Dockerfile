FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :specialist-service:build -x test

CMD ["java", "-jar", "specialist-service/build/libs/specialist-service-0.0.1-SNAPSHOT.jar"]