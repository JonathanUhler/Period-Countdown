# Period-Countdown Changelog
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

## FULL-RELEASES