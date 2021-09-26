/usr/lib/jvm/jdk-17/bin/jpackage \
  --name PeriodCountdown \
  --input ./lib \
  --input ./json \
  --input ./src \
  --dest ./release/ \
  --verbose \
  --main-jar PeriodCountdown.jar \
  --main-class main.PeriodCountdown
