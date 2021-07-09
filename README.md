# TWITTER-TRENDS
Application for collecting data from Twitter stream. 

## Application written with:
1. [Java 15](https://www.java.com/) 
2. [Spring 5](https://spring.io/) and [Spring Boot 2.5](https://spring.io/projects/spring-boot)
3. [MongoDB 4.2](https://www.mongodb.com/)

## Running

### Running with docker compose

#### Prerequisites
- Docker
- Docker Compose

#### Steps
1. Run `docker-compose up`
2. Navigate in browser to `localhost:3000`

### Running locally

#### Prerequisites
- Docker
- Java 15
- Maven
- Npm

#### Steps
1. Start Infrastructure (MongoDB): `make deps`
2. Start backend: `make be-all`
3. Start frontend: `make fe-all`
4. Navigate in browser to `localhost:3000`

#### INFO
1. Backend formatted with [google-java-format](https://github.com/google/google-java-format)
   
