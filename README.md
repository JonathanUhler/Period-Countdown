# Period-Countdown
A Java productivity application to display time remaining until a class based on a school schedule.


# Dependencies
Java (Standard Edition) 17 or above - https://www.oracle.com/java/technologies/downloads/ \
Google GSON 2.2.2 or above - Included automatically in builds of Period Countdown found in the
"release" folder


# Preface Warning
Thank you for using Period-Countdown. Before you continue, please read and accept this warning:
1) There is no guarantee Period-Countdown will have an accurate schedule. Always check with
   teachers and school authority about official schedules. Do not blame Period-Countdown for
   tardies or absences!
2) Period-Countdown, upon first launch, will create a directory at
   "\~/Library/Application Support/PeriodCountdown" for Mac OS X, "\~/.PeriodCountdown" for Linux,
   or "HOME\\AppData\\PeriodCountdown" for Windows. This will store the file "User.json" which
   holds required user configuration.


# Installation (From Package)
## Mac OS X
1) Clone or download the repository from GitHub.
2) In Finder, go to the Period-Countdown/release folder.
3) Double-click on the "PeriodCountdown-x.x.x.dmg" file to launch the installer. A new window with
   "PeriodCountdown.app" should appear. Drag the .app file to the /Applications folder or another
   location.

## Linux
*Note: the Linux build of Period-Countdown currently only supports Debian-based distros. RPM-based
 distros are not supported at this time. You can try building from source for RPM, but it hasn't
 been tried.*
1) Clone or download the repository from GitHub.
2) In Files, go to the Period-Countdown/release folder.
3) Double-click on the "PeriodCountdown_x.x.x-1_amd64.deb" file to launch the Linux software
   manager. Click "Install". Alternatively, install through apt with
   `sudo apt install ./PeriodCountdown_x.x.x-1_amd64.deb`.

The launch process is still not completely smooth but does work. To start the app use these steps:
1) If installed with the software manager, figure out where the app was installed by going to the
   software manager > "Installed" > "PeriodCountdown"
2) If installed with apt, the software is likely at `/opt/PeriodCountdown/`
3) Run the executable with `/path/to/periodcountdown/bin/PeriodCountdown`
   1) Ex: `/opt/periodcountdown/bin/PeriodCountdown`

## Windows
1) Clone or download the repository from GitHub.
2) In File Explorer, go to the Period-Countdown/release folder.
3) Double-click on the "PeriodCountdown-x.x.x.exe" file to launch the installer.

In order to allow Period-Countdown to be found from the search menu, do the following:
1) Open File Explorer and go to "C:\\Program Files\\PeriodCountdown".
2) Right-click on the PeriodCountdown exactuable and select "Create Shortcut".
3) The shortcut will be created on your desktop. Move it to
   "C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs".


# Build (From Source)
## Build Files
### compile.sh
Compiles the java source code into the `obj` directory. \
Usage: ./compile.sh \[options\] \
       \[-t <target>\]    \(Builds for "desktop" or "web"\) \
	   \[-h\]             \(Displays a help message and exits\)

### jar.sh
Builds a jar file in `bin` from the compiled source in `obj`. \
Usage: ./jar.sh \[options\] \
       \[-t <target>\]    \(Builds for "desktop" or "web"\) \
	   \[-h\]             \(Displays a help message and exits\)

### build.sh
Builds a release of the app in `release` from the jar in `bin`. \
Usage: ./build.sh [options] \
       \[-t <target>\]    \(Builds for "mac", "linux", "windows", or "web"\) \
	   \[-h\]             \(Displays a help message and exits\)

## Mac OS X
1) Clone or download the repository from GitHub.
2) In Terminal, `cd` to the Period-Countdown folder.
3) Run `./compile.sh -t desktop`.
4) Run `./jar.sh -t desktop`.
5) Run `java -jar bin/PeriodCountdown-desktop.jar` to run the jar file, or
6) Run `./build.sh -t mac`, then follow the installation instructions above.

## Linux
1) Clone or download the repository from GitHub.
2) In the terminal, `cd` to the Period-Countdown folder.
3) Run `./compile.sh -t desktop`.
4) Run `./jar.sh -t desktop`.
5) Run `java -jar bin/PeriodCountdown-desktop.jar` to run the jar file, or
6) Run `./build.sh -t linux`, then follow the installation instructions above.

