FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :eureka-server:build -x test

CMD ["java", "-jar", "eureka-server/build/libs/eureka-server-0.0.1-SNAPSHOT.jar"]