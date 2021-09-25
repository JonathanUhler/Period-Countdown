#!/bin/zsh
javac -cp lib/gson-2.2.2.jar -d jv/ src/calendar/*.java src/school/*.java src/graphics/*.java src/main/*.java
#java -cp jv:lib/gson-2.2.2.jar main.PeriodCountdown