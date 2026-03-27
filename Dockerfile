# Используем Java
FROM eclipse-temurin:17-jdk

# Копируем проект
WORKDIR /app
COPY . .

# Собираем проект
RUN chmod +x gradlew
RUN ./gradlew build -x test

# Запускаем jar
CMD ["java", "-jar", "build/libs/*.jar"]