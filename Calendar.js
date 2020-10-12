
// Calendar.js
//
// Calendar-related functions for the MVHS schedule app
//
"use strict";

// Constants that enable test code in the browser (it's always enabled in node.js)
// and running the example code, which is a pretty good visual test of the operation
// of the use of the code. These are defined here so that they are easy to find
const _enableTestCodeInBrowser = false; // True to enable test code in the browser
const _enableExampleCode = false;        // True to enable the example code.

const CalendarVersion = "3.0.1";

// Revision History
//
//  version    date                     Change
//  ------- ----------  --------------------------------------------------------
//  1.0.0   10/04/2020  First usable release of Calendar.js
//
//  1.1.0   10/05/2020  Changes is this version:
//                       - Add the "toString" key to the return of Calendar.getTimeRemainingInPeriod
//                         that is a string of the remaining time in [h]h:mm:ss format.
//                         This saves a call to the CalendarHHMMSSAsString function
//                         by the caller.
//                       - Finish implementing the Calendar.getNextPeriod method
//
//  1.2.0   10/05/2020  Changes in this version:
//                       - Add the Calendar.getTimeRemainingUntilPeriod method,
//                         returning the same information as getTimeRemainingInPeriod,
//                         but to the start of a specified period.
//                       - As a byproduct of this addition, add the dDelta key to
//                         the timeLeft object to cover the possibility that the
//                         time left may be measured not just in hours, but days.
//
//  1.3.0   10/06/2020  Changes in this version:
//                       - Put all of the school-related information into it's
//                         own class in preparation for moving it all into a
//                         separate file. This will leave Calendar.js as the
//                         vehicle that creates the calendar data structures for
//                         the school year and provides public class variables and
//                         methods through which the data structures are manipulated.
//                         Because of the separation, a new school year, or information
//                         for a new student can be created without having to
//                         edit Calendar.js.
//
//  2.0.0   10/08/2020  Changes is this version:
//                       - Remove all of the school-related information from this
//                         file and put it into MVHS.js
//
//  2.0.1   10/09/2020  Changes in this verion:
//                       - Fix up some comments and documentation
//
//  3.0.0   10/09/2020  Changes in this version:
//                       - Fix up the DST problems by going from a millisecond
//                         based comparison of periods, to one that uses
//                         the .setHours call to a real date object, and then
//                         comparing the .getTime() values for the date object.
//                         This requires a corresponding change to MVHS.js.
//                       - Add a field in every class to specify the type of class.
//                         This helps with checking that an argument to a method
//                         is of the right type
//                       - Add improved CalendarAssert calls in methods to
//                         ensure that method arguments are of the correct type.
//                         This is the manual method of working around the fact
//                         that Javascript doesn't do type or argument checking.
//                       - Convert the .toString property in timeLeft from a
//                         variable that is only updated by calculateTimeLeft to
//                         function that will generate the pretty-printed string
//                         when called. This enables correct values if the
//                         timeLeft *Delta variables are updated manually
//                       - Add an optional boolean argument as the last in the
//                         calls to calculateTimeLeft and getTimeRemainingUntilPeriod
//                         methods to add (.dDelta*24) to .hDelta and to set
//                         .dDelta to zero. This converts days to hours for those
//                         cases where that format is desired.
//
//  3.0.1   10/10/2020   Changes in this version:
//                        - Remove getMsSinceMidnight method because the v3.3.0
//                          eliminated the need
//                        - Minor commentary and typo fixing that doesn't change
//                          the function
//
//  3.0.2   10/11/2020   Bug fix: The getTimeRemainingUntilPeriod method was
//                       returning the time remaining until the end of the
//                       identified period, not the start.

// TODO List
//  1. In the self-test code, find some way to verify getNextPeriod, perhaps by
//     doing something similar to the test for getPeriodByDateAndTime.

// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Copyright 2020 Mike Uhler and Jonathan Uhler
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Conventions, used throughout
//
// Terms
//   A "day tag" is a string representing the date of a particular day, in
//   the form "yyyy-mm-dd". Both month and day are left-padded with a zero
//   to two digits.
//
//   The "day tag array" is an array containing all day tags, in date order, for
//   the entire school year.
//
//   The "day object hash" is an associative array whose index is the day tag
//   and whose value is the CalendarDayObject object for that day. There is one
//   day object hash entry for each element in the day tag array
//
//   The "day index" is the offset in the day tag array of a particular day tag,
//   starting at 0
//
//   A "week tag" is a day tag string representing the Sunday of the start of
//   a week.
//
//   The "week tag array" is an array containing all week tags, in date order,
//   for the entire school year.
//
//   The "week object hash" is an associative array whose index is the week tag
//   and whose value is the CalendarWeekObject object for that week. There is one
//   week object hash entry for each element in the week tag array
//
//   The "week index" is the offset in the week tag array of a particular week
//   tag, starting at 0
//
// Variable/Constant Naming Conventions
//
//   Global variables/constants (and some function variables/contants) have
//   a suffix that identifies their type. These are convention, not enforced
//   by type checking. Such variables/constants end with "_" followed by a
//   single lowercase character, as follows:
//
//       _k    Constant (historical terminology from c)
//
// Class Variable and Method Naming Convention
//
//   Class variables that start with "_" are intended to be private variables,
//   although Javascript has no way of defining a private class variable. If
//   you see a class variable like this, you aren't intended to reach into the
//   class instance to access that variable. There are public methods to do so.
//
//   Class variables that don't start with "_" are intended to be public, so
//   you are free to access them directly from a class instance. BUT, IT'S
//   PROBABLY NOT A GOOD IDEA TO CHANGE THESE - CONSIDER THEM READ-ONLY.
//
//   The same convention applies to methods of a class. If they start with "_",
//   they are for the use of code in this file, not for a consumer of the
//   Calendar class. Just before each class is defined, there is a block of
//   comments that lists all class variable and methods.
//
// Other Thoughts
//
//   Consumers of the Calendar are really intended to interact only with the
//   main Calendar class. One instantiates a new Calendar instance with a
//   call to the constructure, e.g., new Calendar (), which gives you back
//   a populated instance of the class. One can now interact with it using
//   the public methods of the class.
//
//   There are other classes defined in this file for weeks, days, periods, and
//   (school) classes. Instances of these classes are not intended to be
//   created by the consumer of the Calendar class - this is done by the
//   Calendar class constructor. However, these lower-level classes do have
//   methods and public variables that can be accessed when one receives a
//   reference to an instance of one of these classes.
//
//   Calendar.js has no knowledge of the school year layout, including periods,
//   classes, etc. All of this comes from another file, which in the first
//   example, is MVHS.js whose role is to define the SchoolYearDefinitions
//   class from which all school-related information is extracted, either
//   from public class variables, or accessor functions to return information
//   from which Calendar.js builds its data structures. There are inter-
//   dependencies between the files in terms of data structure layout assumptions,
//   but these are documented fairly well in MVHS.js.
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// GLOBAL FUNCTIONS
//
// CalendarAssert
//
// Function to test an assertion and thow an exception, with traceback,
// if the assertion failed
//
// Arguments:
//  assertion         Boolean expression
//
//  msg               Message explaining the assertion
//
//  args              Optional list of arguments to output
//
// Returns:
//  If assertion is true, just return with no value. If false, emit an error
//  message, including msg and the args list, and throw an exception

function CalendarAssert (assertion, msg, ...args) {
  if (!assertion) {
    let message = "***ERROR: CalendarAssert Assertion Failed: " + msg;
    if (args.length > 0) {
      message += " " + args.join("\n, ")
    }
    console.log (message)
    throw Error ("Assertion Failed");
  }
} // function CalendarAssert

// ===========================================================================
// CalendarMessage
//
// Function to emit a message, with optional arguments, which are separated
// by ", "
//
// Arguments:
//  msg               Message
//
//  args              Optional list of arguments to output
//
// Returns:
//  None

function CalendarMessage (msg, ...args) {

  let message = "Calendar Message: " + msg;
  if (args.length > 0) {
    message += " " + args.join("\n, ")
  }
  console.log (message)
} // function CalendarMessage

// ===========================================================================
// CalendarMidnightOfDate
//
// Function to return a Date() value corresponding to midnight on the date
// specified.
//
// Arguments:
//  sDate         (String) Date in the format yyyy-mm-yy
//
// Returns:
//  New Date() object corresponding to midnight, local time, of the date
// specified

function CalendarMidnightOfDate (sDate) {

  // Verify the existence and type of the argument
  CalendarAssert (
    (sDate !== undefined) &&
    (typeof sDate == "string"),
    "CalendarMidnightOfDate called with invalid argument",
    sDate, typeof sDate
  );

  // Appending the "T00:00:00" seems to be the only reliable way to get the
  // Date() code to force a local timezone. Using .setHours doesn't seem to work
  // in all cases, despite the documentation to the contrary.
  let mysDate = sDate + "T00:00:00";
  return new Date(mysDate);

} // function CalendarMidnightOfDate

// ===========================================================================
// CalendarPadStringLeft
//
// Function to return a string, padded to the left to a specified width with
// a character
//
// Arguments:
//  value         (Value that can be converted to a string) The value to be
//                padded
//
//  width         (Number) Width to which the value should be padded
//
//  character     (Character) Character to use when padding
//
// Returns:
//  The "value" string, padded to the left to "width" characters, using the
//  pad character "character"

function CalendarPadStringLeft (value, width, character) {

  return value.toString().padStart(width, character);

} // function CalendarPadStringLeft

// ===========================================================================
// CalendarPadStringRight
//
// Function to return a string, padded to the right to a specified width with
// a character
//
// Arguments:
//  value         (Value that can be converted to a string) The value to be
//                padded
//
//  width         (Number) Width to which the value should be padded
//
//  character     (Character) Character to use when padding
//
// Returns:
//  The "value" string, padded to the right to "width" characters, using the
//  pad character "character"

function CalendarPadStringRight (value, width, character) {

  return value.toString().padEnd(width, character);

} // function CalendarPadStringRight

// ===========================================================================
// CalendarHHMMSSAsString
//
// Function to return a string in a [d] [h]h:mm:ss format
//
// Arguments:
//  hours         (Number in the range 0..23) The number of hours
//                padded
//
//  minutes       (Number in the range 0..59) The number of minutes
//
//  seconds       (Number in the range 0..59) The number of seconds
//
//  days          (Optional positive number) The number of days. Nothing is
//                returned for this unless it is > 0;
//
// Returns:
//  A string in the format "[d] [h]h:mm:ss"

function CalendarHHMMSSAsString (hours, minutes, seconds, days) {

  let dayCount = days || 0;
  let dhhmmss = "";
  if (dayCount > 0) {
    dhhmmss = dayCount.toString() + "d ";
  }
  dhhmmss +=
    (hours.toString() + ":" +
    CalendarPadStringLeft(minutes,2,"0") + ":" +
    CalendarPadStringLeft(seconds,2,"0"));

  return dhhmmss;

} // function CalendarHHMMSSAsString

// END GLOBAL FUNCTIONS DEFINITIONS
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// GLOBAL CONSTANTS AND VARIABLES
//

