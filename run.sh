#!/bin/bash

usage() {
	cat <<EOF

    Usage: $(basename $0) <command>

    start                 Starts the app.
    run-docker            Starts Docker on Arch based GNU Linux.
    run-mongo             Starts MongoDB docker image.
    run-infra             Starts Docker and RethinkDB and Redis

EOF
	exit 1
}

start() {
	set -e
		mvn clean package && java -jar target/twitter-trends-java-0.0.1-SNAPSHOT.jar
	set +e
}

run-docker() {
	set -e
		sudo systemctl start docker
	set +e
}

run-mongo() {
	set -e
		docker run -p 27021:27017 -v $PWD/mongodb:/infra/data/db -d mongo:latest
		echo "MongoDB is listening on ports: 27021. Data is stored inside 'mongodb' directory"
	set +e
}


run-infra() {
	set -e
		run-docker
		run-mongo
	set +e
}


CMD="$1"
shift
case "$CMD" in
	start)
		start
	;;
	run-docker)
		run-docker
	;;
	run-mongo)
		run-mongo
	;;
	run-infra)
		run-infra
	;;
	*)
		usage
	;;
esac
