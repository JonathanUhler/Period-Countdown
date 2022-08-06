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


echo "[jar.sh] Clearing bin directory"
rm -rf bin/*


echo "[jar.sh] Copying lib dependencies for target \"$target\""
echo "[jar.sh] Setting manifest file for target \"$target\""
if [ "$target" = "native" ]; then
	mkdir bin/lib
	cp src/lib/gson-2.2.2.jar bin/lib
	manifest="manifest-native.mf"
elif [ "$target" = "web" ]; then
	cp -a src/lib/ bin/lib
	manifest="manifest-web.mf"
	echo "[jar.sh] Copying javascript files to bin/ for web build"
	cp -a src/main/webfe/js bin/js
else
	echo "[jar.sh] Target $target not recognized, cannot package dependencies"
	exit 1
fi


echo "[jar.sh] Creating jar file"
jar cmf $manifest bin/PeriodCountdown-$target.jar -C obj/ . -C src/ assets/
