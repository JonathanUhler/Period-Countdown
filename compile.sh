#!/bin/zsh
javac -cp lib/gson-2.2.2.jar -d jv/ src/*.java school/*.java graphics/*.java
java -cp jv:lib/gson-2.2.2.jar PeriodCountdown