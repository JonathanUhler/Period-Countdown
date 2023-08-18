#!/bin/bash


cd src/main
javadoc $(find . -name "*.java") \
		-d ../../docs/javadoc \
		-cp .:../lib/picocli.jar:../lib/jnet.jar:../lib/gson-2.2.2.jar
