mvn jacoco:prepare-agent-integration failsafe:integration-test -P jboss "-Dit.test=*EJBIT*" "-Djacoco.unit.skip=true" %*
mvn jacoco:prepare-agent-integration failsafe:integration-test -P jboss "-Dit.test=*DataLoaderIT*" %*
