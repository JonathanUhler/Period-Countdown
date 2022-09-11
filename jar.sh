#!/bin/bash


target="desktop"

usage() {
	echo "Usage: $0 [options]";
	echo "       [-t <target>]    (Builds for \"desktop\" or \"web\". Default $target)"
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
mkdir -p bin
rm -rf bin/*


echo "[jar.sh] Copying lib dependencies for target \"$target\""
if [ "$target" = "desktop" ]; then
	mkdir bin/lib
	cp src/lib/gson-2.2.2.jar bin/lib
elif [ "$target" = "web" ]; then
	cp -a src/lib bin/lib
	echo "[jar.sh] Copying python files to bin/ for web build"
	cp -a src/main/web/server bin/server
	echo "[jar.sh] Copying key generation scripts for web build"
	mkdir bin/keys
	cp $(find src/assets/web/keys -name '*.sh') bin/keys # Ignore any actual key/cert files
	echo "[jar.sh] Copying website config and logs for web build"
	cp -a src/assets/web/logs bin/logs
	cp src/assets/web/period-countdown.conf bin/
else
	echo "[jar.sh] Target $target not recognized, cannot package dependencies"
	exit 1
fi

echo "[jar.sh] Setting manifest file for target \"$target\""
manifest="manifest-$target.mf"


echo "[jar.sh] Creating jar file"
jar cmf $manifest bin/PeriodCountdown-$target.jar -C obj/ . -C src/ assets/
