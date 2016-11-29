#!/bin/bash

usage() {
	cat <<EOF

    Usage: $(basename $0) <command>

    run-docker            Starts Docker on Arch based GNU Linux.
    run-rethink           Starts RethinkDB docker image.
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
		docker run -p 28017:28015 -p 8082:8080 -v $PWD/rethinkdb:/data -d rethinkdb
		echo "RethinkDB is listening on ports: 28017 and 8082. Data is stored inside 'rethinkdb' directory"
	set +e
}


run-infra() {
	set -e
		run-docker
		run-rethink
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
	run-infra)
		run-infra
	;;
	*)
		usage
	;;
esac