// Maximum print depth for calls to class toString methods. This is simply to
// limit runaway nesting, which is a code bug.
const _CalendarMaxToStringDepth_k = 10;

// Make first period and last period globals so that they are visible to all
// classes. They are loaded from the instance of the SchoolYearDefinitions class
let CalendarFirstPeriod;
let CalendarLastPeriod;

// Define class type constants, which will be placed as a class variable in
// every class for error checking purposes

const _classTypeClass_k     = "Type Class";
const _classTypePeriod_k    = "Type Period";
const _classTypeDay_k       = "Type Day";
const _classTypeWeek_k      = "Type Week"
const _classTypeCalendar_k  = "Type Calendar";

// END GLOBAL CONSTANTS AND VARIABLES
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class CalendarClassObject
//
// This class defines the structure of the object for each (school) class.
//
// Public Class Variables:
//
//    classType     (String) Type of class - see _classType*_k. Used for
//                  argument checking
//
//    period:       (Positive Integer) Period number
//
//    className:    (String or null) Name of the class or null if there
//                  is no class this period
//
//    room:         (String) Room in which the class is held
//
//    teacher:      (String) Name of the teacher for the class
//
// Public Methods:
//
//    returnString = toString ([linePrefix] [, maxDepth])
//                  Return a string with printable information about the class

const _CalendarClassObjectDebug_k = false;
class CalendarClassObject {

  // ===========================================================================
  // constructor
  //
  // Initialize the CalendarClassObject object
  //
  // Arguments:
  //
  //  period      (REQUIRED Number) Period number
  //
  //  className   (REQUIRED String) Name of the class or null
  //
  //  room:       (REQUIRED String) Room in which the class is held
  //
  //  teacher:    (REQUIRED String) Name of the teacher for the class

  constructor (period, className, room, teacher) {

    // Make sure the caller provided the required arguments and argument types
    CalendarAssert(
      (period    !== undefined)            && (typeof period  === "number") &&
      (period    >= CalendarFirstPeriod )  && (period         <=  CalendarLastPeriod) &&
      (className !== undefined)            && ((className     === null) || (typeof className === "string")) &&
      (room      !== undefined)            && (typeof room    === "string") &&
      (teacher   !== undefined)            && (typeof teacher === "string"),
      "CalendarClassObject.constructor called with invalid arguments",
      period, className, room, teacher
    );
    this.classType = _classTypeClass_k;

    // This information is really just for reporting purposes
    this.period = period;
    this.className = className;
    this.room = room;
    this.teacher = teacher;

    if (_CalendarClassObjectDebug_k) {
      CalendarMessage ("DEBUG: Created new CalendarClassObject instance for period ", period);
      CalendarMessage ("DEBUG: Instance is\n", this.toString("  ", 1));
    }

  } // CalendarClassObject.constructor

  // ===========================================================================
  // toString
  //
  // Returns a multi-line string with the printable information about the
  // class
  //
  // Arguments:
  //  linePrefix  (OPTIONAL String) The prefix to use for each line. If not
  //              specified, defaults to "";
  //
  //  maxDepth    (OPTIONAL Positive Number) Max depth of class toString calls.
  //              If not specified, defaults to _CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this period

  toString (linePrefix, maxDepth) {

    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || _CalendarMaxToStringDepth_k;
    if (myMaxDepth-- <= 0) return "";

    // A null in the className field indicates that there is no class in that
    // period
    if (this.className === null) {
      return myLinePrefix + "There is no class in period " + this.period + "\n"
    }

    // Otherwise, dump the information
    var returnString =
    myLinePrefix + "Class in period " + this.period + " is " + this.className +
                   " taught by " + this.teacher + " in room " + this.room + "\n";

    return returnString;

  } // CalendarClassObject.toString

} // class CalendarClassObject


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class CalendarPeriodObject
//
// This class defines the structure of the object for each period of the day.
//
// Public Class Variables:
//
//    classType     (String) Type of class - see _classType*_k. Used for
//                  argument checking
//
//    period:       (Number) Period number -1..7. If this is -1, it is a pseudo
//                  period, used for before and after school, passing periods
//                  and lunch and there is no classInfoObject.
//
//    name:         (String) Name of the period
//
//    startSTime:   (String) Start time of the period: "hh:mm" using a 24-hour
//                  clock
//
//    startDAdj:    (Array) Array containing hours, minutes, seconds and ms to
//                  be used in a .setHours() call to adjust the eDate value of
//                  the day to be a full Date() for the start of the period
//
//    endSTime:     (String) End time of the period: "hh:mm" using a 24-hour
//                  clock
//
//    endDAdj:      (Array) Array containing hours, minutes, seconds and ms to
//                  be used in a .setHours() call to adjust the eDate value of
//                  the day to be a full Date() for the start of the period
//
//    comment:      (String) Comment describing the period. May be ""
//
//    classInfoObject:
//                  (CalendarClassObject) Class information for this period. If
//                  there is no class this period, this value is null
//
// Public Methods:
//
//    returnString = toString ([linePrefix] [, maxDepth])
//                  Return a string with printable information about the period

const _CalendarPeriodObjectDebug_k = false;
class CalendarPeriodObject {

  // ===========================================================================
  // constructor
  //
  // Initialize the CalendarPeriodObject object
  //
  // Arguments:
  //
  //  period      (REQUIRED Number) Period number. If this is -1, it is a
  //              pseudo period, used for before and after school, passing
  //              periods, and lunch
  //
  //  name        (REQUIRED String) Name of the period
  //
  //  startTime   (REQUIRED String) Start time of the period in "hh:mm" (24 hour
  //              clock)
  //
  //  endTime     (REQUIRED String) End time of the period in "hh:mm" (24 hour
  //              clock)
  //
  //  comment     (OPTIONAL String) Comment describing the period or null
  //
  //  eodAdjust   (REQUIRED boolean) True if this period is the last one in the
  //              day; false for all others
  //
  //  classInfoObject
  //              (REQURIED CalendarClassObject instance) The class information
  //              object corresponding to this period, or null if there is no
  //              class during this period.

  constructor (period, name, startTime, endTime, comment, eodAdjust, classInfoObject) {

    // Make sure the caller provided the required arguments and argument types
    CalendarAssert (
      (period !== undefined)            && (typeof period    === "number") &&
      ((period === -1) ||
       ((period >= CalendarFirstPeriod) && (period <= CalendarLastPeriod))) &&
      (name   !== undefined)            && (typeof name      === "string") &&
      (startTime !== undefined)         && (typeof startTime === "string") &&
      (endTime !== undefined)           && (typeof endTime   === "string") &&
      (eodAdjust !== undefined)         && (typeof eodAdjust === "boolean") &&
      ((classInfoObject !== undefined)   &&
        (classInfoObject === null) || (classInfoObject.classType === _classTypeClass_k)),
      "CalendarPeriodObject.constructor called with invalid arguments",
      period, name, startTime, endTime, classInfoObject, CalendarFirstPeriod, CalendarLastPeriod
    );
    this.classType = _classTypePeriod_k;

    this.period = period;
    this.name = name;

    // Save both the string version of start and end times, but also convert
    // them an array that provides the arguments to .setHour() used with the
    // day's Date() object to get the absolute start and end times of the period
    let errorString = period + ":" + name + ":" + startTime + ":" + endTime + ":" + comment;
    this.startSTime = startTime;
    this.startDAdj = _helperSTimeToSetTimeArgs(startTime, false, false, errorString);

    this.endSTime = endTime;
    this.endDAdj = _helperSTimeToSetTimeArgs(endTime, true, eodAdjust, errorString);

    // If the period number isn't in the range of the class array, then
    // make classInfoObject null, otherwise extract the class information for this
    // period
    if (classInfoObject === null || classInfoObject.className === null) {
      this.classInfoObject = null;
    } else {
      this.classInfoObject = classInfoObject;
    }

    // If a comment was not included in the constructor call, make it empty
    this.comment = comment || "";

    if (_CalendarPeriodObjectDebug_k) {
      CalendarMessage ("DEBUG: Created new CalendarPeriodObject instance");
      CalendarMessage ("DEBUG: Instance is\n", this.toString("  ", 2));
    }

    // Helper function to convert a string in the format "[h]h:[m]m" to an
    // array that provides the arguments to .setTime() to compute the absolute
    // start and end times of the period on a particular day.
    //
    // Arguments:
    //  sTime       String to convert, in [h]h:[m]m format
    //
    //  isEndTime   If true, this is the period end time, which needs to be
    //              adjusted; false otherwise
    //
    //  eodAdjust   True if this value is for an end-of-day end time, which
    //              is special-cased to put the value 1ms before midnight.
    //              Only used if isEndTime is also true.
    //
    //  errorString Context string to print if an error is found
    //
    // Returns:
    //  Array of [hours, minutes, seconds, milliseconds] for the relative time
    //  for this date. Will be adjusted if isEndTime is true.

    function _helperSTimeToSetTimeArgs (sTime, isEndTime, eodAdjust, errorString) {
      // Define some useful constants
      const HoursPerDay_k = 24;
      const MinutesPerHour_k = 60;
      const SecondsPerMinute_k = 60
      const MsPerSecond_k = 1000;

      // Pattern match the time format. H is returned in [1]; M in [2]
      let timeSplit = sTime.match(/^\s*(\d+)\:(\d+)\s*$/);
      let hours = parseInt(timeSplit[1],10);
      let minutes = parseInt(timeSplit[2],10);

      // Throw an error if the parse failed. This is a bug in the caller of
      // the constructor, so it's OK to give up if an error is found.
      CalendarAssert (
        !isNaN(hours)           &&
        !isNaN(minutes)         &&
        (hours >= 0)            &&
        (hours < HoursPerDay_k) &&
        (minutes >= 0)          &&
        (minutes < MinutesPerHour_k),
        "CalendarPeriodDecr.constructor found an incorrect formatted time",
        sTime, errorString
      );

      // Case 1: The start time of the period
      // If this isn't the end time, simply return hours and minutes and leave
      // seconds and milliseconds zero
      if (!isEndTime) { return [hours, minutes, 0, 0] };

      // Case 2: The end time for the last period of the day
      // Here if this is the end time, which needs some adjustment. If eodAdjust
      // is true, this is the end time for the period at the end of the day, so
      // just return [23, 59, 59, 999], which is 1ms before midnight
      if (eodAdjust) { return [HoursPerDay_k-1, MinutesPerHour_k-1, SecondsPerMinute_k-1, MsPerSecond_k-1] };

      // Case 3: The end time for all but the last period of the day
      // Here if this is an end time AND not the last period in the day. The
      // adjustment is to back up the end time by 1 ms. This means that we need
      // to backup minutes by 1, then add in 59, 999 for the seconds and ms
      minutes -= 1;           // Backup minutes by 1
      if (minutes < 0) {      // If it went negative, make it 59 and decrement
        hours -= 1;           // hours by 1
        minutes = MinutesPerHour_k - 1;
      }
      return [hours, minutes, SecondsPerMinute_k-1, MsPerSecond_k-1];

    } // CalendarPeriodObject.constructor._helperSTimeToSetTimeArgs

  } // CalendarPeriodObject.constructor

