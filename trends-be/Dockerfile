FROM maven:3.8-openjdk-15-slim

ADD . /trends-be
WORKDIR /trends-be

RUN mvn clean install -DskipTests

EXPOSE 8080

CMD ["bash", "start.sh"]