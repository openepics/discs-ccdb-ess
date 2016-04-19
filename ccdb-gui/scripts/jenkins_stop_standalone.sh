#!/bin/sh
#
# DO NOT REMOVE!!!!
# This file is used by the CCDB build process to stop Wildfly after the build.
# See jenkins_start_standalone.sh for additional information.
#
JBOSS_PID=`cat $JBOSS_PIDFILE`
kill -HUP $JBOSS_PID
sleep 1
rm -rf $JBOSS_PIDFILE
