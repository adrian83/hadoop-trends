FROM maven:3-jdk-14

ADD . /trends-be
WORKDIR /trends-be



RUN mvn clean install -DskipTests

EXPOSE 8080

CMD ["bash", "start.sh"]