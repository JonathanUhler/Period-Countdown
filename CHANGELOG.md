# Period-Countdown Changelog
**NOTE TO CONTRIBUTORS:** When creating a new version of this software, make sure to update the VERSION file at ```Period-Countdown/src/assets/VERSION```. Please see CONTRIBUTING.md for more information and style guidelines. \
\
All notable changes to Period-Countdown should be documented in this file.

## [M.m.p] - M/D/YY
### Added
### Fixed
### Changed

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
