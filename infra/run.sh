#!/bin/bash

usage() {
	cat <<EOF

    Usage: $(basename $0) <command>

    run-docker            Starts Docker on Arch based GNU Linux.
    run-rethink           Starts RethinkDB docker image.
    run-mongo           Starts MongoDB docker image.
    run-infra             Starts Docker and RethinkDB and Redis

EOF
	exit 1
}

run-docker() {
	set -e
		sudo systemctl start docker
	set +e
}

run-rethink() {
	set -e
		docker run -p 28018:28015 -p 8082:8080 -v $PWD/rethinkdb:/data -d rethinkdb
		echo "RethinkDB is listening on ports: 28018 and 8082. Data is stored inside 'rethinkdb' directory"
	set +e
}

run-mongo() {
	set -e
		docker run -p 27017:27017 -v $PWD/mongodb:/data/db -d mongo:latest
		echo "MongoDB is listening on ports: 27017. Data is stored inside 'mongodb' directory"
	set +e
}


run-infra() {
	set -e
		run-docker
		run-rethink
		run-mongo
	set +e
}


CMD="$1"
shift
case "$CMD" in
	run-docker)
		run-docker
	;;
	run-rethink)
		run-rethink
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
