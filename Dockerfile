# Используем Java
FROM eclipse-temurin:17-jdk

# Копируем проект
WORKDIR /app
COPY . .


RUN chmod +x gradlew
RUN ./gradlew build -x test

# Запускаем jar
CMD ["java", "-jar", "build/libs/*.jar"]