  // ===========================================================================
  // toString
  //
  // Returns a multi-line string with the printable information about the
  // period
  //
  //  linePrefix  (OPTIONAL String) The prefix to use for each line. If not
  //              specified, defaults to "";
  //
  //  maxDepth    (OPTIONAL Positive Number) Max depth of class toString calls.
  //              If not specified, defaults to _CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this period

  toString (linePrefix, maxDepth) {

    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || _CalendarMaxToStringDepth_k;
    if (myMaxDepth-- <= 0) return "";

    var returnString =
    myLinePrefix + "Period " + this.name;
    if (this.period > 0)  {
      returnString += " (" + this.period + ")";
    }
    myLinePrefix += "  ";
    returnString +=
    myLinePrefix + "starts at " + this.startSTime + " (" + this.startDAdj + ")\n" +
    myLinePrefix + "ends at " + this.endSTime + " (" + this.endendDAdj + ")\n";

    if (this.comment !== null) {
      returnString +=
      myLinePrefix + "comment: " + this.comment + "\n";
    }

    if ((this.classInfoObject !== null) && (myMaxDepth > 0)) {
      returnString += this.classInfoObject.toString(myLinePrefix, myMaxDepth);
    }

    return returnString;

  } // CalendarPeriodObject.toString

} // class CalendarPeriodObject


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class CalendarDayObject
//
// This class defines the structure of the object for each day of the week.
//
// Public Class Variables:
//
//    classType     (String) Type of class - see _classType*_k. Used for
//                  argument checking
//
//    dayName:      (String) Name of the day, e.g., "Sunday"
//
//    printDate:    (String) Date in printable format, e.g., "9/30/2020"
//
//    dayType:      (String) Type of day: See _dayType* in MVHS.js
//
//    dayTag        (String) Day tag for this day
//
//    dayIdx        (Positive number) day index for this day
//
//    weekTag       (String) Week tag for the week containing this day
//
//    eDate:        (Date() object) Date (at midnight) for this day
//
//    periodObjectArray
//                  (Array of CalendarPeriodObject) Array containing the
//                  objects for each period of the day. If the length
//                  of this array is 0, there are no periods in the day
//
// Public Methods:
//
//    returnString = toString ([linePrefix] [, maxDepth])
//                  Return a string with printable information about the day

let _CalendarDayObjectDebug_k = false;
class CalendarDayObject {

  // ===========================================================================
  // constructor
  //
  // Initialize the CalendarDayObject object
  //
  // Arguments:
  //  dayTag      (REQUIRED String) The day tag for this day
  //
  //  dayIdx      (REQUIRED positive number) The day index for this day
  //
  //  dayType     (REQUIRED String) Type of day: one of _dayType*
  //
  //  weekTag     (REQUIRED String) The week tag for the week in which this
  //              day is found
  //
  //  periodObjectArray
  //              (REQUIRED Array of CalendarPeriodObject) Array of the
  //              CalendarPeriodObject instances for each period of the day, or
  //              [] if there are no periods.

  constructor (dayTag, dayIdx, weekTag, dayType, periodObjectArray) {

    // Make sure the caller provided the required arguments
    CalendarAssert (
      (dayTag                 !== undefined) &&
      (dayIdx                 !== undefined) &&
      (dayIdx                 >= 0)          &&
      (weekTag                !== undefined) &&
      (dayType                !== undefined) &&
      (periodObjectArray      !== undefined),
      "CalendarDayObject.constructor called with invalid arguments",
      periodObjectArray, dayType
    );
    this.classType = _classTypeDay_k;

    this.dayTag = dayTag;
    this.dayIdx = dayIdx;
    this.weekTag = weekTag;
    this.dayType = dayType;

    const dayIndexToName = [
      "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    ];

    // Set the eDate class variable
    this.eDate = CalendarMidnightOfDate(dayTag);

    // Generate a printable date
    let month = this.eDate.getMonth() + 1;
    this.printDate =
    month.toString() + "/" +
    this.eDate.getDate().toString() + "/" +
    this.eDate.getFullYear().toString();

    // Create the printable day name
    this.dayName = dayIndexToName[this.eDate.getDay()];

    // Build the periodObjectArray
    this.periodObjectArray = [];
    for (let p = 0; p < periodObjectArray.length; p++) {
      CalendarAssert (
        periodObjectArray[p].classType === _classTypePeriod_k,
        "CalendarDayObject constructor called with invalid period object array",
        p, periodObjectArray[p]
      );
      this.periodObjectArray.push(periodObjectArray[p]);
    }

    if (_CalendarDayObjectDebug_k) {
      CalendarMessage ("DEBUG: Created new CalendarDayObject instance");
      CalendarMessage ("DEBUG: Instance is\n", this.toString("  ", 3));
    }
  } // CalendarDayObject.constructor

  // ===========================================================================
  // toString
  //
  // Returns a multi-line string with the printable information about the
  // day
  //
  // Arguments:
  //  linePrefix  (OPTIONAL String) The prefix to use for each line. If not
  //              specified, defaults to "";
  //
  //  maxDepth    (OPTIONAL Positive Number) Max depth of class toString calls.
  //              If not specified, defaults to _CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this day

  toString (linePrefix, maxDepth) {
    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || _CalendarMaxToStringDepth_k;
    if (myMaxDepth-- <= 0) return "";

    let dayNum = this.dayIdx+1;
    var returnString =
    myLinePrefix +
    "Day " + dayNum + ", " +
    this.dayName + ", " +
    this.printDate + " (" + this.eDate + ") " +
    "is a " + this.dayType +
    ", has week tag " + this.weekTag +
    " and day tag " + this.dayTag +
    ", and has "

    if (this.periodObjectArray.length === 0) {
      returnString += "no periods\n";
    } else if (myMaxDepth <= 0) {
      returnString += "periods suppressed by maxDepth limitations\n";
    } else {
      returnString += "the following periods:\n";
      for (const period of this.periodObjectArray) {
        returnString += period.toString(myLinePrefix, myMaxDepth);
      }
    }

    return returnString;

  } // CalendarDayObject.toString

} // class CalendarDayObject


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class CalendarWeekObject
//
// This class defines the structure of the object for a week of classes,
// which looks like this
//
// Public Class Variables:
//    classType   (String) Type of class - see _classType*_k. Used for
//                argument checking
//
//    weekTag:    (String) Date for the Sunday of this week in the format
//                "yyyy-mm-dd".
//
//    weekIdx:    (Positive Integer) Index of week, starting at zero. Can
//                be used to index _weekTagArray in the calendar
//
//    eDate       (Date() object) Date corresponding to weekTag
//
//    dayObjectArray
//                (Array) Array 0..6, indexed by day of the week (Sunday=0,
//                Saturday=6) containing the CalendarDayObject object for each
//                day of that week.
//
// Public Methods:
//
//    setDayObjectArray (dayObjectArray)
//                  Set the dayObjectArray class variable
//
//    returnString = toString ([linePrefix] [, maxDepth])
//                  Return a string with printable information about the week

class CalendarWeekObject {

  // ===========================================================================
  // constructor
  //
  // Initialize a new calendar week object
  //
  // Arguments:
  //  weekTag     (REQUIRED String) Week tag for the week for this object
  //
  //  weekIdx     (REQUIRED Number) Week index for this week
  //
  // NOTE: The setDayObjectArray must be called after the constructor to set
  // the dayObjectArray class variable.

  constructor (weekTag, weekIdx) {

    CalendarAssert (
      (weekTag    !== undefined) &&
      (weekIdx    !== undefined),
      "CalendarWeekObject.constructor called with invalid arguments",
      weekTag, weekIdx
    );
    this.classType = _classTypeWeek_k;

    // Initialize the variables
    this.weekTag = weekTag;
    this.weekIdx = weekIdx;
    this.eDate = CalendarMidnightOfDate(weekTag);

    this.dayObjectArray = []

  } // CalendarWeekObject.constructor

  // ===========================================================================
  // setDayObjectArray
  //
  // Supply the dayObjectArray for this week
  //
  // Arguments:
  //  dayObjectArray
  //              (REQUIRED Array[0..6]) Array of CalendarDayObject arguments for
  //              each day of the week. This may also be null if there are no
  //              school days in the list

  setDayObjectArray (dayObjectArray) {
    const daysPerWeek_k = 7;

    CalendarAssert (
      (dayObjectArray != undefined) &&
      (dayObjectArray.length === daysPerWeek_k),
      "CalendarWeekObject.setDayObjectArray called with invalid arguments",
      dayObjectArray
    )
    this.dayObjectArray = [...dayObjectArray];

  } // CalendarWeekObject.setDayObjectArray

  // ===========================================================================
  // toString
  //
  // Returns a multi-line string with the printable information about the
  // day
  //
  // Arguments:
  //  linePrefix  (OPTIONAL String) The prefix to use for each line. If not
  //              specified, defaults to "";
  //
  //  maxDepth    (OPTIONAL Positive Number) Max depth of class toString calls.
  //              If not specified, defaults to _CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this day