## Windows
*Note: ".sh" scripts will not work on Windows. There are some ways to get around this, but it is
 not advised to build from source on Windows.*
1) Install a bash environment.
   1) We recommend Git Bash, which can be opened by right-clicking on a window in File Explorer.
2) Clone or download the repository from GitHub.
3) In command prompt, `cd` to the Period-Countdown folder.
4) Run `./compile.sh -t desktop`.
5) Run `./jar.sh -t desktop`.
6) Run `java -jar bin\PeriodCountdown-desktop.jar` to run the jar file, or
7) Run `./build.sh -t windows`, then follow the installation instructions above.


# Settings and Configuration
To change user options, click on the "Settings" menu at the top of the app. \
From the dropdown menu that opens, select the setting you wish to edit. Once a new option is
chosen, click "OK". To cancel, click "Cancel". If the app does not update within a few seconds,
try closing and reopening it.

## Class Information
Allows entry of user-specific class information. The data entered here is used to customize
information displayed on the app and is purely cosmetic. \
Teacher names and room numbers can be left blank to be ignored. \
See [How do I mark periods as free?](#how-do-i-mark-periods-as-free) for ignoring classes.

## School Information
Allows choosing a different school to base time calculations off of. \
Choose a file with your school name from the list of supported institutions. If your school does
not exist, see [here](#i-dont-go-to-mvhs-how-do-i-use-my-schools-bell-schedule).

## Next Up
A feature to display some or all of the remaining periods in the day (shows a future schedule in
addition to the main timer). \
Choose "Disabled" to disable this feature completely. \
Choose "Next Class" to display information for just the next class. \
Choose "All Classes" to display information for all classes remaining in the current day. \
The format of upcoming periods is "\<Name\> | \<Start\>-\<End\> [| \<Teacher\>, \<Room\>]". The
teacher/room information in "[ ]" is only displayed if it is not blank in Class Information.

## Theme
Allows a custom color for the app. \
To select a color, either choose from one of the preset values in the "Swatches" tab, or enter
your own value in HSV, HSL, RGB, or CMYK format.

## Font
Allows choosing a different font for the main timer and Next Up feature. \
This feature reads all the available fonts on your system, so choices may vary. New fonts, such as
OpenDyslexic to aid in reading, can be downloaded onto your system and used. This process depends
on your OS. \
*Note: some unusual fonts, or fonts that substitute with non-ASCII characters, may overlap or
 otherwise display incorrectly. Most common fonts should work fine.*


# Possible Questions
* [I don't go to MVHS, how do I use my school's bell schedule?](#i-dont-go-to-mvhs-how-do-i-use-my-schools-bell-schedule)
* [How do I mark periods as free?](#how-do-i-mark-periods-as-free)
* [How do I uninstall Period-Countdown?](#how-do-i-uninstall-period-countdown)
* [How do I contribute?](#how-do-i-contribute)

## I don't go to MVHS, how do I use my school's bell schedule?
If you do not attend Mountain View High School or another supported institution, you can either:
1) Submit an [issue](https://github.com/JonathanUhler/Period-Countdown/issues/new?assignees=&labels=school+data&template=school-request.md&title=%5BSchool+Request%5D+) requesting the addition of a
   school, and that JSON data will be added as soon as possible, or
2) If you have some experience with JSON, you can try creating your own file (see
   [How do I contribute?](#how-do-i-contribute) for more details).

## How do I mark periods as free?
To skip free periods in Period-Countdown, open the app and go to Settings > Class Information. \
For your free periods, set the "Name" as either "None", "Free", or "N/A" (case insensitive) and
they will be ignored.

## How do I uninstall Period-Countdown?
To completely remove Period-Countdown from your system:
1) Uninstall the application file.
   1) Mac OS X: delete the PeriodCountdown.app file, which was recommended to be installed at
      /Applications
   2) Linux: uninstall the .deb. Go to the software manager, find PeriodCountdown, and click
      "Uninstall"
   3) Windows: delete any shortcuts created, then uninstall the .exe
2) Remove the app data.
   1) Mac OS X: delete the directory at \~/Library/Application Support/PeriodCountdown
   2) Linux: delete the directory at \~/.PeriodCountdown
   3) Windows: delete the directory at HOME\\AppData\\PeriodCountdown

## How do I contribute?
Please read the CONTRIBUTING.md file for more information.
