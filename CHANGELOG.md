# Period-Countdown Changelog
History of revisions for all files associated with Period-Countdown.


# display.js
 Revision History

  version    date                     Change
  ------- ----------  --------------------------------------------------------
  1.0.0   10/04/2020  First usable release of display.js

  1.1.0   10/05/2020  Changes in this version:
                          -Add new code from Calendar.js ("toString", removal
                          of CalendarHHMMSSAsString)
                          -Add "nextMatch" to display the time until the next
                          period if the current period is a pseudo-period (end-
                          of-day or start-of-day)
                              -Change "timeLeft" by one of two values depending
                              on the type of period
 
 1.2.0    10/10/2020  Changes in this version:
                          -timeLeft.toString replaced with timeLeft.toString()
                          that calls a function in Calendar.js
                          -Days do not display anymore and are converted to hours
                          in Calendar.js

 1.2.1    10/10/2020  Changes in this version:
                          -Documentation conventions updated to match Calendar.js
                          (no functional changes)
                          -Additional documentation changes

 1.3.0    10/10/2020  Changes in this version:
                          -Added function printText to handle displaying text
                          -Removed individual print statements (context.fillText())
                          from refreshPeriod and refreshRemainingTime with a call of
                          printText
                          -Renamed the textPos object to textDef (text definition) for
                          clarity
                          -Removed the x and y offset variables from textDef in favor of
                          a single x-position variable (textDef.textX) and independant
                          y-position variables (textDef.nameY for "period name" text and
                          textDef.timeY for "time remaining" text)

 1.3.1    10/10/2020  Changes in this version:
                          -Minor documentation changes
                          -Added the "color" argument to printText to allow for changable
                          text-color

 1.4.0    10/11/2020  Changes in this version:
                          -Added function "DisplayMessage" to replace console.log commands
                          -Removed all console.log calls with calls to DisplayMessage
                          -Minor documentation changes and code cleanup

 2.0.0    10/21/2020  Changes in this version:
                          -Fixed major compatibility issues with calendarTest.js
                          -Added in a declaration for calendar


# settings.js
 Revision History

  version    date                     Change
  ------- ----------  --------------------------------------------------------
  1.0.0   1/24/2021   -First working version of settings.js


# Calendar.js
 Revision History

  version    date                     Change
  ------- ----------  --------------------------------------------------------
  1.0.0   10/04/2020  First usable release of Calendar.js

  1.1.0   10/05/2020  Changes is this version:
                       - Add the "toString" key to the return of Calendar.getTimeRemainingInPeriod
                         that is a string of the remaining time in [h]h:mm:ss format.
                         This saves a call to the CalendarHHMMSSAsString function
                         by the caller.
                       - Finish implementing the Calendar.getNextPeriod method

  1.2.0   10/05/2020  Changes in this version:
                       - Add the Calendar.getTimeRemainingUntilPeriod method,
                         returning the same information as getTimeRemainingInPeriod,
                         but to the start of a specified period.
                       - As a byproduct of this addition, add the dDelta key to
                         the timeLeft object to cover the possibility that the
                         time left may be measured not just in hours, but days.

  1.3.0   10/06/2020  Changes in this version:
                       - Put all of the school-related information into it's
                         own class in preparation for moving it all into a
                         separate file. This will leave Calendar.js as the
                         vehicle that creates the calendar data structures for
                         the school year and provides public class variables and
                         methods through which the data structures are manipulated.
                         Because of the separation, a new school year, or information
                         for a new student can be created without having to
                         edit Calendar.js.

  2.0.0   10/08/2020  Changes is this version:
                       - Remove all of the school-related information from this
                         file and put it into MVHS.js

  2.0.1   10/09/2020  Changes in this verion:
                       - Fix up some comments and documentation

  3.0.0   10/09/2020  Changes in this version:
                       - Fix up the DST problems by going from a millisecond
                         based comparison of periods, to one that uses
                         the .setHours call to a real date object, and then
                         comparing the .getTime() values for the date object.
                         This requires a corresponding change to MVHS.js.
                       - Add a field in every class to specify the type of class.
                         This helps with checking that an argument to a method
                         is of the right type
                       - Add improved CalendarAssert calls in methods to
                         ensure that method arguments are of the correct type.
                         This is the manual method of working around the fact
                         that Javascript doesn't do type or argument checking.
                       - Convert the .toString property in timeLeft from a
                         variable that is only updated by calculateTimeLeft to
                         function that will generate the pretty-printed string
                         when called. This enables correct values if the
                         timeLeft *Delta variables are updated manually
                       - Add an optional boolean argument as the last in the
                         calls to calculateTimeLeft and getTimeRemainingUntilPeriod
                         methods to add (.dDelta*24) to .hDelta and to set
                         .dDelta to zero. This converts days to hours for those
                         cases where that format is desired.

  3.0.1   10/10/2020   Changes in this version:
                        - Remove getMsSinceMidnight method because the v3.3.0
                          eliminated the need
                        - Minor commentary and typo fixing that doesn't change
                          the function

  3.0.2   10/11/2020   Bug fix: The getTimeRemainingUntilPeriod method was
                       returning the time remaining until the end of the
                       identified period, not the start.

  3.0.3   10/21/2020   Move all of the self-test code out of this file into
                       it's own file.


# MVHS.js
 Revision History

  version    date                     Change
  ------- ----------  --------------------------------------------------------
  1.0.0   10/08/2020  First usable release of MVHS.js

  1.0.1   10/08/2020  Add a module exports statement for runs using node.js
                      (not needed in the browser because the html file loads
                      MVHS.js before Calendar.js)

  1.0.2   10/09/2020  Fix up some comments and documentation

  2.0.0   10/09/2020  Change the _periodDayTypeHash structure for each day
                      type such that the adjustment field becomes a boolean
                      that indicates whether a period is the last one of the
                      day and let Calendar.js decide what to do with that
                      information. This change requires a corresponding change
                      to Calendar.js

  2.0.1   10/10/2020  Minor documentation change

  2.1.0   12/14/2020  Add the exception for finals week starting 12/13/2020

  2.1.1   01/03/2021  Fix the weeks of 1/3/2021 and 1/17/2021. They should both
                      be HABAB weeks

  2.1.2   01/21/2021  Fix the week of 1/17/2021 again. This is an advisory week
                      and Thurs/Fri of that week have modified schedules.
