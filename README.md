
# TWITTER-TRENDS
Simple application for Twitter analysis. Application is written in [Java 8](https://www.java.com/) and [Spring 5](https://spring.io/). [MongoDB](https://www.mongodb.com/) is used as a main data storage.

### PREREQUISITES
1. JDK 8 installed
2. Maven
3. Docker 

### INIT
1. `./run.sh run-infra` or `./run.sh run-mongo` - start infrastructure 
2. `mvn clean install` - build application 
3. `java -jar target/twitter-trends-java-0.0.1-SNAPSHOT.jar` - start application 

### INSIDE
1. Spring 5 - Reactive Web Framework
2. Spring Data - Reactive MongoDB
3. RxJava (Hot and Cold Observables)
4. Server Sides Events