  toString (linePrefix, maxDepth) {
    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || _CalendarMaxToStringDepth_k;
    if (myMaxDepth-- <= 0) return "";

    let weekNum = this.weekIdx+1;
    var returnString =
    myLinePrefix +
    "Week " + weekNum +
    ", with week tag " + this.weekTag +
    ", starts on " + this.weekTag +
    " (" + this.eDate + "), and has the following days:\n";
    myLinePrefix += "  ";
    if (myMaxDepth <= 0) {
      returnString += myLinePrefix + "maxPrint suppressed the printing of days\n";
    } else {}
      for (const day of this.dayObjectArray) {
        returnString += day.toString(myLinePrefix, myMaxDepth);
      }

    return returnString;

  } // CalendarWeekObject.toString

} // class CalendarWeekObject



// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class Calendar
//
// This class defines the structure of the data structure for the calendar.
//
// Public Class Variable:
//
//    classType     (String) Type of class - see _classType*_k. Used for
//                  argument checking
//
//    version       (String) The version number of the Calendar class (and all
//                  other sub-classes) as {major}.{minor}.{patch}.
//
//    schoolVersion (String) The version number of the file containing the
//                  SchoolYearDefinitions class.
//
//    firstPeriod   (Number) The period number of the first period of the day
//
//    lastPeriod    (Number) The period number of the last period of the day
//
// Private Class Variables:
//
//    _startEDate:  (Date() Object) Midnight of the first day of school
//
//    _startWEDate  (Date() Object) Midnight of the Sunday of the first week of
//                  school (essentially, _startEDate moved back to the previous
//                  Sunday)
//
//    _endEDate:    (Date() Object) Midnight of the last day of school
//
//    _endWEDate    (Date() Object) Midnight of the Saturday of the last week of
//                  school (essentially, _endEDate moved forward to the next
//                  Saturday)
//
//    _dayObjectHash
//                  (Hash of CalendarDayObject) A hash structure, indexed by the
//                  day tag of each day, with the value being the CalendarDayObject
//                  object for the day.
//
//    _dayTagArray  (Array of String) An array containing the day tags of each
//                  day of the school year, whether an actual school day, a
//                  weekend day, or a holiday. By indexing into this array by
//                  day index, one can extract the day tag (to be used to)
//                  index into _dayObjectHash) for any other day.
//
//    _weekObjectHash
//                  (Hash of CalendarWeekObject) The calendar data structure,
//                  indexed by week tag of the Sunday of the week, with the
//                  value being the CalendarWeekObject object for that week
//
//    _weekTagArray (Array of String) An array containing the week tags of
//                  each week of the school year, in order of week. By indexing
//                  into this array by the week number, one can extract the
//                  week tag (to be used to index into _weekObjectHash) for any other
//                  week.
//
//    _periodObjectHash
//                  (Readonly hash of CalendarPeriodObject) A hash, indexed by
//                  The type of school day (see _periodsForDayType*_k) and whose
//                  value is an array of CalendarPeriodObject instances for
//                  each period of that day type. The class information for
//                  real periods will have already been inserted into these
//                  objects
//
// Public Methods:
//
//    advanceToFutureDay (eDate, count)
//                  Update eDate to advance the date by the value of the count
//                  argument
//
//    eDate = convertSDateToEDate (sDate)
//                  Return a new Date() object corresponding to the sDate argument
//
//    dayTag = getDayTag (eDate)
//                  Return a day tag corresponding to the eDate argument
//
//    weekTag = getWeekTag (eDate)
//                  Return a week tag corresponding to the eDate argument
//
//    array = getDayTagArray ()
//                  Return the day tag array for each day in the calendar
//
//    array = getWeekTagArray ()
//                  Return the week tag array for each week in the calendar
//
//    dayObj = getDayByIndex (dayIndex)
//                  Return the CalendarDayObject object for the day whose index
//                  is specified by the dayIndex argument
//
//    dayObj = getDayByTag (dayTag)
//                  Return the CalendarDayObject object for the day whose day tag
//                  is specified by the dayTag argument
//
//    weekObj = getWeekByIndex (weekIndex)
//                  Return the CalendarWeekObject object for the week whose index
//                  is specified by the weekIndex argument
//
//    weekObj = getWeekByTag (weekTag)
//                  Return the CalendarWeekObject object for the week whose day tag
//                  is specified by the weekTag argument
//
//    startEDate = getStartEDate ()
//                  Return a new Date() object corresponding to the school year
//                  start date supplied to the constructor
//
//    endEDate = getEndEDate ()
//                  Return a new Date() object corresponding to the school year
//                  end date supplied to the constructor
//
//    eDate = getFirstDayOfWeek (eDate)
//                  Return a new Date() object corresponding to the first day
//                  of the week (the Sunday before or equal to the eDate
//                  argument) corresponding to the eDate argument.
//
//    eDate = getLastDayOfWeek (eDate)
//                  Return a new Date() object corresponding to the last day
//                  of the week (the Saturday after or equal to the eDate
//                  argument) corresponding to the eDate argument.
//
//    match = getPeriodByDateAndTime (eDate)
//                  Return the day, period, and index of the period in which
//                  the eDate argument exists.
//
//    match = getNextPeriod (lastMatch, matchRealPeriod)
//                  Return the day, period, and index of the next period following
//                  the one proided by the lastMatch argument (which is the
//                  match argument from the last call to getPeriodByDateAndTime,
//                  or the last call to getNextPeriod)
//
//    timeLeft = calculateTimeLeft (startTime, endTime)
//                  Calculate the time in the interval between startTime and
//                  endTime (both in milliseconds) and return an object that
//                  describes that time
//
//    timeLeft = getTimeRemainingInPeriod (eDate, pObj)
//                  Returns an object providing the time remaining in a period
//                  described by the pObj argument relative to the eDate argument
//
//    timeLeft = getTimeRemainingUntilPeriod (eDate, dObj, pObj)
//                  Returns an object providing the time between the eDate
//                  argument and start time of the day and period described by
//                  the dObj and pObj arguments
//
//    version = getVersion ()
//                  Return the version string
//
//    returnString = toString ([linePrefix] [, maxDepth])
//                  Return a string with printable information about the calendar

// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=

class Calendar {

  // ===========================================================================
  // Constructor
  //
  // Initialize a new Calendar Object
  //
  // Arguments:
  //  None - Everthing comes from the SchoolYearDefinitions class.
  //
  // Returns:
  //  this

  constructor () {
    const daysPerWeek_k = 7;
    const maxDayIdx_k = 365
    const maxWeekIdx_k = 52;
    this.classType = _classTypeCalendar_k;

    this.version = CalendarVersion;
    let school;
    // If running on a browser, the html file loads MVHS.js before Calendar.js
    // and the SchoolYearDefinitions class is visible. If running on node.js,
    // one has to require the file.
    if (typeof process !== "undefined") {
      CalendarMessage("Attempting to load MVHS.js")
      const SchoolYearDefinitions = require('./MVHS.js');
      school = new SchoolYearDefinitions ();
    } else {
      school = new SchoolYearDefinitions ();
    }
    this.schoolVersion = school.version;

    // Get the start and end dates for the school year
    let startSDate = school.firstDate;
    let endSDate = school.lastDate;
    CalendarFirstPeriod = school.firstPeriod;
    CalendarLastPeriod = school.lastPeriod;

//    let defaultWeek = school.getDefaultWeek();
//    let weekExceptions = school.getWeekExceptions();

    CalendarAssert (
      (typeof(startSDate) === "string") &&
      (typeof(endSDate)   === "string"),
      "Calendar.constructor: startSDate or endSDate are invalid",
      startSDate, endSDate
    );

    // Force the start and end dates to midnight local time and store
    this._startEDate = this.convertSDateToEDate(startSDate);
    this._endEDate = this.convertSDateToEDate(endSDate);
    // Calculate the Sunday before the first day of school and the
    // Saturday after the last day of school. These define the bounds for
    // all of the weeks of the school year and are the limits of the
    // _dayTagArray array.
    this._startWEDate = this.getFirstDayOfWeek(this._startEDate);
    this._endWEDate = this.getLastDayOfWeek(this._endEDate);

    // ---------- Class objects
    // Create objects for class information about each period.
    let classInfoObjectArray = _helperInstantiateClassInfoObjects(school.getClassInfo);

    // ---------- Period Objects
    // and do the same for the different periods
    this._periodObjectHash = _helperInstantiatePeriodObjects(
      school.getPeriodDayTypes,
      school.getPeriodCount,
      school.getPeriodInfo,
      classInfoObjectArray
    );

    // ---------- Day and week objects
    // Build the remainder of the calendar data structure and setup the
    // class variables _weekTagArray, _weekObjectHash, _dayTagArray and
    // _dayObjectHash, starting with empty values for each
    this._weekTagArray = [];
    this._weekObjectHash = new Object();
    let weekIdx = 0;
    this._dayTagArray = [];
    this._dayObjectHash = new Object();
    let dayIdx = 0;

    //  Start at the Sunday before the start of school. The loop actully runs by the
    //  day to fill in both the week and day class variable, and to call the
    //  CalendarWeekObject and CalendarDayObject constructors for each week and day
    //  in the school year. eDate moves as days are processed.
    let eDate = new Date(this._startWEDate.getTime());
    while (eDate.getTime() <= this._endWEDate.getTime()) {

      // Push week tag onto the week tag array
      let weekTag = this.getDayTag(eDate);
      this._weekTagArray.push (weekTag);

      // Create a new week object and store it in the _weekObjectHash object
      let weekObject = new CalendarWeekObject(weekTag, weekIdx);
      this._weekObjectHash[weekTag] = weekObject;

      // Loop over each day, create a day object and add it to the day
      // object hash. Also add the day tag to the day tag array
      let dayObjectArray = [];
      for (let d = 0; d < daysPerWeek_k; d++) {
        let dayTag = this.getDayTag(eDate);

        // Extract the day type and the period array from the day array. These
        // are used as args in the creation of the new day object
        let dayType = school.getDayInfo (weekTag, d, "daytype");
        let periodHashName = school.getDayInfo (weekTag, d, "periodidx");
        let periodsForThisDay = this._periodObjectHash[periodHashName];

        // Create the new day object and update _dayTagArray, _dayObjectHash
        let day = new CalendarDayObject (dayTag, dayIdx, weekTag, dayType, periodsForThisDay);
        dayObjectArray.push (day);
        this._dayTagArray.push (dayTag);
        this._dayObjectHash[dayTag] = day;

        dayIdx++;
        this.advanceToFutureDay (eDate,1);

        CalendarAssert (
          dayIdx <= maxDayIdx_k,
          "Calendar.constructor runaway dayIdx - code logic failure",
          dayIdx
        );
      } // for (let d = 0; d < dayConstructorArgs.length; d++)

        // Put the day object array in the week object
        weekObject.setDayObjectArray (dayObjectArray);

        weekIdx++;
        CalendarAssert (
          weekIdx <= maxWeekIdx_k,
          "Calendar.constructor runaway weekIdx - code logic failure",
          weekIdx
        );

      } // while (eDate.getTime() <= this._endWEDate.getTime())

      // ---------- Done
      return this;

      // -----------------------------------------------------------------------
      // _helperInstantiateClassInfoObjects
      //
      // Helper function to instantiate new CalendarClassObject instances for
      // each class.
      //
      // Arguments:
      //  getClassInfoFunc    The accessor function to call to return class
      //                      information
      //
      // Returns:
      //  Array of CalendarClassObject instances for each of the periods

      function _helperInstantiateClassInfoObjects (getClassInfoFunc) {
        // Loop through the periods and create a new object for the
        // class info, and store it in the classInfoObjectArray based on the
        // period number
        let classInfoObjectArray = [];
        for (let p = CalendarFirstPeriod; p <= CalendarLastPeriod; p++) {
          let period = getClassInfoFunc(p, "period");
          let className = getClassInfoFunc(p, "class");
          let room = getClassInfoFunc(p, "room");
          let teacher = getClassInfoFunc(p, "teacher");

          CalendarAssert (
            (period === Number(p)) &&
            (className !== undefined) &&
            (room !== undefined) &&
            (teacher !== undefined),
            "school classInfoArray has incorrect information",
            period, className, room, teacher
          );

          let classInfoObject = new CalendarClassObject (period, className, room, teacher);
          classInfoObjectArray[period] = classInfoObject;
        }
        // Now make sure that all entries 0..CalendarPeriodLlast_k either contain
        // a class info object, or are set to null
        for (let p = 0; p <= CalendarLastPeriod; p++) {
          if (classInfoObjectArray[p] === undefined) {
            classInfoObjectArray[p] = null;
          }
        }

        return classInfoObjectArray;
      } // function _helperInstantiateClassInfoObjects

      // -----------------------------------------------------------------------
      // _helperInstantiateClassPeriodObjects
      //
      // Helper function to instantiate new CalendarPeriodObject instances for
      // each period.
      //
      // Arguments:
      //  getPeriodDayTypesFunc
      //                      The accessor function to call to return the list
      //                      of day types
      //
      //  getPeriodCountFunc  The accessor function to call to return the count
      //                      of periods for each day type
      //
      //  getPeriodInfoFunc   The accessor function to call to return the period
      //                      information
      //
      //  classInfoObjectArray
      //                      The array (from _helperInstantiateClassInfoObjects)
      //                      containing the CalendarClassObject instances for
      //                      each period.
      //
      // Returns:
      //  The period object hash to store into this._periodObjectHash

      function _helperInstantiatePeriodObjects
      (
        getPeriodDayTypesFunc,
        getPeriodCountFunc,
        getPeriodInfoFunc,
        classInfoObjectArray
      ) {

        // Start with an empty hash to return to the caller
        let periodObjectHash = Object();

        // Get the list of period day types from the accessor function
        let periodDayTypes = getPeriodDayTypesFunc();
        for (const thisDayType of periodDayTypes) {

          // Initialize the key for the hash, and make the value an empty array
          // into which the CalendarPeriodObject instances will be added
          periodObjectHash[thisDayType] = [];

          // Get the number of periods for this day type
          let periodCount = getPeriodCountFunc(thisDayType);

          // Now loop over each of the elements
          for (let p = 0; p < periodCount; p++) {
            // Extract the information required for the CalendarPeriodObject
            // constructor call.
            let period    = getPeriodInfoFunc(thisDayType, p, "period");
            let name      = getPeriodInfoFunc(thisDayType, p, "name");
            let startTime = getPeriodInfoFunc(thisDayType, p, "startTime");
            let endTime   = getPeriodInfoFunc(thisDayType, p, "endTime");
            let comment   = getPeriodInfoFunc(thisDayType, p, "comment");
            let eodAdjust = getPeriodInfoFunc(thisDayType, p, "eodAdjust");

            // Validate that the values came back correctly
            CalendarAssert (
              (period     !== undefined) &&
              (name       !== undefined) &&
              (startTime  !== undefined) &&
              (endTime    !== undefined) &&
              (comment    !== undefined) &&
              (eodAdjust  !== undefined),
              "school periodDayHashType has incorrect information",
              period, name, startTime, endTime, comment, eodAdjust
            );

            // If this is a real period, find the class info object for that
            // period
            let classInfoObject = null;
            if ((period >= CalendarFirstPeriod) && (period <= CalendarLastPeriod)) {
              classInfoObject = classInfoObjectArray[period];
              CalendarAssert (
                classInfoObject.classType === _classTypeClass_k,
                "_helperInstantiateClassPeriodObjects got back an object that had the wrong type",
                classInfoObject
              );
            }

            // Create a new period object and push it onto the array for this
            // day type
            periodObjectHash[thisDayType].push(
              new CalendarPeriodObject(
                period, name, startTime, endTime, comment, eodAdjust, classInfoObject
              )
            );
          } // for (let p = 0; p < periodCount; p++)
        } // for (const thisDayType of periodDayTypes)

        return periodObjectHash;

      } // function _helperInstantiateClassInfoObjects

    } // Calendar.constructor

