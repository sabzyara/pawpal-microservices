# Используем Java
FROM openjdk:17-jdk-slim

# Копируем проект
WORKDIR /app
COPY . .

# Собираем проект
RUN chmod +x gradlew
RUN ./gradlew build -x test

# Запускаем jar
CMD ["java", "-jar", "build/libs/*.jar"]