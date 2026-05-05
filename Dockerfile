FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :pet-service:build -x test

CMD ["java", "-jar", "pet-service/build/libs/pet-service-0.0.1-SNAPSHOT.jar"]