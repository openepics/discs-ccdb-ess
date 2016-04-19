#!/bin/sh
# This is command line used on the jenkins build server to trigger the full build and test of the CCDB
mvn clean package failsafe:integration-test -P jboss -U -e
