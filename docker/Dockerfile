FROM ubuntu:20.04

ENV PG_HOST=things.c9ey4qksejme.ap-northeast-2.rds.amazonaws.com \
    PG_PORT=5432 \
    PG_DATABASE=postgres \
    PG_USER=postgres \
    PG_PASSWORD=thingsdb1234

RUN apt-get update && apt-get install -y openjdk-17-jdk
RUN mkdir /server
COPY server/api-server.jar /server

CMD ["java", "-jar", "/server/api-server.jar"]
