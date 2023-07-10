#!/bin/bash


cd src/main
javadoc **/*.java \
		-d ../../documentation/javadoc \
		-cp .:../lib/picocli.jar:../lib/jnet.jar:../lib/gson-2.2.2.jar