  // ===========================================================================
  // advanceToFutureDay
  //
  // Advance the argument to the same time on a future day.
  // NOTE: THIS MODIFIES THE ARGUMENT!!!
  //
  // Arguments:
  //  eDate      Date() value for the date to advance. This is modified.
  //
  //  count      Number of days to advance
  //
  // Returns:
  //  No direct return value, but the eDate argument is modified

  advanceToFutureDay (eDate, count) {

    // This is as simple as setting the date of the argument to the current
    // date plus count
    eDate.setDate(eDate.getDate() + count);

  } // Calendar.advanceToFutureDay

  // ===========================================================================
  // convertSDateToEDate
  //
  // Convert an sDate value to the corresponding eDate value, with the time
  // forced to midnight, local time, of that day.
  //
  // Arguments:
  //  sDate       (REQUIRED String) sDate of the form yyyy-mm-dd to convert to
  //              and eDate.
  //
  // Returns:
  //  New Date() object corresponding to the sDate input, with the time forced
  //  to midnight

  convertSDateToEDate (sDate) {

    let eDate = CalendarMidnightOfDate(sDate);
    return eDate;

  } //Calendar.convertSDateToEDate

  // ===========================================================================
  // getDayTag
  //
  // convert an Date() value to a day tag in the form yyyy-mm-dd.
  //
  // Arguments:
  //  eDate      Date() value for the date to convert
  //
  // Returns:
  //  Date of the argument, as a string in the form yyyy-mm-dd. Note that
  //  both month and day are left-padded, if necessary, with a 0 to two digits.

  getDayTag (eDate) {
    var myEDate = new Date(eDate.getTime());  // Make copy of eDate

    // getMonth() returns 0..1 and needs to be adjusted to 1..12
    let month = myEDate.getMonth() + 1;
    return (myEDate.getFullYear() + "-" +
    CalendarPadStringLeft(month, 2,"0") + "-" +
    CalendarPadStringLeft(myEDate.getDate(), 2, "0"))

  } // Calendar.getDayTag

  // ===========================================================================
  // getWeekTag
  //
  // convert an Date() value to a week tag in the form yyyy-mm-dd. The week tag
  // always represents the Sunday on or before the date specified in the argument,
  // so this is adjusted here.
  //
  // Arguments:
  //  eDate      Date() value for the date to convert
  //
  // Returns:
  //  Date of the Sunday on or before that specified in the argument, as
  //  a string in the form yyyy-mm-dd. Note that both month and day are left-
  //  padded, if necessary, with a 0 to two digits.

  getWeekTag (eDate) {

    // Return the day tag for the first day of the week
    return this.getDayTag (this.getFirstDayOfWeek(eDate));

  } // Calendar.getWeekTag

  // ===========================================================================
  // getDayTagArray
  //
  // Return the list of all day tags in the calendar
  //
  // Arguments:
  //  None
  //
  // Returns
  //  List of all day tags in the calendar

  getDayTagArray () {
    let dayList = [...this._dayTagArray]; // Return a copy of the list
    return dayList;

  } // Calendar.getDayTagArray

  // ===========================================================================
  // getWeekTagArray
  //
  // Return the list of all week tags in the calendar
  //
  // Arguments:
  //  None
  //
  // Returns
  //  List of all week tags in the calendar

  getWeekTagArray () {
    let weekList = [...this._weekTagArray]; // Return a copy of the list
    return weekList;
  } // Calendar.getWeekTagArray

  // ===========================================================================
  // getDayByIndex
  //
  // Return the CalendarDayObject object for a day based on the day index
  //
  // Arguments:
  //  dayIndex        Day index for which to return the daily schedule, in
  //                  the range 0.._dayTagArray.length-1
  //
  // Returns
  //  CalendarDayObject object for the day, or null if the day doesn't exist.

  getDayByIndex (dayIndex) {

    // Check for bounds of the argument and return null if the argument is
    // invalid.

    if ((dayIndex < 0) ||
    (dayIndex >= this._dayTagArray.length)) {
      return null;
    }

    // Otherwise, let getDayByTag do the work
    return this.getDayByTag (this._dayTagArray[dayIndex]);

  } // Calendar.getDayByIndex

  // ===========================================================================
  // getDayByTag
  //
  // Return the CalendarDayObject object for a day based on the day tag
  //
  // Arguments:
  //  dayTag        Day tag for which to return the daily schedule
  //
  // Returns
  //  CalendarDayObject object for the day, or null if the day doesn't exist.

  getDayByTag (dayTag) {

    return this._dayObjectHash[dayTag] || null;

  } // Calendar.geDayByTag

  // ===========================================================================
  // getWeekByIndex
  //
  // Return the CalendarWeekObject object for a week based on the week index
  //
  // Arguments:
  //  weekIndex       Week index for which to return the weekly schedule, in
  //                  the range 0.._weekTagArray.length-1
  //
  // Returns
  //  CalendarWeekObject object for the week, or null if the week doesn't exist.

  getWeekByIndex (weekIndex) {

    // Check for bounds of the argument and return null if the argument is
    // invalid.

    if ((weekIndex < 0) ||
    (weekIndex >= this._weekTagArray.length)) {
      return null;
    }

    // Otherwise, let getWeekByTag do the work
    return this.getWeekByTag (this._weekTagArray[weekIndex]);

  } // Calendar.getWeekByIndex

  // ===========================================================================
  // getWeekByTag
  //
  // Return the CalendarWeekObject object for a week based on the week tag
  //
  // Arguments:
  //  weekTag       Week tag for which to return the weekly schedule
  //
  // Returns
  //  CalendarWeekObject object for the week, or null if the week doesn't exist.

  getWeekByTag (weekTag) {

    return this._weekObjectHash[weekTag] || null;

  } // Calendar.getWeekByTag

  // ===========================================================================
  // getStartEDate
  //
  // Return the Date() value of the last day of the school year (defined on the)
  // new Calendar call
  //
  // Arguments:
  //  None
  //
  // Returns
  //  Date() value of the start of the calendar

  getStartEDate () {

    return new Date(this._startEDate.getTime());

  } // Calendar.getStartEDate


  // ===========================================================================
  // getEndEDate
  //
  // Return the Date() of the first day of the school year (defined on the)
  // new Calendar call
  //
  // Arguments:
  //  None
  //
  // Returns
  //  Date() value of the end of the calendar

  getEndEDate () {

    return new Date(this._endEDate.getTime());

  } // Calendar.getEndEDate

  // ===========================================================================
  // getFirstDayOfWeek
  //
  // Return a new Date() value for the Sunday of the week containing the
  // argument
  //
  // Arguments:
  //  eDate   Date() value for which the previous Sunday is desired
  //
  // Returns
  //  New Date() value of the Sunday of the week corresponding to the argument

  getFirstDayOfWeek (eDate) {
    let newDate = new Date(eDate.getTime())

    // If getDay() returns 0..6 for Sunday..Saturday, then backing up
    // the date by that amount leaves the date at Sunday of the week
    newDate.setDate(eDate.getDate() - eDate.getDay());

    return newDate;
  } // Calendar.getFirstDayOfWeek

  // ===========================================================================
  // getLastDayOfWeek
  //
  // Return a new Date() value for the Saturday of the week containing the
  // argument
  //
  // Arguments:
  //  eDate   Date() value for which the next Saturday is desired
  //
  // Returns
  //  New Date() value of the Saturday of the week corresponding to the argument

  getLastDayOfWeek (eDate) {
    const highestDayIndex_k = 6; // Index value from .getDay() for Saturday

    let newDate = new Date(eDate.getTime())

    // If getDay() returns 0..6 for Sunday..Saturday, then advancing the date
    // by (6-value) leaves the date at Saturday of the week
    newDate.setDate(eDate.getDate() + (highestDayIndex_k - eDate.getDay()));

    return newDate;
  } // Calendar.getLastDayOfWeek

