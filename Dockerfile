# Etapa 1: Compilación
FROM gradle:8.9-jdk17 AS builder

COPY . /home/app/
WORKDIR /home/app

# Dar permisos de ejecución a gradlew
RUN chmod +x ./gradlew

# Construir el JAR (sin tests)
RUN ./gradlew build --no-daemon -x test

# Etapa 2: Ejecución
FROM amazoncorretto:17-alpine-jdk

RUN mkdir -p /app

COPY --from=builder /home/app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]