#!/bin/sh
# no need to run tests and generate JavaDoc again when deploying on the AS
mvn wildfly:deploy -DskipTests -Dmaven.javadoc.skip=true $@
