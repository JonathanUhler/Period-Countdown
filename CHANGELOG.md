# Period-Countdown Changelog
**NOTE TO CONTRIBUTORS:** When creating a new version of this software, make sure to update the VERSION constant in
main.PeriodCountdown.java \
Project created 8/22/21 -- Changelog begin:

## PRE-RELEASES
    version   date          changes
    ------- --------    ----------------------------------------------------------------------------------------------------
    1.0.0   9/16/21     Changes in this version:
                            -First working version of Period-Countodwn in Java

    1.1.0   9/25/21     Changes in this version:
                            -Minor code improvements
                            -Changed method in which SchoolDisplay finds the json data
                            -Created compile.sh and jar.sh to automate building a jarfile from the project
                            -Finished build-mac.sh and Mac .app package creation

    1.1.1   9/25/21     Changes in this version:
                            -Minor file restructure
                            -Changed method for Mac build to use jpackage over javapackager

    1.2.0   9/25/21     Changes in this version:
                            -Fixed a possible security issue with Period-Countdown writing to files within itself
                            -Moved all JSON data to ~/.periodcountdown/json/

    1.3.0   9/26/21     Changes in this version:
                            -Added the "School..." option to allow changing school *.json file
                            -Added a better error handling routine for when json data is missing
                            -Changed text displayed in "Period Name" and info popups to be the period name from the json data

    1.3.1   9/26/21     Changes in this vesrion:
                            -Added the ability to mark periods as free using the keywords "Free", "None", or "N/A"

    2.0.0   10/3/21     Changes in this version:
                            -Updated MVHS_School.json with 10/4/21 rally week schedule
                            -Added "Support" menu item to menu bar
                            -Added update routine requested in issue #15
                            -Fixed bug described in issue #16
                            -Added Java JDK 17+ as a dependency

    2.0.1   10/8/21     Changes in this version:
                            -Updated in-file documentation
                            -Added error popup when the "Support > Update" routine fails
                            -Added SAT/PSAT testing week schedule

    2.0.2   10/9/21     Changes in this version:
                            -Fixed minor issue in timer skipping -2 (free) periods

    2.0.3   11/17/21    Changes in this version:
                            -Updated JSON data for year, fixed finals schedule

    2.0.3.1 11/17/21    Changes in this version:
                            -PATCH FOR CRASH IN 2.0.3 IF YOU INSTALLED 2.0.3 UPDATE TO 2.0.3.1

## FULL-RELEASES