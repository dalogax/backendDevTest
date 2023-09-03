FROM maven:3.9.3 AS maven

WORKDIR /usr/src/app
COPY . /usr/src/app

RUN mvn package -DskipTests

FROM openjdk:17.0.2-jdk
ARG JAR_FILE=napptilus-technical-test.jar

WORKDIR /opt/app
COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/

ENTRYPOINT ["java","-jar","napptilus-technical-test.jar"]