  // ===========================================================================
  // getPeriodByDateAndTime
  //
  // Return the CalendarDayObject and CalendarPeriodObject instances for the
  // Date() object specified in the argument.
  //
  // Arguments:
  //  eDate         The Date() object for the date and time for which the
  //                day and period information is to be returned.
  //
  // Returns
  //  Object describing the match. If the return value is null, there is no
  //  match for this date and time, which can only happen if the eDate argument
  //  is before the school year starts, or after it ends.
  //
  //  If the return value is not null, it is an hash which provides information
  //  about the day and period that matched. The hash has three keys, as follows:
  //
  //    dObj        The CalendarDayObject instance for the day that matched
  //
  //    pObj        The CalendarPeriodObject instance for the period that matched
  //
  //    pIdx        An index to the match. This is only used in subsequent calls
  //                to the getNextPeriod method, and should not be otherwise
  //                interpreted

  getPeriodByDateAndTime (eDate) {

    // Make sure the specified date is in the calendar. Return null if not

    // For the last day check, we need 1ms before midnight on that day, so we
    // have to calculate midnight on the next day and back it up by one.
    let lastDayLastMs = new Date(this.endWEDate);
    this.advanceToFutureDay(lastDayLastMs, 1);
    lastDayLastMs = lastDayLastMs.getTime() - 1;
    if (
      (eDate.getTime() < this._startWEDate.getTime()) ||
      (eDate.getTime() > lastDayLastMs)
    ) {
      return null;
    };

    // Convert the date to a day tag and get the day object for that date
    let dayTag = this.getDayTag(eDate);
    let dayObject = this._dayObjectHash[dayTag];
    CalendarAssert (
      (dayObject !== undefined) && (dayObject.classType === _classTypeDay_k),
      "getPeriodByDateAndTime got back an undefined on the day object for tag",
      dayTag
    );

    CalendarAssert (
      dayObject.periodObjectArray.length !== 0,
      "getPeriodByDateAndTime got back a periodObjectArray with length zero",
      dayTag, dayObject.dayType
    );

    // At this point, we know that the day has periods, so we have to look
    // for a period whose time surrounds that of the one provided in the
    // argument. To do this, calculate the number of ms since midnight in
    // the eDate arg. This will allow delta comparison in the period
    // object.

    for (let index = 0; index < dayObject.periodObjectArray.length; index++) {

      let periodObject = dayObject.periodObjectArray[index];
      // Calculate the absolute start and end times for the period using the
      // base eDate for the day, and the setHours() arguments from the period
      let startTime = new Date(dayObject.eDate.getTime());
      startTime.setHours(...periodObject.startDAdj);
      let endTime = new Date(dayObject.eDate.getTime());
      endTime.setHours(...periodObject.endDAdj);

      // See if the eDate.getTime() value is in the range startTime.getTime()...endTime.getTime()
      if (
        eDate.getTime() >= startTime.getTime() &&
        eDate.getTime() <= endTime.getTime()
      ) {
        return {dObj: dayObject, pObj: periodObject, pIdx: index};
      }
    }

    // If we get here, there was no match, so return null
    return null;

  } // Calendar.getPeriodByDateAndTime

  // ===========================================================================
  // getNextPeriod
  //
  // Return the CalendarDayObject and CalendarPeriodObject instances for the
  // period following the one described by the first argument. This method is
  // intended to be called after getPeriodByDateAndTime to provide a "next up"
  // view of the following period. It can also be called after itself in following
  // a chain of periods.
  //
  // Arguments:
  //  lastMatch     (REQUIRED Object) The return value from getPeriodByDateAndTime
  //                (or getNextPeriod itself)
  //
  //  matchRealPeriod
  //                (OPTIONAL Boolean) True if the next period to return is a
  //                "real" period as opposed to a pseudo-period; false to get
  //                the next period real or not.
  //
  //  matchPeriodsWithClass
  //                (OPTIONAL Boolean) If matchRealPeriod is true, setting this
  //                to true restricts the next period to one with a class. This
  //                can happen if the student has no class for a real period.
  //
  // Returns
  //  Object describing the match. If the return value is null, there is no
  //  match for this date and time, which can only happen if the eDate argument
  //  is at the end of a school year.
  //
  //  If the return value is not null, it is an hash which provides information
  //  about the day and period that matched. The hash has three keys, as follows:
  //
  //    dObj        The CalendarDayObject instance for the day that matched
  //
  //    pObj        The CalendarPeriodObject instance for the period that matched
  //
  //    pIdx        An index to the match. This is only used in subsequent calls
  //                to the getNextPeriod method, and should not be otherwise
  //                interpreted

  getNextPeriod (lastMatch, matchRealPeriod, matchPeriodsWithClass) {
    if (
      (lastMatch === undefined) ||
      (typeof lastMatch !== "object") ||
      (lastMatch.dObj === undefined) ||
      (lastMatch.pObj === undefined) ||
      (lastMatch.pIdx === undefined)
    ) {
      CalendarMessage (
        "***WARNING: Calendar.getNextPeriod called with invalid arguments - returning null",
        lastMatch, matchRealPeriod
      );
      return null;
    };

    // Default the matchRealPeriod and matchPeriodsWithClass values to false if
    // not supplied and make a copy of the lastMatch hash to use as we go
    let matchReal = matchRealPeriod || false;
    let matchClass = matchPeriodsWithClass || false;
    let findMatch = {dObj: lastMatch.dObj, pObj: lastMatch.pObj, pIdx: lastMatch.pIdx+1};

    // Setup a runaway counter to limit the loop below. If we can't find the
    // next period by that many passes through the loop, then it's a code logic
    // bug.
    let runawayCounter = this._dayTagArray.length * 50;

    for (let i = 0; i < runawayCounter; i++) {
      // Use the helper function to see if there is a match in the current day.
      // If so, return that. Note that this is called with lastMatch.pIdx on the
      // first pass, and then it is reset to 0 if we have to advance to the next
      // day to find the next period
      let match = _helperGetNextPeriodThisDay (findMatch, matchReal, matchClass);
      if (match !== null) { return match; } // Found a match for this day

      // No match in the current day, so we have to advance to the next day. We
      // do this by getting the dayIdx of the current day, adding 1 to it, and
      // checking to see if it's off the end of the day tag array. If it is, return
      // null. If not, that's the new day object, and we can call the helper
      // function again, but with pIdx === 0;
      let nextDayIdx = findMatch.dObj.dayIdx + 1;
      if (nextDayIdx >= this._dayTagArray.length) { return null; }
      let dObj = this.getDayByIndex(nextDayIdx);

      findMatch = {dObj: dObj, pObj: null, pIdx: 0};
    } //for (let i = 0; i < runawayCounter; i++)

    CalendarAssert (
      false,
      "getNextPeriod had a runaway loop after not finding a next period"
    );

    // Helper function to find the next period in this day.
    //
    // Arguments:
    //
    //  lastMatch, matchRealPeriod
    //              Same as the arguments to the getNextPeriod method
    //
    // Returns:
    //  Same as the return value from getNextPeriod. However, in this case, a
    //  return value of null means that the next period couldn't be found in
    //  this day, and the caller must advance the day before trying again.

    function _helperGetNextPeriodThisDay (
      lastMatch,
      matchRealPeriod,
      matchPeriodWithClass) {
      let dObj = lastMatch.dObj;
      let pIdx = lastMatch.pIdx;
      let match = null;

      // Starting at the index of the last try, walk through the rest of the
      // period list for this day looking for the next period which, based on the
      // matchRealPeriod argument, can either be ANY period, or the next REAL
      // period
      for (let index = pIdx; index < dObj.periodObjectArray.length; index++) {
        let pObj = dObj.periodObjectArray[index];

        // Skip this one if it's a pseudo period and matchRealPeriod is true or
        // if matchPeriodWithClass is true and there is no class this period
        if (pObj.period < 0) {
          if (matchRealPeriod) { continue };
        } else if (pObj.classInfoObject === null){
          if (matchPeriodWithClass) { continue };
        }

        // Otherwise, we've found a match, so we can stop the loop
        match = {dObj: dObj, pObj: pObj, pIdx: index};
        break;
      }
      return match;

    } // _helperGetNextPeriodThisDay

  } // getNextPeriod

  // ===========================================================================
  // calculateTimeLeft
  //
  // Return an object specifying the time left between the two arguments
  //
  // Arguments:
  //  startTime   Time in milliseconds of the start of the interval
  //
  //  endTime     Time in milliseconds of the end of the interval
  //
  //  noDays        (Optional boolean) If true, hours are increased by (24*days)
  //                days are set to zero. If not supplied, this value defaults to
  //                false
  //
  // Returns:
  //  Object that describes the time between the arguments as four keys:
  //
  //    msTotal   The number of milliseconds in the interval
  //
  //    dDelta    The number of days in the interval
  //
  //    hDelta    The number of hours in the interval
  //
  //    mDelta    The number of minutes in the interval
  //
  //    sDelta    The number of seconds in the interval
  //
  //    toString  The time in the interval as a string in the format "[d] [h]h:mm:ss"
  //
  //  If msTotal is <= 0, there is no time in the interval. toString is just
  //  dDelta, hDelta, mDelta and sDelta passed to the  CalendarHHMMSSAsString
  //  funciton to get a pretty-printed version of the information as a string in
  //  "[d] [h]h:mm:ss" format.

  calculateTimeLeft (startTime, endTime, noDays) {
    const msPerSecond_k = 1000;
    const msPerMinute_k = msPerSecond_k * 60;
    const msPerHour_k = msPerMinute_k * 60;
    const msPerDay_k = msPerHour_k * 24;
    const hoursPerDay_k = 24;

    let suppressDays = noDays || false;

    // Initialize the return value, including the toString function which, when
    // called, pretty-prints the value of d,h,m,s and returns that as a string
    let timeLeft = {
      msTotal: 0,
      dDelta: 0,
      hDelta: 0,
      mDelta: 0,
      sDelta:0,
      toString () {
        let msg = CalendarHHMMSSAsString (
          this.hDelta,
          this.mDelta,
          this.sDelta,
          this.dDelta
        );
        return msg;
      }
    };

    let delta_ms = endTime - startTime;

    // Break this down to the days, hours, minutes and seconds
    if (delta_ms >= 0) {
      timeLeft.msTotal = delta_ms;
      timeLeft.dDelta = Math.floor(delta_ms / msPerDay_k);
      delta_ms -= timeLeft.dDelta * msPerDay_k;
      timeLeft.hDelta = Math.floor(delta_ms / msPerHour_k);
      delta_ms -= timeLeft.hDelta * msPerHour_k;
      timeLeft.mDelta = Math.floor(delta_ms / msPerMinute_k);
      delta_ms -= timeLeft.mDelta * msPerMinute_k
      timeLeft.sDelta = Math.floor(delta_ms / msPerSecond_k);
      if (suppressDays) {
        timeLeft.hDelta += timeLeft.dDelta * hoursPerDay_k;
        timeLeft.dDelta = 0;
      }
    }

    return timeLeft;

  } // calculateTimeLeft

