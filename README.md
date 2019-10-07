
# TWITTER-TRENDS
Application for collecting data from Twitter stream. 
Application written with:
1. [Java 12](https://www.java.com/) 
2. [Spring 5](https://spring.io/) and [Spring Boot 2.1](https://spring.io/projects/spring-boot)
3. [MongoDB 4.2](https://www.mongodb.com/)

### PREREQUISITES
1. JDK 12
2. Maven
3. Docker 

### INIT
1. Edit `trends-be/src/main/resources/application.properties` and adjust those properties: `twitter.consumerKey`, `twitter.consumerSecret`, `twitter.token`, `twitter.secret`
2. `./run.sh run-mongo` - starts mongo docker image 
3. `mvn clean install` - build application 
4. `java -jar target/trends-1.0.0.jar` - start application 

### INFO
1. Backend formatted with [google-java-format](https://github.com/google/google-java-format)


