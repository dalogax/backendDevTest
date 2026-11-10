FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
COPY shared/simulado/mocks.json ./shared/simulado/mocks.json
RUN mvn -B -ntp verify

FROM eclipse-temurin:21-jre
RUN groupadd --system app && useradd --system --gid app --home-dir /app app
WORKDIR /app
COPY --from=build --chown=app:app /app/target/similar-products-1.0.0.jar app.jar
USER app
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]
