# Period-Countdown
A light-weight Java application to display time remaining in school periods or classes based on a school schedule.

# Preface Warning
Thank you for using Period-Countdown. Before you continue, please read and accept this preface warning:
1) There is no guarantee Period-Countdown will have an accurate schedule. Always check with teachers and schools about changes in schedules. Do not blame Period-Countdown or its developers for tardies or absences!
2) While the Period-Countdown developers try to make the app as foolproof as possible, there is no guarantee that all the code will work perfectly. If you encounter issues or have a feature request, please see the Github page online at https://github.com/JonathanUhler/Period-Countdown/issues

# Installation
The installation process will be streamlined in the future with easy-to-use installer packages for MacOS, Linux, and Windows.\
Currently, the app likely only works on Unix (although with some basic java knowledge, could be compiled on Windows). To install Period-Countdown, download the Github repository or clone it directly onto your machine. If downloaded, unzip the compressed file.\
Open your machine's terminal/command line tool and use the change directory command (```cd``` on Unix) to go to the Period-Countdown directory.\
Use ```ls``` to confirm that the ```compile.sh``` script is in your working directory. If it is type ```./compile.sh```. The Period-Countdown app should launch.

# Usage
Given the two files School.json and User.json located under Period-Countdown/json/, the app should start counting down once started.\
At the top of the screen will be the period status -- a message comprised of the period name and freedom status (ex: "Chemistry | Period 1" or "Lunch | Free")\
Below that is the timer, which displays the time left in the period mentioned in the period status.\
Finally, if enabled, is the "next up" display. This shows the next period(s) and has five levels of verbosity: disabled, next period, next period with all info, all periods, and all periods with all info.

# Settings and Configuration
To change class names, teacher names, room numbers, and the next up display's verbosity, click on the "Settings" button at the very top of the app.\
From the dropdown menu that opens, select the setting you want to edit. For everything but the next up option (which is in the same "Settings" menu) a second window will open for you to enter information into. Click "OK" or "Save" to save information.
