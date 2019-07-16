FROM maven:3-jdk-12

ADD . /trends-be
WORKDIR /trends-be



RUN mvn clean install -DskipTests

CMD ["java", "-jar", "./target/trends-1.0.0.jar", "-Dspring.config.location=classpath:compose.properties"]


