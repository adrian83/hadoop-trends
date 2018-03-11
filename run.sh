#!/bin/bash

usage() {
	cat <<EOF

    Usage: $(basename $0) <command>

    run-docker            Starts Docker on Arch based GNU Linux.
    run-mongo             Starts MongoDB docker image.
    run-infra             Starts Docker and RethinkDB and Redis.
    start-be              Builds and starts backend app.

EOF
	exit 1
}

run-docker() {
	set -e
		sudo systemctl start docker
	set +e
}

run-mongo() {
	set -e
		docker run -p 27021:27017 -v $PWD/trends-be/infra/mongodb:/infra/data/db -d mongo:latest
		echo "MongoDB is listening on ports: 27021. Data is stored inside 'trends-be/infra/mongodb' directory"
	set +e
}

run-infra() {
	set -e
		run-docker
		run-mongo
	set +e
}

start-be() {
	set -e
		cd trends-be
		mvn clean install
		java -jar target/twitter-trends-1.0.0.jar
	set +e
}

CMD="$1"
shift
case "$CMD" in
	run-docker)
		run-docker
	;;
	run-mongo)
		run-mongo
	;;
	run-infra)
		run-infra
	;;
	start-be)
		start-be
	;;
	*)
		usage
	;;
esac