  // ===========================================================================
  // getTimeRemainingInPeriod
  //
  // Return an object specifying the time remaining in the period the arguments
  //
  // Arguments:
  //  eDate       Date() object for the date/time to compare to the period
  //
  //  pObj        CalendarPeriodObject for the period from which an end time
  //              can be extracted
  //
  // Returns:
  //  Object that describes the time left in the period. See calculateTimeLeft
  //  for a description of the object

  getTimeRemainingInPeriod (eDate, pObj) {
    CalendarAssert (
      pObj.classType === _classTypePeriod_k,
      "getTimeRemainingInPeriod called with invalid period object",
      pObj
    );
    // Convert eDate to ms since midnight and the same for the end date of the
    // period and then set the hours, minutes, seconds and milliseconds to get
    // absolute values of the two dates
    // startTime could be later than endTime, but calculateTimeLeft takes care
    // of that case
    let startTime = eDate;
    let endTime = new Date(eDate.getTime());
    endTime.setHours(...pObj.endDAdj);
    endTime.setTime(endTime.getTime()+1)
    return this.calculateTimeLeft (startTime.getTime(), endTime.getTime());

  }

    // ===========================================================================
    // getTimeRemainingUntilPeriod
    //
    // Return an object specifying the time remaining until the start time of
    // the specified period on a day (which may not be today)
    //
    // Arguments:
    //  eDate       Date() object for the date/time to compare to the period
    //
    //  dObj        CalendarPeriodObject for the day that contains the period
    //
    //  pObj        CalendarPeriodObject for the period from which an start time
    //              can be extracted
    //
    //  noDays        (Optional boolean) If true, hours are increased by (24*days)
    //                days are set to zero. If not supplied, this value defaults to
    //                false
    //
    // Returns:
    //  Object that describes the time left in the period. See calculateTimeLeft
    //  for a description of the object

    getTimeRemainingUntilPeriod (eDate, dObj, pObj, noDays) {

      CalendarAssert (
        dObj.classType === _classTypeDay_k,
        "getTimeRemainingUntilPeriod called with invalid day object",
        dObj
      );
      CalendarAssert (
        pObj.classType === _classTypePeriod_k,
        "getTimeRemainingUntilPeriod called with invalid period object",
        pObj
      );

      let suppressDays = noDays || false;

      // The start time is simply the getTime() value for eDate. The end time
      // is the start time of the period on the day specified.  It's possible that
      // startTime could be later than endTime, but calculateTimeLeft takes care
      // of that case
      let startTime = eDate;
      let periodStart = new Date(dObj.eDate.getTime());
      periodStart.setHours(...pObj.startDAdj);
      return this.calculateTimeLeft (startTime.getTime(), periodStart.getTime(), suppressDays);

    }

  // ===========================================================================
  // getVersion
  //
  // Returns the version number of Calendar
  //
  // Arguments:
  //  None
  //
  // Returns:
  //  The version number string

    getVersion () {

      return this.version;

    } // getVersion

  // ===========================================================================
  // toString
  //
  // Returns a multi-line string with the printable information about the
  // day
  //
  // Arguments:
  //  linePrefix  (OPTIONAL String) The prefix to use for each line. If not
  //              specified, defaults to "";
  //
  //  maxDepth    (OPTIONAL Positive Number) Max depth of class toString calls.
  //              If not specified, defaults to _CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this day

