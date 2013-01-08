#!/bin/bash
MAVEN_OPTS="-Xmx1G" mvn clean package exec:java -Dexec.mainClass="test.MainApp" $*
