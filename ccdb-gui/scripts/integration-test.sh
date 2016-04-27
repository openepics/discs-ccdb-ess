#!/bin/sh
mvn clean package jacoco:prepare-agent-integration failsafe:integration-test -P jboss,development,noRbac -Dit.test=*EJBIT* -Djacoco.unit.skip=true $@
mvn jacoco:prepare-agent-integration failsafe:integration-test -P jboss,development,noRbac -Dit.test=*DataLoaderIT* $@
