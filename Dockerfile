FROM eclipse-temurin:21.0.7_6-jre-alpine

ARG PROJECT_VERSION="0.0.1-SNAPSHOT"
ENV PROJECT_VERSION="$PROJECT_VERSION"
ENV MAIN_DIR="/app"

RUN addgroup -S backprod && adduser -S backprod -G backprod && \
    mkdir -p "/app" && \
    chown -R backprod:backprod "/app" && \
    chgrp -R backprod $JAVA_HOME && \
    chmod ugo+rwx -R /var/log && \
    mkdir /var/log/backprod && \
    chmod ugo+rwx -R /var/log/backprod && \
    chmod -R g+rw $JAVA_HOME

ADD bootstrap/target/bootstrap-${PROJECT_VERSION}.jar /app/app.jar

WORKDIR $MAIN_DIR

ADD start.sh /app/start.sh
RUN chmod +x /app/start.sh

USER backprod

ENTRYPOINT [ "sh", "/app/start.sh" ]