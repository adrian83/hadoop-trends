
# TWITTER-TRENDS
Simple application for Twitter analysis. Application is written in [Java 8](https://www.java.com/) and [Spring](https://spring.io/). [MongoDB](https://www.mongodb.com/) is also used.

### PREREQUISITES
1. JDK 8 installed
2. Maven
3. Docker 
4. MongoDB CLient app

### INIT
1. `./run.sh run-infra` - start infrastructure (bash)
2. `mongo --port=27021` - connect to mongodb server (bash)
3. `use twitter_trends` - create database (mongodb client)
4. `db.createCollection('hashtags')` - create collection for hashtags (mongodb client)
5. `mvn clean install` - build application (bash)
6. `java -jar target/twitter-trends-java-0.0.1-SNAPSHOT.jar` - start application (bash)
