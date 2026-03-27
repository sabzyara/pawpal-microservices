FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :api-gateway:build -x test

CMD ["java", "-jar", "api-gateway/build/libs/api-gateway-0.0.1-SNAPSHOT.jar"]