# Period-Countdown
A light-weight Java application to display time remaining in school periods or classes based on a school schedule.


# Preface Warning
Thank you for using Period-Countdown. Before you continue, please read and accept this preface warning:
1) There is no guarantee Period-Countdown will have an accurate schedule. Always check with teachers and schools about changes in schedules. Do not blame Period-Countdown or its developers for tardies or absences!
2) Period-Countdown, upon first launch, will create a directory at ~/.periodcountdown in which it will store two JSON files for the school and user data. These files are needed for Period-Countdown to function
3) While the Period-Countdown developers try to make the app as foolproof as possible, there is no guarantee that all the code will work perfectly. If you encounter issues or have a feature request, please see the Github page online at https://github.com/JonathanUhler/Period-Countdown/issues


# Installation
*Note: The installation process will be expanded in the future, but currently only supports Unit-based operating systems (Mac and Linux) (see issue #13 for more information, or consider contributing if you have knowledge on building for Windows and Linux!)*\

## MacOS
### MacOS Installation
Clone or download the repository from Github.\
In Finder or Terminal go to the Period-Countdown/release folder.\
Double-click on the ```PeriodCountdown-1.0.dmg``` to launch the installer. The ```PeriodCountdown.app``` application should appear in a new window, drag this to the Applications folder or another location.

### MacOS Build Process
To build or update the app, follow the process below:
1) Open Terminal and ```cd``` to the Period-Countdown/ directory
2) Compile with ```./compile.sh```
3) Make the jarfile with ```./jar.sh```
4) Build the app bundle with ```./build-mac.sh```
5) Follow the instructions under MacOS Installation to open and use the app

## Linux Installation
Clone or download the repository from Github.\
In Files or Terminal go to the Period-Countdown/release folder.\

* In Files, double-click on the ```periodcountdown_1.0-1_amd64.deb``` to launch the Linux software manager. Click "Install"
* In Terminal, type ```sudo apt install ./periodcountdown_1.0-1_amd64.deb``` to install directly using apt

The launch process is still not completely smooth but does work. To start the app, follow these steps:
1) If installed with the software manager, figure out where the app was installed by going to the software manager > "Installed" > "periodcountdown"
2) If installed with apt, the software is likely at ```/opt/periodcountdown/```
3) Run the executable with ```/path/to/periodcountdown/bin/PeriodCountdown```
   1) Ex: ```/opt/periodcountdown/bin/PeriodCountdown```

### Linux Build Process
To build or update the app, follow the process below:
1) Open Terminal and ```cd``` to the Period-Countdown/ directory
2) Compile with ```./compile.sh```
3) Make the jarfile with ```./jar.sh```
4) Build the app bundle with ```./build-linux.sh```
5) Follow the instructions under Linux Installation to open and use the app

## Windows
*Note: The installation process will be expanded in the future, but currently only supports Unit-based operating systems (Mac and Linux) (see issue #13 for more information, or consider contributing if you have knowledge on building for Windows and Linux!)*\
\
*Until an official solution is developed, you can still use the app with ```java -jar PeriodCountdown.jar```*


# Usage
Given the two files School.json and User.json located under Period-Countdown/json/, the app should start counting down once started.\
At the top of the screen will be the period status -- a message comprised of the period name and freedom status (ex: "Chemistry | Period 1" or "Lunch | Free")\
Below that is the timer, which displays the time left in the period mentioned in the period status.\
Finally, if enabled, is the "next up" display. This shows the next period(s) and has five levels of verbosity: disabled, next period, next period with all info, all periods, and all periods with all info.


# Settings and Configuration
To change class names, teacher names, room numbers, and the next up display's verbosity, click on the "Settings" button at the very top of the app.\
From the dropdown menu that opens, select the setting you want to edit. For everything but the next up option (which is in the same "Settings" menu) a second window will open for you to enter information into. Click "OK" or "Save" to save information.


# Possible Questions
* [I don't use Mac, how do I use PeriodCountdown?](#I-don't-use-Mac,-how-do-I-use-PeriodCountdown?)
* [If I don't go to MVHS how do I enter my school's bell schedule?](#If-I-don't-go-to-MVHS-how-do-I-enter-my-school's-bell-schedule?)

## If I don't go to MVHS how do I enter my school's bell schedule?
If you do not attend Mountain View High School, you can use the gendata.py python script located at Period-Countdown/python/gendata.py\
To run this, ```cd``` to that directory through Terminal and type ```./gendata.py```.\
Follow the instructions and enter information when prompted.\
\
Alternatively, you can create an issue on Github requesting the addition of a school and that JSON data will be added as soon as possible