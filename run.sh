#!/bin/bash

usage() {
	cat <<EOF

    Usage: $(basename $0) <command>

    run-docker            Starts Docker daemon (systemd).
    run-infra             Starts MongoDB docker image.
    start-be              Builds and starts backend app.
    start-fe              Builds and starts frontend app (dev mode).

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
		docker run -p 27017:27017 -v $PWD/trends-be/infra/mongodb:/infra/data/db -d mongo:latest
		echo "MongoDB is listening on ports: 27017. Data is stored inside 'trends-be/infra/mongodb' directory"
	set +e
}

run-infra() {
	set -e
		run-mongo
	set +e
}

start-be() {
	set -e
		cd trends-be
		mvn clean install
		java -jar target/trends-1.0.0.jar
	set +e
}

start-fe() {
	set -e
		cd trends-fe
		npm start
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
	start-fe)
		start-fe
	;;
	*)
		usage
	;;
esac
