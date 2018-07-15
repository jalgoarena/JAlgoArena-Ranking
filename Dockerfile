FROM openjdk:8-jre-alpine

MAINTAINER Jacek Spolnik <jacek.spolnik@gmail.com>

WORKDIR /app
COPY build/libs/jalgoarena-ranking-*.jar /app

VOLUME /app/RankingStore

EXPOSE 5006

CMD ["/usr/bin/java", "-jar", "/app/jalgoarena-ranking-*.jar"]
