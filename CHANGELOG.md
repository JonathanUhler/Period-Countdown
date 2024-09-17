# Period-Countdown Changelog
All notable changes to Period-Countdown should be documented in this file.

## Versioning Conventions
Versions are assigned with the `MAJOR.Minor.patch` syntax, where the components of the version
string generally refer to the following scopes of changes:
* `MAJOR`: A breaking change that guarantees a **lack** of support with previous versions.
  That is, version 2.0.0 does **not** guarantee support of all the features of version 1.0.0, and
  the reverse case also does **not** guarantee support.
* `Minor`: A non-breaking but significant change. Compatibility of this version with an older
  version is guaranteed, but the reverse may not be true. That is, version 1.1.0 must support all
  the features of version 1.0.0, but the reverse may or may not be true.
* `patch`: A non-breaking change without any major significance (e.g. fixing typos, updating
  comments or documentation, algorithm optimization). That is, version 1.0.1 must support all the
  features of version 1.0.0, and version 1.0.0 must support all the features of version 1.0.1.
  
## [8.0.1] - 2024-09-16
### Changed
* Fill in next school period and current user period data in GetCurrentPeriod command during summer
  
## [8.0.0] - 2024-09-15
### Changed
* Refactor of website code

## [7.0.1] - 2023-09-24
### Changed
* School data wizard now names day-long free periods based on the parent day

