#!/bin/bash
set -e

export JAVA_OPTS="${JAVA_OPTS} -Djava.net.preferIPv4Stack=true -XX:MaxRAMPercentage=80"

exec java ${JAVA_OPTS} -jar ${ARTIFACT} ${@}