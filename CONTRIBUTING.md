# Period-Countdown Contributing Guidelines
Contributions to Period-Countdown are highly appreciated. \
The following are a set of general guidelines and important information about contributing to the
project. Generally, when making contributions, use your best judgement on what should be changed
and how. \
We appreciate you taking the time to contribute. \
\
**Table of Contents** \
[How You Can Contribute](#how-you-can-contribute)
* [Reporting Bugs and Issues](#reporting-bugs-and-issues)
* [Suggesting Features](#suggesting-features)
* [Pull Requests](#pull-requests)


# How You Can Contribute

## Reporting Bugs and Issues
When submitting bug and issue reports, please be as descriptive as possible. Include as much
information as you can following the [bug report template](https://github.com/JonathanUhler/Period-Countdown/issues/new?assignees=&labels=bug&template=bug-report.md&title=%5BBug%5D+). \
Try to include:
* A clear title
* A detailed description of the bug (observed behavior) and what you expect the behavior should
  have been
* A list of steps to reproduce the issue
* The version of Period-Countdown you are using and the system you are running on (e.g. "3.0.1 on 
  Mac OS 10.3 with Java 17")
* Any screenshots or further context to help describe the issue

## Suggesting Features
To suggest new features, please create a
[blank issue](https://github.com/JonathanUhler/Period-Countdown/issues/new) describing the feature
you want added. \
Try to include:
* A clear title
* A detailed description of the feature you want added and its behaviors
* Any resources that might be useful in implementing the feature, such as a link to a school's
  calendar for adding new school data

## Pull Requests
### School Data
Create your new school JSON file at `Period-Countdown/src/assets/json`. JSON files can be created
semi-automatically by using the data generator build in to the desktop application (accessible
through Help > School Data Wizard). Alternatively, JSON files can be written by hand with some
basic knowledge of the formatting. \
\
Below is a full example of a school JSON file with descriptions of its components:
```json
{
    "Info": {
        "FirstPeriod": "1",
        "LastPeriod": "7",
        "FirstDayTag": "2022-08-10",
        "LastDayTag": "2023-06-08",
        "Timezone": "America/Los_Angeles"
    },
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
    "Exceptions": [
        {"WeekTag": "2022-11-20", "Type": "Break"}
    ]
}
```

#### Info
Required fields:
* `FirstPeriod`: the first valid period/class number, inclusive, must be an integer >= 0
* `LastPeriod`: the last valid period/class number, inclusive, must be an integer >= `FirstPeriod`
* `FirstDayTag`: the first date of school in yyyy-MM-dd format, inclusive
* `LastDayTag`: the last date of school in yyyy-MM-dd format, inclusive
* `Timezone`: the unix timezone identifier which the json was created for. A good list can be found
  at: https://manpages.ubuntu.com/manpages/focal/man3/DateTime::TimeZone::Catalog.3pm.html

#### Days
Any key can be included in the Days section, as defined by the programmer. Each value in the Days
section must be a list with map-like elements that have the following keys:
* `Type`: the type of the event or class. Valid values are "Nothing" (for an event that should be
  skipped and merged with adjacent "Nothing"s when calculating time remaining), "Special (for an
  event without a class that should NOT be merged), and an integer "N" where
  FirstPeriod <= N <= LastPeriod
* `Name`: a programmer-defined name for the event or class, can be anything
* `Start`: the start time of the event or class in HH:mm format (24-hour time)
* `End`: the end time of the event or class in HH:mm format, such that End > Start

Note that the first event of a day definition must have the `Start` value of `"00:00"` and the
last event must have the `End` value of `"23:59"`. Additionally, the end time of any period p[n]
should be the same value as the start time of the next period p[n+1] (with the obvious exception
of the last period in the day).

#### Weeks
Required keys:
* `DEFAULT`: the default week to use
* Additional week types can be defined as required by the programmer

Each entry in the Weeks section should be a list of exactly seven strings, where each string is
the exact, case-sensitive name of a day defined in the Days section.

#### Exceptions
This is the section to define any use of a non-DEFAULT week in the yearly calendar. Each
entry is a map with the required keys:
* `WeekTag`: a date in yyyy-MM-dd format that occurs sometime in the week in which the specified
  week type should be substituted for DEFAULT. This application considers sunday to be the first
  day of the week and saturday to be the last day. Older versions required this field to be
  the sunday of the week during which the exception occured, but the latest releases will be able
  to convert any day during a week to the closest sunday (rounded back in time) to find the
  correct position for the exception
* `Type`: the type of week to substitute for DEFAULT during the specified week tag. This must be
  the case-sensitive name of one of the weeks defined in the Weeks seciton

### Updaing Documentation
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