  toString (linePrefix, maxDepth) {
    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || _CalendarMaxToStringDepth_k;
    if (myMaxDepth-- <= 0) return "";

    var returnString = "";
    for (let w = 0; w < this._weekTagArray.length; w++) {
      let weekTag = this._weekTagArray[w];
      let weekObject = this._weekObjectHash[weekTag];
      returnString += weekObject.toString(myLinePrefix, myMaxDepth);
    }

    return returnString;

  } // CalendarWeekObject.toString

}  // class Calendar


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// TEST CODE FOR LOCAL TESTING OF THE CLASSES


let calendar;
let version;

// The following block of code is set to run on node.js, but not on the browser,
// and does self-test of the data structures. Before committing a change to
// Calendar.js, please make sure you verify that the code runs, and all tests
// pass.

if (typeof process != "undefined" || _enableTestCodeInBrowser) {

  // Emit error message for a test failure
  function emitError (testNum, message, ...args) {
    let errorString = "** ERROR in test " + testNum + " " + message +
    ":\n\t" + args.join("\n\t");
    throw Error(errorString);
  }

  // Test 1: Validate that the different paths into _weekTagArray result in the same
  // values, and that the array contents match
  function test1(calendar) {
    CalendarMessage ("Test 1: Validate _weekTagArray");
    let weekTagArray = calendar.getWeekTagArray();
    if (weekTagArray.length !== calendar._weekTagArray.length) {
      emitError (1.1, "weekTagArray length test failed", weekTagArray.length, calendar._weekTagArray.length)
      return false;
    }
    for (let i = 0; i < weekTagArray.length; i++) {
      if (weekTagArray[i] !== calendar._weekTagArray[i]) {
        emitError (1.2, "weekTagArray mismatch", i, weekTagArray[i], calendar._weekTagArray[i])
        return false;
      }
    }
    CalendarMessage ("  Test passed");
    return true;
  } // test1

  // Test 2: Validate that the first and last tags in the week tag array correspond
  // to the start and end days of the school year.
  function test2(calendar) {
    CalendarMessage ("Test 2: Validate _weekTagArray school year range");
    let firstWeek = calendar.getDayTag(calendar.getFirstDayOfWeek(calendar.getStartEDate()));
    if (firstWeek !== calendar._weekTagArray[0]) {
      emitError (2.1, "Error in first _weekTagArray entry", firstWeek, calendar._weekTagArray[0]);
      return false;
    }
    let lastWeek = calendar.getDayTag(calendar.getFirstDayOfWeek(calendar.getEndEDate()));
    if (lastWeek !== calendar._weekTagArray[calendar._weekTagArray.length -1]) {
      emitError (2.2, "Error in last _weekTagArray entry", lastWeek, calendar._weekTagArray[calendar._weekTagArray.length-1]);
      return false;
    }
    let expectedEDate = calendar.convertSDateToEDate(calendar._weekTagArray[0]);
    for (let i = 0; i < calendar._weekTagArray.length; i++) {
      let actualEDate = calendar.convertSDateToEDate(calendar._weekTagArray[i]);
      if (actualEDate.getTime() !== expectedEDate.getTime()) {
        emitError (2.3, "weekList date not expected", i, actualEDate, expectedEDate);
        return false;
      }
      calendar.advanceToFutureDay(expectedEDate,7);
    }
    for (let i = 0; i < calendar._weekTagArray.length; i++) {
      let listEDate = calendar.convertSDateToEDate(calendar._weekTagArray[i]);
      let weekObject = calendar.getWeekByTag(calendar._weekTagArray[i]);
      if (listEDate.getTime() != weekObject.eDate.getTime()) {
        emitError (2.4, "week object date does not match expected", i, actualEDate, weekObject.eDate);
        return false;
      }
    }
    CalendarMessage ("_weekTagArray is " + calendar._weekTagArray.length +
    " entries: " + calendar._weekTagArray[0] + "..." + calendar._weekTagArray[calendar._weekTagArray.length-1])

    CalendarMessage ("  Test passed");
    return true;
  } // test2

  // Test 3: Validate that the different paths into _dayTagArray result in the same
  // values, and that the array contents match
  function test3(calendar) {
    CalendarMessage ("Test 3: Validate _dayTagArray");
    let dayTagArray = calendar.getDayTagArray();
    if (dayTagArray.length !== calendar._dayTagArray.length) {
      emitError (3.1, "DayTagArray length test failed", dayTagArray.length, calendar._dayTagArray.length)
      return false;
    }
    for (let i = 0; i < dayTagArray.length; i++) {
      if (dayTagArray[i] !== calendar._dayTagArray[i]) {
        emitError (3.2, "dayTagArray mismatch", i, dayTagArray[i], calendar._dayTagArray[i]);
        return false;
      }
    }
    CalendarMessage ("  Test passed");
    return true;
  } // test3

  // Test 4: Validate that the first and last tags in the day tag array correspond
  // to the start and end days of the school year.
  function test4(calendar) {
    CalendarMessage ("Test 4: Validate _dayTagArray school year range");
    let firstDay = calendar.getDayTag(calendar.getFirstDayOfWeek(calendar.getStartEDate()));
    if (firstDay !== calendar._dayTagArray[0]) {
      emitError (4.1, "Error in first _dayTagArray entry", firstDay, calendar._dayTagArray[0]);
      return false;
    }

    let lastDay = calendar.getDayTag(calendar.getLastDayOfWeek(calendar.getEndEDate()));
    if (lastDay !== calendar._dayTagArray[calendar._dayTagArray.length - 1]) {
      emitError (4.2, "Error in last _dayTagArray entry", lastDay, calendar._dayTagArray[calendar._dayTagArray.length-1]);
      return false;
    }

    let expectedEDate = calendar.convertSDateToEDate(calendar._dayTagArray[0]);
    for (let i = 0; i < calendar._dayTagArray.length; i++) {
      let actualEDate = calendar.convertSDateToEDate(calendar._dayTagArray[i]);
      if (actualEDate.getTime() !== expectedEDate.getTime()) {
        emitError (4.3, "dayTagArray date not expected", i, actualEDate, expectedEDate)
      }
      calendar.advanceToFutureDay(expectedEDate,1);
    }
    for (let i = 0; i < calendar._dayTagArray.length; i++) {
      let listEDate = calendar.convertSDateToEDate(calendar._dayTagArray[i]);
      let dayObject = calendar.getDayByTag(calendar._dayTagArray[i]);
      if (listEDate.getTime() != dayObject.eDate.getTime()) {
        emitError (4.4, "day object date does not match expected", i, calendar._dayTagArray[i], listEDate, dayObject.eDate);
        return false;
      }
    }
    CalendarMessage ("_dayTagArray is " + calendar._dayTagArray.length +
    " entries: " + calendar._dayTagArray[0] + "..." + calendar._dayTagArray[calendar._dayTagArray.length-1])
    CalendarMessage ("  Test passed");
    return true;
  } // test4


  // Test 5: Validate that the lengths of _dayTagArray and _weekTagArray are
  //  consistent.
  function test5(calendar) {
    CalendarMessage ("Test 5: Validate length consistency of _dayTagArray and _weekTagArray");
    let dayList = calendar.getDayTagArray();
    let weekList = calendar.getWeekTagArray();
    if (dayList.length !== weekList.length * 7) {
      emitError(5.1, "Length mismatch between _dayTagArray and _weekTagArray", dayList.length, weekList.length)
    }
    CalendarMessage ("  Test passed");
    return true;
  } // test5

  // Test6: Validate that the eDate in the week object matches the week tag
  function test6(calendar) {
    CalendarMessage ("Test 6: Validate dates in weeks and days");
    for (const weekTag of calendar.getWeekTagArray()) {
      let weekObject = calendar.getWeekByTag(weekTag);
      let expectedEDate = calendar.convertSDateToEDate(weekTag);

      for (let i = 0; i < weekObject.dayObjectArray.length; i++) {
        let dayObject = weekObject.dayObjectArray[i];
        if (dayObject.eDate.getTime() !== expectedEDate.getTime()) {
          emitError (6.1, "Date mismatch between week and day",
          i, dayObject.eDate, expectedEDate);
          return false;
        }
        calendar.advanceToFutureDay(expectedEDate, 1);
      }
    }
    CalendarMessage ("  Test passed");
    return true;
  } // test6

  // Test7: Validate week tags and week index down to day object
  function test7(calendar) {
    CalendarMessage ("Test 7: Validate week tags and week index down to day object");
    let weekTagArray = calendar.getWeekTagArray();
    for (let weekIdx = 0; weekIdx < weekTagArray.length; weekIdx++) {
      let weekTag = weekTagArray[weekIdx];
      let weekObj = calendar.getWeekByTag(weekTag);
      if (weekObj === null) {
        emitError (7.1, "Failed to find week object for weekTag and weekIdx",
        weekTag, weekIdx);
        return false;
      }
      if (weekTag !== weekObj.weekTag) {
        emitError (7.2, "Week tag in week object did not match expected",
        weekTag, weekObj.weekTag);
        return false;
      }
      if (weekIdx !== weekObj.weekIdx) {
        emitError (7.3, "Week index in week object did not match expected",
        weekIdx, weekObj.weekIdx);
        return false;
      }
      for (let di = 0; di < weekObj.dayObjectArray.length; di++) {
        let dayObj = weekObj.dayObjectArray[di];
        if (dayObj.weekTag !== weekTag) {
          emitError (7.4, "weekTag in day object did not match expected",
          weekTag, weekObj.weekTag, di);
          return false;
        }
      } // for (let di = 0; di < WeekObj.dayObjectArray.length; di++)
    } // for (let weekIdx = 0; weekIdx < weekTagArray.length; weekIdx++)
    CalendarMessage ("  Test passed");
    return true;
  } // Test7

  // Test8: Validate day tags and day index down to the day object
  function test8(calendar) {
    CalendarMessage ("Test 8: Validate week tags and week index down to day object");
    let dayTagArray = calendar.getDayTagArray();
    for (let dayIdx = 0; dayIdx < dayTagArray.length; dayIdx++) {
      let dayTag = dayTagArray[dayIdx];
      let dayObj = calendar.getDayByTag(dayTag);
      if (dayObj === null) {
        emitError (8.1, "Failed to find day object for dayTag and dayIdx",
        dayTag, dayIdx);
        return false;
      }
      if (dayTag !== dayObj.dayTag) {
        emitError (8.2, "Day tag in day object did not match expected",
        dayTag, dayObj.dayTag);
        return false;
      }
      if (dayIdx !== dayObj.dayIdx) {
        emitError (8.3, "Day index in day object did not match expected",
        dayIdx, dayObj.dayIdx);
        return false;
      }
    } // for (let dayIdx = 0; dayIdx < dayTagArray.length; dayIdx++)
    CalendarMessage ("  Test passed");
    return true;
  } // Test8

  // Test9: Validate that days and periods cover the entire school year with
  // no gaps and no overlaps
  function test9(calendar) {
    const msPerDay_k = 24*60*60*1000;
    CalendarMessage ("Test 9: Validate that day/period coverage has no gaps and no overlaps ");
    let dayTagArray = calendar.getDayTagArray();
    for (let dayIdx = 0; dayIdx < dayTagArray.length; dayIdx++) {
      let dayTag = dayTagArray[dayIdx];
      let dayObj = calendar.getDayByTag(dayTag);
      let expectedTime = new Date(dayObj.eDate.getTime());
      if (dayObj.periodObjectArray.length === 0) {
        emitError (9.1, "periodObjectArray length is zero",
        dayTag, dayObj.dayType);
        return false;
      }
      for (let pIdx = 0; pIdx < dayObj.periodObjectArray.length; pIdx++) {
        let pObj = dayObj.periodObjectArray[pIdx];
        let startTime = new Date(dayObj.eDate.getTime())
        startTime.setHours(...pObj.startDAdj);
        if (startTime.getTime() !== expectedTime.getTime()) {
          emitError (9.2, "Unexpected startTime for period",
          expectedTime, startTime, dayTag, dayIdx, pIdx,
          pObj.period, pObj.name, pObj.comment);
          return false;
        }
        expectedTime.setHours(...pObj.endDAdj);
        expectedTime.setTime(expectedTime.getTime()+1)
      } // for (pIdx = 0; pIdx < dayObj.periodObjectArray.length; pIdx++)
      let expectedEODTime = new Date(dayObj.eDate.getTime());
      calendar.advanceToFutureDay(expectedEODTime, 1)
      if (expectedTime.getTime() !== expectedEODTime.getTime()) {
        let pIdx = dayObj.periodObjectArray.length-1;
        let pObj = dayObj.periodObjectArray[pIdx];
        emitError (9.3, "Final period did not end at 1ms before midnight",
        expectedTime, expectedEODTime, dayTag, dayIdx, pIdx, pObj.startMSTime, pObj.endMSTime,
        pObj.period, pObj.name, pObj.comment);
        return false;
      }
    } // for (let dayIdx = 0; dayIdx < dayTagArray.length; dayIdx++)
    CalendarMessage ("  Test passed");
    return true;
  } // test9

  // Test10: Validate the getPeriodByDateAndTime method by calling it for the
  // start time and the end times of every period in the school year
  function test10(calendar) {
    CalendarMessage ("Test 10: Validate getPeriodByDateAndTime");
    let dayTagArray = calendar.getDayTagArray();
    for (let dayIdx = 0; dayIdx < dayTagArray.length; dayIdx++) {
      let dayTag = dayTagArray[dayIdx];
      let dayObj = calendar.getDayByTag(dayTag);
      let expectedMSTime = 0;
      for (let pIdx = 0; pIdx < dayObj.periodObjectArray.length; pIdx++) {
        let pObj = dayObj.periodObjectArray[pIdx];
        let startTime = new Date(dayObj.eDate.getTime());
        startTime.setHours(...pObj.startDAdj);
        let endTime = new Date(dayObj.eDate.getTime());
        endTime.setHours(...pObj.endDAdj);

        let match = calendar.getPeriodByDateAndTime(startTime);
        if (
          match === null ||
          match.dObj !== dayObj ||
          match.pObj !== pObj ||
          pIdx !== match.pIdx
        )
        {
          emitError (10.1, "getPeriodByDateAndTime returned wrong value on start time test",
          pIdx, match.pIdx, dayTag,
          match.dObj.toString("",1),
          dayObj.toString("",1),
          match.pObj.toString("",1),
          pObj.toString("",1));
          return false;
        }

        match = calendar.getPeriodByDateAndTime(endTime);
        if (
          match === null ||
          match.dObj !== dayObj ||
          match.pObj !== pObj ||
          pIdx !== match.pIdx
        )
        {
          emitError (10.2, "getPeriodByDateAndTime returned wrong value on end time test",
          pIdx, match.pIdx, dayTag,
          match.dObj.toString("",1),
          dayObj.toString("",1),
          match.pObj.toString("",1),
          pObj.toString("",1));
          return false;
        }
      } // for (pIdx = 0; pIdx < dayObj.periodObjectArray.length; pIdx++)
    } // for (let dayIdx = 0; dayIdx < dayTagArray.length; dayIdx++)
    CalendarMessage ("  Test passed");
    return true;
  } // test10


  // Create new calendar using argument defaults and initialize the weeks, days
  // and periods
  calendar = new Calendar();
  version = calendar.getVersion();
  console.log (`Calendar v${version}, SchoolYearDefinitions v${calendar.schoolVersion}`);

  // Run tests and report results

  test1 (calendar);
  test2 (calendar);
  test3 (calendar);
  test4 (calendar);
  test5 (calendar);
  test6 (calendar);
  test7 (calendar);
  test8 (calendar);
  test9 (calendar);
  test10(calendar);

} // if (typeof process != "undefined" || _enableTestCodeInBrowser)

// END TEST CODE FOR LOCAL TESTING OF THE CLASSES
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// EXAMPLE CODE SHOWING HOW TO INTERACT WITH THIS FILE

// The following block of code shows some examples of how to interface to
// Calendar.js from an application. It should be disabled when run with an application
if (_enableExampleCode) {

  // Create a new calendar and emit the version number
  calendar = new Calendar();
  version = calendar.getVersion();
  console.log (`Calendar v${version}, SchoolYearDefinitions v${calendar.schoolVersion}`);


  // The following variable lets us force a date and time to see the result.
  // If it is set to null, the current date/time is used, which is the normal
  // behavior of the browser application
  // ************************************************
    //let lookupDateTime = "2020-10-19T09:30:00";   //*
    let lookupDateTime = null;                    //*
  // ************************************************

  let eDate;
  if (lookupDateTime === null) {
    eDate = new Date();
  } else {
    eDate = new Date(lookupDateTime);
  }

  console.log ("Looking for the day/period for " + eDate);
  let match = calendar.getPeriodByDateAndTime(eDate);
  _printDayAndPeriodMatch (match);

  // Print time left in period
  let timeLeft = calendar.getTimeRemainingInPeriod(eDate, match.pObj);
  console.log ("Time remaining in the period is " + timeLeft.toString());

  let matchRealPeriod = true;
  let matchPeriodsWithClass = true;

  let countOfNextPeriods = 4;
  console.log("The next " + countOfNextPeriods + " periods are")
  for (let i = 0; i < countOfNextPeriods; i++) {
    match = calendar.getNextPeriod(match, matchRealPeriod, matchPeriodsWithClass);
    _printDayAndPeriodMatch (match);
    if (match !== null) {
      let timeLeft = calendar.getTimeRemainingUntilPeriod(eDate, match.dObj, match.pObj, true);
      console.log ("Time remaining until the start of that period is " + timeLeft.toString())
    }
  }

  // Function to print the match information returned from either
  // getPeriodByDateAndTime or getNextPeriod
  //
  // Arguments:
  //    match       The match hash returned from one of the mentioned calls

  function _printDayAndPeriodMatch (match) {
    if (match === null) {

      // if match is null, then there is no match against the current date/time,
      // which likely means that the date/time is outside the current school year
      console.log ("No match on " + eDate)

    } else {

      // Found a match, extract the day and period objects
      let dObj = match.dObj;
      let pObj = match.pObj;

      // Print day information
      console.log (`${dObj.dayName}, ${dObj.printDate}, is a ${dObj.dayType}`);

      // Print period information
      console.log (`Period ${pObj.name} starts at ${pObj.startSTime} and ends at ${pObj.endSTime}`);

      // Print class information
      let cObj = pObj.classInfoObject;
      if (pObj.period >= 0 && cObj !== null) {
        console.log (`The class in this period is ${cObj.className}, taught by ${cObj.teacher}, in room ${cObj.room}`);
      } else {
        console.log ("There is no class during this period");
      } // if (pOjb.period >= 0) ... else

    }  // if (match === null) ... else ...

  } // _printDayAndPeriodMatch

} // if (_enableExampleCode)

// END EXAMPLE CODE SHOWING HOW TO INTERACT WITH THIS FILE
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
