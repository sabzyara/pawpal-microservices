FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :user-service:build -x test

CMD ["java", "-jar", "pet-management-service/build/libs/pet-management-service-0.0.1-SNAPSHOT.jar"]