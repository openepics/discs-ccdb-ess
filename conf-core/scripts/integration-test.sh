#!/bin/sh
mvn jacoco:prepare-agent failsafe:integration-test -P jboss -Djacoco.append=true -Dit.test=*EJBIT* $@
mvn jacoco:prepare-agent failsafe:integration-test -P jboss -Djacoco.append=true -Dit.test=*DataLoaderIT* $@
