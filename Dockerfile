FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 5000
ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","/app.jar"]