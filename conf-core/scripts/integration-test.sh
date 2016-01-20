#!/bin/sh
mvn clean package -Djacoco.out.file=../target/unit-tests.exec -Djacoco.append=false
mvn jacoco:prepare-agent-integration failsafe:integration-test -Djacoco.append=true -Dit.test=*EJBIT* -Djacoco.destFile=../target/ejb-it.exec -Djacoco.append=false $@
mvn jacoco:prepare-agent-integration failsafe:integration-test -Djacoco.append=true -Dit.test=*DataLoaderIT* -Djacoco.destFile=../target/dl-it.exec -Djacoco.append=false $@
