#!/bin/sh
#
# DO NOT REMOVE!!!!
# This file is used by the CCDB build process to start Wildfly before the build.
# The integration tests run better with the standalone Wildfly server running.
#
# The script requires that Jenkins configuration sets the following variables:
#
# JBOSS_HOME=/home/jenkins/opt/wildfly-8.1.0.Final
# LAUNCH_JBOSS_IN_BACKGROUND="x"
# JBOSS_PIDFILE=$JBOSS_HOME"/jboss.pid"
#
WAIT_TIME=25
eval $JBOSS_HOME/bin/standalone.sh "&"
echo "Waiting $WAIT_TIME seconds for JBOSS to start."
sleep $WAIT_TIME
