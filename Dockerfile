FROM openjdk:8

MAINTAINER Jacek Spolnik <jacek.spolnik@gmail.com>

WORKDIR /app
ADD build/libs/jalgoarena-ranking-*.jar /app
RUN mkdir /app/RankingStore
VOLUME /app/RankingStore

EXPOSE 5006

CMD java -jar /app/jalgoarena-ranking-*.jar
