docker:
	sudo systemctl start docker

compose-build:
	sudo docker-compose build

compose-up:
	sudo docker-compose up

deps:
	echo "starting mongo image (version 4.2.0)"
	docker run -p 27017:27017 -d mongo:4.2.0

fe-get:
	echo "getting frontend dependencies" 
	cd trends-fe && npm install

fe-run: 
	echo "running frontend"
	cd trends-fe && npm run start

fe-all: fe-get fe-run

be-test: 
	echo "running backend tests"
	cd trends-be && mvn clean test

be-build: 
	echo "building backend"
	cd trends-be && mvn clean package

be-run: 
	echo "running backend"
	cd trends-be && java -jar trends-1.0.0.jar

be-all: be-test be-build be-run
