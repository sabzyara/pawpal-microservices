FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :user-service:build -x test

CMD ["java", "-jar", "user-service/build/libs/user-service-0.0.1-SNAPSHOT.jar"]