## [7.0.0] - 2023-08-21
### Added
* Add support for reading school JSON files from PeriodCountdown/schools/*.json
* Add the OSPath class to handle OS-specific file operations
### Fixed
* Caught a new error in constructing a SchoolYear class if the Days field is missing
### Changed
* Move the User.json file to PeriodCountdown/user/User.json
* Remove the ability to specify the Days field in the User.json file per school
* Increase verbosity of the User.json Settings/SchoolJson field (which now specifies a full path)
* Move all file operation related variables to OSPath.java
* Remove deprecated JSON school files (native support summary: removed 2, retained 0)
* Update documentation

## [6.2.0] - 2023-08-17
### Added
* Add a "Help" menu to the desktop application
* Add the school data creation wizard proposed by issue #25
### Fixed
* Fix documentation inconsistencies as described by issue #24
### Changed
* Update and rebuild in-file javadoc documentation
* Minor markdown formatting changes
* Update license year

## [6.1.0] - 2023-07-10
### Fixed
* Fix integer overflow bug with the Duration class
### Changed
* Improved documentation

## [6.0.3] - 2023-02-11
### Changed
* Font selection menu will now display names in that font

## [6.0.2] - 2023-02-02
### Added
* Minor debug and error message improvement

## [6.0.1] - 2023-01-08
### Changed
* Update JSON information for MVHS

## [6.0.0] - 2022-11-22
### Added
* Implemented support for google login
* Added a whitelist for the login feature
* Added a settings page and associated style/template files
### Changed
* Moved website configuration information to a single file and system (Conf.java and conf.py
  respectively)
* Miscellaneous minor security improvements
* Changed the constructor format for UserAPI. No arguments now indicates loading from jar, and the
  path argument must be absolute if used
* Removed the deprecated LoginUser command
* Minor style and template changes

## [5.1.3] - 2022-11-10
### Fixed
* Fixed a DST issue
### Changed
* The previousEndTime variable is now persisted throughout the entire creation of a SchoolYear,
  rather than just inside of each day definition. This will help catch discontinuities across days
  or weeks

## [5.1.2] - 2022-10-17
### Fixed
* Change "Brunch" for G schedule from "Nothing" to "Special"

## [5.1.1] - 2022-10-17
### Changed
* Enable "Next Up" by default (allow viewing on the generic website)

## [5.1.0] - 2022-10-16
### Changed
* Moved log and pid files to command line options instead of hard-coded paths

## [5.0.0] - 2022-10-15
### Added
* Minor documentation changes and additions
### Changed
* Change the structure of the User.json file to include a "Schools" table that can contain period
  info for many schools

## [4.0.2] - 2022-10-13
### Changed
* Web build now comes as a tar.gz archive

## [4.0.1] - 2022-10-13
### Added
* Add a "ExpireTime" field to GetTimeRemaining, indicating the end of the current period (which may
  be the same as "EndTime")
### Fixed
* Fix an issue with merging periods to calculate time remaining on the web build
### Changed
* Minor syntax changes to GetTimeRemaining

## [4.0.0] - 2022-10-02
### Added
* Add a .wsgi file to load the web server from
* Add a logs folder and blank log files to the web build
* Add the apache configuration file period-countdown.conf to the web build
* Add a favicon
* Transport will now write to a pid file
* Add upcoming special schedules
### Changed
* Change the abilities of the gen_keys.sh script (now only generates for the transport)
* Remove the "meta refresh" test line from index.html -- this was purly for testing and a better
  solution will be implemented
* Move server configuration files to src/assets/web
* Add a required timezone code tag to the school (or user) json files
* All internal operations are now done in UTC, before being converted to the client's timezone as
  needed
* Logging operations simplified slightly, log files for transport and server added

## [3.1.0] - 2022-09-04
### Added
* Add general framework for web-server
  * Add specification for web communcation to documents/
  * Create a separate "main" package for the web build (web.transport instead of desktop) and a
    Java transport system
  * Create a Python web server to display generic data (from default User.json and MVHS_School.json)
    for now
    * Future releases will likely include database functionality, allowing the same features as
	  the native app
* Add a constructor flag to UserAPI that allows loading from the local User.json packaged with the
  jar file
### Fixed
* Fix a problem with the last class of the day not displaying in "Next Class" mode for the Next Up
  feature
* Fix the day index check in SchoolWeek.getDay() to correctly be [0, 6] instead of [1, 6]
* Fix a minor issue with the Next Up display. If name, room, or both are missing and Next Up is
  enabled, the displayed format will be adjusted

## [3.0.3] - 2022-08-08
### Added
* Add in-line documentation
* Add miscellaneous precondition checks
### Changed
* Prefer the term "desktop" over "native" because "native" is a reserved keyword in java
  * Change package names, scripts, and manifests to follow this convention
* Week exceptions in the school json files can now be day tags within some week, although week
  tags are still preferred

## [3.0.2] - 2022-08-07
### Added
* Added option to change font based on available fonts in the system
### Fixed
* Fixed minor issue with "Next Up" feature not properly displaying classes
* Windows icon file now works
### Changed
* SchoolPeriod getStartTime and getEndTime are now public, and their behavior (reason for DateTime
  arg) is documented
* Duration toString now only removes hours if they are 0. Thus the shortest format is "00:01" not
  "01"

## [3.0.1] - 2022-08-06
### Fixed
* Resolved issue #18
* Resolved issue #19
### Changed
* Added CONTRIBUTING.md

## [3.0.0] - 2022-08-06
### Added
* Text will now scale with increasing window size
### Changed
* Rewrote the Java codebase for Period-Countdown
* Improved code readability and quality
* Theme selection replaced with a JColorChooser to avoid confusion with hex numbers
* Class names, teachers, and rooms added to a single option
* Replaced school file chooser with a JComboBox that scans for valid json files
* Version number bundled with the package, leaving the operating system to display it

## [2.0.4] - 2022-07-17
### Added
* Created new MVHS JSON file for 2022-2023 school year
### Changed
* Minor changes to .gitignore and general file cleanup

## [2.0.3.1] - 2021-11-17
### Fixed
* PATCH FOR CRASH IN 2.0.3 IF YOU INSTALLED 2.0.3 UPDATE TO 2.0.3.1

## [2.0.3] - 2021-11-17
### Changed
* Updated JSON data for year, fixed finals schedule

## [2.0.2] - 2021-10-09
### Changed
* Fixed minor issue in timer skipping -2 (free) periods

## [2.0.1] - 2021-10-08
### Added
* Added error popup when the "Support > Update" routine fails
* Added SAT/PSAT testing week schedule
### Changed
* Updated in-file documentation

## [2.0.0] - 2021-10-03
### Added
* Added "Support" menu item to menu bar
* Added update routine requested in issue #15
* Added Java JDK 17+ as a dependency
### Fixed
* Fixed bug described in issue #16
### Changed
* Updated MVHS_School.json with 10/4/21 rally week schedule

## [1.3.1] - 2021-09-26
### Added
* Added the ability to mark periods as free using the keywords "Free", "None", or "N/A"

## [1.3.0] - 2021-09-26
### Added
* Added the "School..." option to allow changing school *.json file
* Added a better error handling routine for when json data is missing
### Changed
* Changed text displayed in "Period Name" and info popups to be the period name from the json data

## [1.2.0] - 2021-09-25
### Fixed
* Fixed a possible security issue with Period-Countdown writing to files within itself
### Changed
* Moved all JSON data to ~/.periodcountdown/json/

## [1.1.1] - 2021-09-25
### Changed
* Minor file restructure
* Changed method ofr Mac build to use jpackage over jpackager

## [1.1.0] - 2021-09-25
### Added
* Created compile.sh and jar.sh to automate building a jarfile from the project
### Changed
* Minor code improvements
* Changed method in which SchoolDisplay finds the json data
* Finished build-mac.sh and Mac .app package creation

## [1.0.0] - 2021-09-16
### Added
* First working version of Period-Countdown in Java
