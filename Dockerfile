FROM openjdk:8

WORKDIR /app
ADD build/libs/* /app/
RUN mkdir /app/RankingStore
VOLUME /app/RankingStore

ENV EUREKA_URL=http://eureka:5000/eureka
ENV BOOTSTRAP_SERVERS=kafka1:9092,kafka2:9093,kafka3:9094
EXPOSE 5006

CMD java -Dserver.port=5006 -jar /app/jalgoarena-ranking-*.jar