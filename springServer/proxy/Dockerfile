FROM openjdk:17
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 5000

CMD ["java", "-jar", "app.jar"]