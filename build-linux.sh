/usr/lib/jvm/jdk-17/bin/jpackage \
  --name PeriodCountdown \
  --input ./src \
  --dest ./release \
  --verbose \
  --icon ./reference/icon.png \
  --main-jar PeriodCountdown.jar \
  --main-class main.PeriodCountdown
