# Period-Countdown
A Java productivity application to display time remaining until a class based on a school schedule.


# Dependencies
Java (Standard Edition) 17 or above - https://www.oracle.com/java/technologies/downloads/ \
Google GSON 2.2.2 or above - Included automatically in builds of Period Countdown found in the "release" folder


# Preface Warning
Thank you for using Period-Countdown. Before you continue, please read and accept this warning:
1) There is no guarantee Period-Countdown will have an accurate schedule. Always check with teachers and school authority about official schedules. Do not blame Period-Countdown for tardies or absences!
2) Period-Countdown, upon first launch, will create a directory at "\~/Library/Application Support/PeriodCountdown" for Mac OS X, "\~/.PeriodCountdown" for Linux, or "HOME\\AppData\\PeriodCountdown" for Windows. This will store the file "User.json" which holds required user configuration.


# Installation (From Package)
## Mac OS X
1) Clone or download the repository from GitHub.
2) In Finder, go to the Period-Countdown/release folder.
3) Double-click on the "PeriodCountdown-x.x.x.dmg" file to launch the installer. A new window with "PeriodCountdown.app" should appear. Drag the .app file to the /Applications folder or another location.

## Linux
*Note: the Linux build of Period-Countdown currently only supports Debian-based distros. RPM-based distros are not supported at this time. You can try building from source for RPM, but it hasn't been tried.*
1) Clone or download the repository from GitHub.
2) In Files, go to the Period-Countdown/release folder.
3) Double-click on the "PeriodCountdown_x.x.x-1_amd64.deb" file to launch the Linux software manager. Click "Install". Alternatively, install through apt with "sudo apt install ./PeriodCountdown_x.x.x-1_amd64.deb".

The launch process is still not completely smooth but does work. To start the app, follow these steps:
1) If installed with the software manager, figure out where the app was installed by going to the software manager > "Installed" > "PeriodCountdown"
2) If installed with apt, the software is likely at ```/opt/PeriodCountdown/```
3) Run the executable with ```/path/to/periodcountdown/bin/PeriodCountdown```
   1) Ex: ```/opt/periodcountdown/bin/PeriodCountdown```

## Windows
1) Clone or download the repository from GitHub.
2) In File Explorer, go to the Period-Countdown/release folder.
3) Double-click on the "PeriodCountdown-x.x.x.exe" file to launch the installer.

In order to allow Period-Countdown to be found from the search menu, do the following:
1) Open File Explorer and go to "C:\\Program Files\\PeriodCountdown".
2) Right-click on the PeriodCountdown exactuable and select "Create Shortcut".
3) The shortcut will be created on your desktop. Move it to "C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs".


# Build (From Source)
## Build Files
### compile.sh
Compiles the java source code into the ```obj``` directory. \
Usage: ./compile.sh \[options\] \
       \[-t <target>\]    \(Builds for "native" or "web"\) \
	   \[-h\]             \(Displays a help message and exits\)

### jar.sh
Builds a jar file in ```bin``` from the compiled source in ```obj```. \
Usage: ./jar.sh \[options\] \
       \[-t <target>\]    \(Builds for "native" or "web"\) \
	   \[-h\]             \(Displays a help message and exits\)

### build.sh
Builds a release of the app in ```release``` from the jar in ```bin```. \
Usage: ./build.sh [options] \
       \[-t <target>\]    \(Builds for "mac", "linux", "windows", or "web"\) \
	   \[-h\]             \(Displays a help message and exits\)

## Mac OS X
1) Clone or download the repository from GitHub.
2) In Terminal, ```cd``` to the Period-Countdown folder.
3) Run ```./compile.sh -t native```.
4) Run ```./jar.sh -t native```.
5) Run ```java -jar bin/PeriodCountdown-native.jar``` to run the jar file, or
6) Run ```./build.sh -t mac```, then follow the installation instructions above.

## Linux
1) Clone or download the repository from GitHub.
2) In the terminal, ```cd``` to the Period-Countdown folder.
3) Run ```./compile.sh -t native```.
4) Run ```./jar.sh -t native```.
5) Run ```java -jar bin/PeriodCountdown-native.jar``` to run the jar file, or
6) Run ```./build.sh -t linux```, then follow the installation instructions above.

## Windows
*Note: ".sh" scripts will not work on Windows. There are some ways to get around this, but it is not advised to build from source on Windows.*
1) Install a bash environment.
   1) We recommend Git Bash, which can be opened by right-clicking on a window in File Explorer.
2) Clone or download the repository from GitHub.
3) In command prompt, ```cd``` to the Period-Countdown folder.
4) Run ```./compile.sh -t native```.
5) Run ```./jar.sh -t native```.
6) Run ```java -jar bin\PeriodCountdown-native.jar``` to run the jar file, or
7) Run ```./build.sh -t windows```, then follow the installation instructions above.


# Settings and Configuration
To change user options, click on the "Settings" menu at the top of the app. \
From the dropdown menu that opens, select the setting you wish to edit. Once a new option is chosen, click "OK". To cancel, click "Cancel". If the app does not update within a few seconds, try closing and reopening it.


# Possible Questions
* [I don't go to MVHS, how do I use my school's bell schedule?](#i-dont-go-to-mvhs-how-do-i-use-my-schools-bell-schedule)
* [How do I mark periods as free?](#how-do-i-mark-periods-as-free)
* [How do I uninstall Period-Countdown?](#how-do-i-uninstall-period-countdown)
* [How do I contribute?](#how-do-i-contribute)

## I don't go to MVHS, how do I use my school's bell schedule?
If you do not attend Mountain View High School or another supported institution, you can either:
1) Submit a [GitHub issue](https://github.com/JonathanUhler/Period-Countdown/issues/new) requesting the addition of a school, and that JSON data will be added as soon as possible, or
2) If you have some experience with JSON, you can try creating your own file (see [How do I contribute?](#how-do-i-contribute) for more details).

## How do I mark periods as free?
To skip free periods in Period-Countdown, open the app and go to Settings > Class Information. \
For your free periods, set the "Name" as either "None", "Free", or "N/A" (case insensitive) and they will be ignored.

## How do I uninstall Period-Countdown?
To completely remove Period-Countdown from your system:
1) Uninstall the application file.
   1) Mac OS X: delete the PeriodCountdown.app file, which was recommended to be installed at /Applications
   2) Linux: uninstall the .deb. Go to the software manager, find PeriodCountdown, and click "Uninstall"
   3) Windows: delete any shortcuts created, then uninstall the .exe
2) Remove the app data.
   1) Mac OS X: delete the directory at \~/Library/Application Support/PeriodCountdown
   2) Linux: delete the directory at \~/.PeriodCountdown
   3) Windows: delete the directory at HOME\\AppData\\PeriodCountdown

## How do I contribute?
Please read the CONTRIBUTING.md file for more information.
