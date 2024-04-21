FROM openjdk:17-slim as build

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar
COPY run.sh run.sh
COPY application.yml application.yml
COPY conf conf

ENTRYPOINT ["sh", "run.sh"]