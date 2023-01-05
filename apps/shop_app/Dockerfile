FROM openjdk:17-jdk-alpine as builder

WORKDIR /build

COPY ./pom.xml pom.xml
COPY ./shop/pom.xml ./shop/pom.xml
COPY ./apps/shop_app/.mvn ./.mvn
COPY ./apps/shop_app/mvnw .
COPY ./apps/shop_app/pom.xml ./apps/shop_app/pom.xml

RUN ./mvnw -q -ntp -B -pl shop -am dependency:go-offline
COPY shop shop

RUN ./mvnw -q -B -pl shop install

#RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
RUN ./mvnw -q -ntp -B -pl ./apps/shop_app -am dependency:go-offline

COPY ./apps/shop_app ./apps/shop_app

#RUN ./mvnw clean package -DskipTests
RUN ./mvnw -q -ntp -B -pl shop,./apps/shop_app clean package -DskipTests

RUN mkdir -p /jar-layers
WORKDIR /jar-layers
# Extract JAR layers
RUN java -Djarmode=layertools -jar /build/apps/shop_app/target/*.jar extract

#FROM adoptopenjdk/openjdk11:centos-jre
FROM openjdk:17-jdk-alpine

RUN mkdir -p /app
WORKDIR /app

# Copy JAR layers, layers that change more often should go at the end
COPY --from=builder /jar-layers/dependencies/ ./
COPY --from=builder /jar-layers/spring-boot-loader/ ./
COPY --from=builder /jar-layers/snapshot-dependencies/ ./
COPY --from=builder /jar-layers/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

#FROM openjdk:17-jdk-alpine
#
#WORKDIR /app
#RUN mkdir ./logs
#RUN ls -la
#
#COPY --from=builder /app/build/target/shop_app-1.0.0-SNAPSHOT.jar .
#
#EXPOSE 5000
#
#CMD ["java", "-jar", "shop_app-1.0.0-SNAPSHOT.jar"]