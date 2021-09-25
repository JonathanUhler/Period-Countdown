# c - create a jar archive
# v - generate verbose output
# m - specify manifest file to manifest.mf
# f - set the file name of the jar to PeriodCountdown.jar
# C - cd to the given directory and reference the classes in that directory
jar cvmf manifest.mf PeriodCountdown.jar lib/*.jar -C jv/ .

#jar cvmf manifest.mf PeriodCountdown.jar -C jv/ .

#jar cvmf manifest.mf PeriodCountdown.jar `find . -not -path "*/.DS_Store/*"` -C jv/ . -C json/ .