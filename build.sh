#!/bin/bash


usage() {
	echo "Usage: $0 [options]";
	echo "       [-t <target>]    (Builds for \"mac\", \"linux\", \"windows\", or \"web\")"
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


echo "[build.sh] Reading version"
version=$(<src/assets/VERSION)
if [ $? -eq 0 ]; then
	echo "[build.sh] Done"
else
	echo "[build.sh] Could not read version"
	exit 1
fi

version_regex="^[0-9]+\.[0-9]+\.[0-9]+$"
if [[ $version =~ $version_regex ]]; then
	echo "[build.sh] Version regex check passed"
else
	echo "[build.sh] Version is not formatted correctly! Version should be 3 numbers separated by dots"
	echo "[build.sh]     Given version number was: \"$version\""
	exit 1
fi


if [ "$target" = "mac" ]; then
	jpackage \
		--name PeriodCountdown \
		--app-version $version \
		--input bin\
		--dest release \
		--verbose \
		--icon src/assets/icon.icns \
		--main-jar PeriodCountdown-native.jar \
		--main-class natfe.PeriodCountdown \
		--mac-package-name "Period Countdown"
elif [ "$target" = "linux" ]; then
	jpackage \
		--name PeriodCountdown \
		--app-version $version \
		--input bin\
		--dest release \
		--verbose \
		--icon src/assets/icon.png \
		--main-jar PeriodCountdown-native.jar \
		--main-class natfe.PeriodCountdown
elif [ "$target" = "windows" ]; then
	jpackage \
		--name PeriodCountdown \
		--app-version $version \
		--input bin \
		--dest release \
		--verbose \
		--icon src/assets/icon.png \
		--main-jar PeriodCountdown-native.jar \
		--main-class natfe.PeriodCountdown
elif [ "$target" = "web" ]; then
	mkdir -p "release/PeriodCountdown-$version-web/"
	rm -rf "release/PeriodCountdown-$version-web/"
	cp -a bin/ "release/PeriodCountdown-$version-web/"
else
	echo "[build.sh] Target $target not recognized, cannot build"
	exit 1
fi
