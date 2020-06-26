
docker:
	sudo systemctl start docker

compose-build:
	sudo docker-compose build

compose-up:
	sudo docker-compose up

deps:
	echo "starting mongodb image (version 4.2.0)"
	docker run -d -p 27017:27017 mongo:4.2.0

fe-get:
	echo "getting frontend dependencies" 
	cd trends-fe && npm install 

fe-run: 
	echo "running frontend"
	cd trends-fe && npm run start

fe-all: fe-get fe-run


be-test: 
	echo "testing backend" 
	cd trends-be && mvn clean test 

be-build: 
	echo "building backend" 
	cd trends-be && mvn clean install -DskipTests
	
be-run: 
	echo "running backend"
	java -jar trends-be/target/trends-1.0.0.jar 

be-all: be-test be-build be-run