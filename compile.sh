#!/bin/bash


target="native"

usage() {
	echo "Usage: $0 [options]";
	echo "       [-t <target>]    (Builds for \"native\" or \"web\". Default $target)"
	}

while getopts "ht:" flag
do
	case $flag in
		h)
			usage
			exit
			;;
		t)
			target=${OPTARG}
			;;
		*)
			usage
			exit
			;;
	esac
done


echo "[compile.sh] Clearing obj directory"
rm -rf obj/*


echo "[compile.sh] Compiling source for target \"$target\""
if [ "$target" = "native" ]; then
	javac -Xlint:unchecked -cp 'src/lib/*' -d obj/ $(find src/main -name '*.java'|grep -v webfe)
elif [ "$target" = "web" ]; then
	javac -Xlint:unchecked -cp 'src/lib/*' -d obj/ $(find src/main -name '*.java'|grep -v natfe)
else
	echo "[compile.sh] Target $target not recognized, cannot selectively compile source"
	exit 1
fi
