#!/bin/sh
java -Xdebug -Xmx2048M -XX:+CMSClassUnloadingEnabled -XX:PermSize=256M -jar ./sbt-launch-0.12.2.jar "$@"
