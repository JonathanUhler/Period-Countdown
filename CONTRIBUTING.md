# Period-Countdown Contributing Guidelines
Contributions to Period-Countdown are highly appreciated. \
The following are a set of general guidelines and important information about contributing to the project. Generally, when making contributions, use your best judgement on what should be changed and how. \
We appreciate you taking the time to contribute. \
\
**Table of Contents** \
[How You Can Contribute](#how-you-can-contribute)
* [Reporting Bugs and Issues](#reporting-bugs-and-issues)
* [Suggesting Features](#suggesting-features)
* [Pull Requests](#pull-requests)


## How You Can Contribute

### Reporting Bugs and Issues
When submitting bug and issue reports, please be as descriptive as possible. Include as much information as you can following the [bug report template](https://github.com/JonathanUhler/Period-Countdown/issues/new?assignees=&labels=bug&template=bug-report.md&title=%5BBug%5D+). \
Try to include:
* A clear title
* A detailed description of the bug (observed behavior) and what you expect the behavior should have been
* A list of steps to reproduce the issue
* The version of Period-Countdown you are using and the system you are running on (e.g. "3.0.1 on Mac OS 10.3 with Java 17")
* Any screenshots or further context to help describe the issue

### Suggesting Features
To suggest new features, please create a [blank issue](https://github.com/JonathanUhler/Period-Countdown/issues/new) describing the feature you want added. \
Try to include:
* A clear title
* A detailed description of the feature you want added and its behaviors
* Any resources that might be useful in implementing the feature, such as a link to a school's calendar for adding new school data

### Pull Requests
#### School Data
Create your new school JSON file at ```Period-Countdown/src/assets/json```. Here is an example with with descriptions (note that JSON does not actually support comments):
```json
{
    /*
    Info
    ----
    Required keys:
     - FirstPeriod: the first valid period/class, as an integer >= 0
     - LastPeriod: the last valid period/class, as an integer >= FirstPeriod
     - FirstDayTag: the day tag in format YYYY-MM-DD of the first day of school
     - LastDayTag: the day tag in format YYYY-MM-DD of the last day of school
     - Timezone: the unix timezone ID for which this json data was created. A good list can be found
       at: https://manpages.ubuntu.com/manpages/focal/man3/DateTime::TimeZone::Catalog.3pm.html
    */
    "Info": {
        "FirstPeriod": "1",
        "LastPeriod": "7",
        "FirstDayTag": "2022-08-10",
        "LastDayTag": "2023-06-08",
        "Timezone": "America/Los_Angeles"
    },
    /*
    Days
    ----
    Required keys:
    - None, the keys are chosen by the programmer. These are the names of day type
      (e.g. "C Schedule", "Holiday", etc.)

    Hashmap definitions within a day definition
    ----
    Required keys:
    - Type: the type of the event/class. Valid values are "Nothing" for an event that should be
      skipped and merged with adjacent "Nothing"s when calculating time remaining, "Special" for
      an event without a class that should NOT be merged, and an integer "N" where
      FirstPeriod <= N <= LastPeriod
	- Name: the programmer-defined name for the event, can be anything
	- Start: the start time tag in format HH:MM in 24-hour time of the event
	- End: the end time tag in format HH:MM in 24-hour time of the event

    Notes:
    - The first event of a day definition should always have the "Start" value of "00:00"
    - The last event of a day definition should always have the "End" value of "23:59"
    - Any event that is not the first should have the "Start" value equal to the previous
      event's "End" value
    */
    "Days": {
    	"SchoolDay": [
    		{"Type": "Nothing", "Name": "Before School", "Start": "00:00", "End": "08:40"},
    		{"Type": "1", "Name": "Class 1", "Start": "08:40", "End": "10:00"},
    		{"Type": "Special", "Name": "Lunch", "Start": "10:00", "End": "12:00"},
    		{"Type": "2", "Name": "Class 2", "Start": "12:00", "End": "15:00"},
    		{"Type": "Nothing", "Name": "After School", "Start": "15:00", "End": "23:59"}
    	],
    	"Weekend": [
            {"Type": "Nothing", "Name": "Weekend", "Start": "00:00", "End": "23:59"}
        ]
    },
    /*
    Weeks
    -----
    Required keys:
    - DEFAULT: the default week to use, built from the day types defined in Days
	- Additional keys can also be added with special week definitions

    Notes:
	- Each of the arrays in this map should contain exactly 7 strings with values equal to
      (case SENSITIVE) one of the day definition names defined above
	*/
    "Weeks":  {
        "DEFAULT": [
			"Weekend",
			"SchoolDay",
			"SchoolDay",
			"SchoolDay",
			"SchoolDay",
			"SchoolDay",
			"Weekend"
        ],
		"Break": [
			"Weekend",
			"Weekend",
			"Weekend",
			"Weekend",
			"Weekend",
			"Weekend",
			"Weekend"
		]
    },
	/*
	Exceptions: Hashmap definitions with the Exceptions list
	-----
	Required keys:
	- WeekTag: the week tag in format YYYY-MM-DD of the SUNDAY of the week in question
	- Type: the type of week to substitute for DEFAULT in the given week, must be on the names
	  define in the Weeks map above
	*/
    "Exceptions": [
        {"WeekTag": "2022-11-20", "Type": "Break"}
    ]
}
```

#### Updaing Documentation
Once you have made changes to Period-Countdown, please remember to update any relavent
documentation. This includes:
* Update the CHANGELOG.md file
  * Refer to the following template (unused fields can be removed):
```
## [M.m.p] - M/D/YY
### Added
### Fixed
### Changed
```
* Update the version number. The format is MAJOR.Minor.patch. Use your best judgement when
  deciding on a new version number, although your version should generally align with:
  * Major versions often break compatability with older versions because of extreme differences
  * Minor versions often keep compatability with older versions, although introduce new features
    and changes
  * Patch versions keep compatability with older versions because of the limited/smaller changes
	they introduce
* Update the VERSION file under `Period-Countdown/src/assets/VERSION` with the version number
  from CHANGELOG.md
* Run the project locally with
  `./compile.sh && ./jar.sh && java -jar bin/PeriodCountdown-dekstop.jar`