# Period-Countdown Changelog
**NOTE TO CONTRIBUTORS:** When creating a new version of this software, make sure to update the VERSION file at ```Period-Countdown/src/assets/VERSION```. Please see CONTRIBUTING.md for more information and style guidelines. \
\
All notable changes to Period-Countdown should be documented in this file.

## [M.m.p] - M/D/YY
### Added
### Fixed
### Changed

## [5.1.2] - 10/17/22
### Added
### Fixed
* Change "Brunch" for G schedule from "Nothing" to "Special"
### Changed

## [5.1.1] - 10/17/22
### Added
### Fixed
### Changed
* Enable "Next Up" by default (allow viewing on the generic website)

## [5.1.0] - 10/16/22

## [5.1.1] - 10/17/22
### Added
### Fixed
### Changed
* Enable "Next Up" by default (allow viewing on the generic website)

## [5.1.0] - 10/16/22
### Added
### Fixed
### Changed
* Moved log and pid files to command line options instead of hard-coded paths

## [5.0.0] - 10/15/22
### Added
* Minor documentation changes and additions
### Fixed
### Changed
* Change the structure of the User.json file to include a "Schools" table that can contain period info for many schools

## [4.0.2] - 10/13/22
### Added
### Fixed
### Changed
* Web build now comes as a tar.gz archive

## [4.0.1] - 10/13/22
### Added
* Add a "ExpireTime" field to GetTimeRemaining, indicating the end of the current period (which may be the same as "EndTime")
### Fixed
* Fix an issue with merging periods to calculate time remaining on the web build
### Changed
* Minor syntax changes to GetTimeRemaining

## [4.0.0] - 10/2/22
### Added
* Add a .wsgi file to load the web server from
* Add a logs folder and blank log files to the web build
* Add the apache configuration file period-countdown.conf to the web build
* Add a favicon
* Transport will now write to a pid file
* Add upcoming special schedules
### Fixed
### Changed
* Change the abilities of the gen_keys.sh script (now only generates for the transport)
* Remove the "meta refresh" test line from index.html -- this was purly for testing and a better solution will be implemented
* Move server configuration files to src/assets/web
* Add a required timezone code tag to the school (or user) json files
* All internal operations are now done in UTC, before being converted to the client's timezone as needed
* Logging operations simplified slightly, log files for transport and server added

## [3.1.0] - 9/4/22
### Added
* Add general framework for web-server
  * Add specification for web communcation to documents/
  * Create a separate "main" package for the web build (web.transport instead of desktop) and a Java transport system
  * Create a Python web server to display generic data (from default User.json and MVHS_School.json) for now
    * Future releases will likely include database functionality, allowing the same features as the native app
* Add a constructor flag to UserAPI that allows loading from the local User.json packaged with the jar file
### Fixed
* Fix a problem with the last class of the day not displaying in "Next Class" mode for the Next Up feature
* Fix the day index check in SchoolWeek.getDay() to correctly be [0, 6] instead of [1, 6]
* Fix a minor issue with the Next Up display. If name, room, or both are missing and Next Up is enabled, the displayed format will be adjusted
### Changed

## [3.0.3] - 8/8/22
### Added
* Add in-line documentation
* Add miscellaneous precondition checks
### Fixed
### Changed
* Prefer the term "desktop" over "native" because "native" is a reserved keyword in java
  * Change package names, scripts, and manifests to follow this convention
* Week exceptions in the school json files can now be day tags within some week, although week tags are still preferred

## [3.0.2] - 8/7/22
### Added
* Added option to change font based on available fonts in the system
### Fixed
* Fixed minor issue with "Next Up" feature not properly displaying classes
* Windows icon file now works
### Changed
* SchoolPeriod getStartTime and getEndTime are now public, and their behavior (reason for DateTime arg) is documented
* Duration toString now only removes hours if they are 0. Thus the shortest format is "00:01" not "01"

## [3.0.1] - 8/6/22
### Added
### Fixed
* Resolved issue #18
* Resolved issue #19
### Changed
* Added CONTRIBUTING.md

## [3.0.0] - 8/6/22
### Added
* Text will now scale with increasing window size
### Fixed
### Changed
* Rewrote the Java codebase for Period-Countdown
* Improved code readability and quality
* Theme selection replaced with a JColorChooser to avoid confusion with hex numbers
* Class names, teachers, and rooms added to a single option
* Replaced school file chooser with a JComboBox that scans for valid json files
* Version number bundled with the package, leaving the operating system to display it

## [2.0.4] - 7/17/22
### Added
* Created new MVHS JSON file for 2022-2023 school year
### Fixed
### Changed
* Minor changes to .gitignore and general file cleanup

## [2.0.3.1] - 11/17/21
### Added
### Fixed
* PATCH FOR CRASH IN 2.0.3 IF YOU INSTALLED 2.0.3 UPDATE TO 2.0.3.1
### Changed

## [2.0.3] - 11/17/21
### Added
### Fixed
### Changed
* Updated JSON data for year, fixed finals schedule

## [2.0.2] - 10/9/21
### Added
### Fixed
### Changed
* Fixed minor issue in timer skipping -2 (free) periods

## [2.0.1] - 10/8/21
### Added
* Added error popup when the "Support > Update" routine fails
* Added SAT/PSAT testing week schedule
### Fixed
### Changed
* Updated in-file documentation

## [2.0.0] - 10/3/21
### Added
* Added "Support" menu item to menu bar
* Added update routine requested in issue #15
* Added Java JDK 17+ as a dependency
### Fixed
* Fixed bug described in issue #16
### Changed
* Updated MVHS_School.json with 10/4/21 rally week schedule

## [1.3.1] - 9/26/21
### Added
* Added the ability to mark periods as free using the keywords "Free", "None", or "N/A"
### Fixed
### Changed

## [1.3.0] - 9/26/21
### Added
* Added the "School..." option to allow changing school *.json file
* Added a better error handling routine for when json data is missing
### Fixed
### Changed
* Changed text displayed in "Period Name" and info popups to be the period name from the json data

## [1.2.0] - 9/25/21
### Added
### Fixed
* Fixed a possible security issue with Period-Countdown writing to files within itself
### Changed
* Moved all JSON data to ~/.periodcountdown/json/

## [1.1.1] - 9/25/21
### Added
### Fixed
### Changed
* Minor file restructure
* Changed method ofr Mac build to use jpackage over jpackager

## [1.1.0] - 9/25/21
### Added
* Created compile.sh and jar.sh to automate building a jarfile from the project
### Fixed
### Changed
* Minor code improvements
* Changed method in which SchoolDisplay finds the json data
* Finished build-mac.sh and Mac .app package creation

## [1.0.0] - 9/16/21
### Added
* First working version of Period-Countdown in Java
### Fixed
### Changed
