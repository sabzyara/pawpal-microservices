FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :appointment-service:build -x test

CMD ["java", "-jar", "appointment-service/build/libs/appointment-service-0.0.1-SNAPSHOT.jar"]