FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew :pet-management:build -x test

CMD ["java", "-jar", "pet-management/build/libs/pet-management-0.0.1-SNAPSHOT.jar"]