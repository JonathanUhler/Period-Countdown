
 // Calendar.js
 //
 // Calendar-related functions for the MVHS schedule app

"use strict";
const enableDebug = false;
const enableTestCode = true;

const CalendarDayTypeSchoolDay_k = "School Day";
const CalendarDayTypeWeekend_k = "Weekend";
const CalendarDayTypeHoliday_k = "Holiday"

// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class CalendarClassDescr
//
// This class defines the structure of the descriptor for each class,
// which looks like this:
//
//    period:       (Number) Period number
//
//    className:    (String) Name of the class
//
//    room:         (String) Room in which the class is held
//
//    teacher:      (String) Name of the teacher for the class

class CalendarClassDescr {

  // ===========================================================================
  // constructor
  //
  // Initialize the CalendarClassDescr object
  //
  // Arguments:
  //
  //  period      (REQUIRED Number) Period number
  //
  //  className   (REQUIRED String) Name of the class
  //
  //  room:       (REQUIRED String) Room in which the class is held
  //
  //  teacher:    (String) Name of the teacher for the class

  constructor (period, className, room, teacher) {

    // Make sure the caller provided the required arguments
    if ((period === undefined) || (className === undefined) ||
        (room === undefined) || (teacher === undefined)) {
      throw Error ("ERROR: CalendarClassDescr.constructor called with invalid arguments")
    }

    this.period = period;
    this.className = className;
    this.room = room;
    this.teacher = teacher;

  } // constructor

  // ===========================================================================
  // toString
  //
  // Returns a multi-line string with the printable information about the
  // class
  //
  // Arguments:
  //  linePrefix  (OPTIONAL String) The prefix to use for each line
  //
  // Returns:
  //  Multi-line string giving all information about this period

  toString (linePrefix) {

    let myLinePrefix = linePrefix || "";

    // A null in the className field indicates that there is no class in that
    // periods
    if (this.className === null) {
      return myLinePrefix + "There is no class in period " + this.period + "\n"
    }

    // Otherwise, dump the information
    var returnString =
    myLinePrefix + "Class in period " + this.period + " is " + this.className + "\n" +
    myLinePrefix + "taught by " + this.teacher + " in room " + this.room + "\n";

    return returnString;

  } // toString

} // CalendarClassDescr


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class CalendarPeriodDescr
//
// This class defines the structure of the descriptor for each period of the day,
// which looks like this:
//
//    period:       (Number) Period number
//
//    name:         (String) Name of the period
//
//    eDate:        (Date() object) Date at midnight of this period
//
//    startSTime:   (String) Start time of the period: "hh:mm"
//
//    startMSTime:  (Number) Milliseconds after midnight for the start time
//
//    endSTime:     (String) End time of the period: "hh:mm"
//
//    endMSTime:    (Number) Milliseconds after midnight for the end time
//
//    comment:      (String) Comment describing the period
//
//    classInfo:    (CalendarClassDescr) Class information for this period

class CalendarPeriodDescr {

  // ===========================================================================
  // constructor
  //
  // Initialize the CalendarPeriodDescr object
  //
  // Arguments:
  //
  //  period      (REQUIRED Number) Period number
  //
  //  name        (REQUIRED String) Name of the period
  //
  //  startTime   (REQUIRED String) Start time of the period in "hh:mm"
  //
  //  endTime     (REQUIRED String) End time of the period in "hh:mm"
  //
  //  comment     (OPTIONAL String) Comment describing the period

  constructor (period, name, startTime, endTime, comment) {

    // Make sure the caller provided the required arguments
    if ((period === undefined) || (name === undefined) ||
        (startTime === undefined) || (endTime === undefined)) {
      throw Error ("ERROR: CalendarPeriodDescr.constructor called with invalid arguments")
    }

    this.period = period;
    this.name = name;

    // Save both the string version of start and end times, but also convert
    // them to milliseconds from midnight to make time comparisons easier for
    // users of the data

    this.startSTime = startTime;
    this.startMSTime = _helperSTimeToMSTime(startTime,
                period + ":" + name + ":" + startTime + ":" +
                endTime + ":" + comment);
    this.endSTime = endTime;
    this.endMSTime = _helperSTimeToMSTime(endTime,
                period + ":" + name + ":" + startTime + ":" +
                endTime + ":" + comment);

    // If a comment was not included in the constructor call, make it empty
    this.comment = comment || "";

    // Helper function to convert a string in the format "[h]h:[m]m" to
    // the number of milliseconds after midnight, so (h*60+m)*60*1000. Also
    // performs error checking and throws an error if the number is incorrectly
    // formatted
    //
    // Arguments:
    //  sTime       String to convert, in [h]h:[m]m format
    //  errorString Context string to print if an error is found
    //
    // Returns:
    //  ms after midnight corresponding to the input string

    function _helperSTimeToMSTime (sTime, errorString) {
      // Define some useful constants
      const HoursPerDay_k = 24;
      const MinutesPerHour_k = 60;
      const msPerMinute_k = 60*1000;

      // Pattern match the time format. H is returned in [1]; M in [2]
      let timeSplit = sTime.match(/^\s*(\d+)\:(\d+)\s*$/);
      let hours = parseInt(timeSplit[1],10);
      let minutes = parseInt(timeSplit[2],10);

      // Throw an error if the parse failed. This is a bug in the caller of
      // the constructor, so it's OK to give up if an error is found.
      if (isNaN(hours) || isNaN(minutes) ||
          (hours < 0) || (hours >= HoursPerDay_k) ||
          (minutes < 0) || (minutes >= MinutesPerHour_k)) {
            throw Error ("ERROR: CalendarPeriodDecr.constructor found an incorrect formatted time - " + sTime + ", in entry " + errorString);
          }

      return ((hours * MinutesPerHour_k + minutes) * msPerMinute_k);
    } // _helperSTimeToMSTime

  } // CalendarPeriodDescr.constructor

  // ===========================================================================
  // toString
  //
  // Returns a multi-line string with the printable information about the
  // period
  //
  // Arguments:
  //  linePrefix  (OPTIONAL String) The prefix to use for each line
  //
  // Returns:
  //  Multi-line string giving all information about this period

  toString (linePrefix) {

    let myLinePrefix = linePrefix || "";

    var returnString =
    myLinePrefix + "Period " + this.name + " (" + this.period +")\n" +
    myLinePrefix + "on " + this.eDate + "\n" +
    myLinePrefix + "starts at " + this.startSTime + " (" + this.startMSTime + ")\n" +
    myLinePrefix + " and ends at " + this.endSTime + " (" + this.endMSTime + ")\n";
    if (this.comment !== null) {
      returnString +=
      myLinePrefix + "comment: " + this.comment + "\n";
    }

//    returnString += this.classInfo.toString(myLinePrefix);
    return returnString;

  } // CalendarPeriodDescr.toString

  // ===========================================================================
  // setEDate
  //
  // Set the eDate value in the class period descriptor
  //
  // Arguments:
  //  eDate      Date() value (value forced to midnight here)
  //
  // Returns:
  //  None

  setEDate (eDate) {
    // Create a new Date() object, forcing the time to midnight of the day
    this.eDate = new Date(
      eDate.getFullYear(),
      eDate.getMonth(),
      eDate.getDate()
    );
  } // setEDate

} // class CalendarPeriodDescr



// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class CalendarDayDescr
//
// This class defines the structure of the descriptor for each day of the week,
// which looks like this:
//
//    dayName:      (String) Name of the day, e.g., "Sunday"
//
//    printDate:    (String) Date in printable format, e.g., "9/30/2020"
//
//    dayType:      (String) Type of day: CalendarDayTypeSchoolDay_k,
//                  CalendarDayTypeWeekend_k, CalendarDayTypeHoliday_k
//
//    eDate:        (Date() object) Date for this day
//
//    periodList:   (Array of CalendarPeriodDescr) Array containing the
//                  descriptors for each period of the day. If the length
//                  of this array is 0, there are no periods in the day

class CalendarDayDescr {

  // ===========================================================================
  // constructor
  //
  // Initialize the CalendarDayDescr object
  //
  // Arguments:
  //  dayType     (REQUIRED String) Type of day: one of CalendarDayType*
  //
  //  periodList  (REQUIRED Array of CalendarPeriodDescr) List of periods
  //              for this day, or [] if there are no periods
  //
  // NOTE: THE setEDate METHOD MUST BE CALLED TO FINISH INITIALIZING THE
  // DATA STRUCTURE.

  constructor (dayType, periodList) {

    // Make sure the caller provided the required arguments
    if (
      (periodList === undefined) ||
      (dayType === undefined)
    ) {
      throw Error ("ERROR: CalendarDayDescr.constructor called with invalid arguments")
    }

    // the day type
    this.dayType = dayType;

    // and the period list
    this.periodList = [...periodList];

  } // constructor

  // ===========================================================================
  // setEDate
  //
  // Set the eDate, printDate, and dayName value in the class period descriptor
  //
  // Arguments:
  //  sDate      (Required String) The date to use to set the variables
  //
  // Returns:
  //  None

  setEDate (sDate) {

    const dayIndexToName_a = [
          "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        ];

    // Create a new Date() object, forcing the time to midnight of the day
    this.eDate = new Date(sDate + "T00:00:00");

    // Generate a printable date
    let month = this.eDate.getMonth() + 1;
    this.printDate =
    month.toString() + "/" +
    this.eDate.getDate().toString() + "/" +
    this.eDate.getFullYear().toString();

    // Create the printable day name
    this.dayName = dayIndexToName_a[this.eDate.getDay()];

  } // setEDate

  // ===========================================================================
  // toString
  //
  // Returns a multi-line string with the printable information about the
  // day
  //
  // Arguments:
  //  linePrefix  (OPTIONAL String) The prefix to use for each line
  //
  // Returns:
  //  Multi-line string giving all information about this day

  toString (linePrefix) {
    let myLinePrefix = linePrefix || "";

    var returnString =
    myLinePrefix + this.dayName + ", " + this.printDate + " (" + this.eDate + ") " +
    "is a " + this.dayType + " and has the following periods:\n";

    myLinePrefix += "  ";
    if (this.periodList.length == 0) {
      returnString += myLinePrefix + "There are no periods on this day\n";
    } else {
      for (const period of this.periodList) {
        returnString += period.toString(myLinePrefix);
      }
    }

    return returnString;

  } // toString

} // class CalendarDayDescr


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class CalendarWeekDescr
//
// This class defines the structure of the descriptor for a week of classes,
// which looks like this:
//    weekTag:    (String) Date for the Sunday of this week in the format
//                "yyyy-[m]m-[d]d".
//
//    weekNum:    (Positive Integer) Number of week, starting at zero. Can
//                be used to index weekList in the calendar
//
//    eDate       (Date() object) Date corresponding to weekTag
//
//    weekList    (Array) Array 0..6, indexed by day of the week (Sunday=0,
//                Saturday=6) containing the descriptor for each day of that
//                week.
//
class CalendarWeekDescr {

  // constructor
  //
  // Initialize a new calendar week descriptor
  //
  // Arguments:
  //  weekTag     (REQUIRED String) Week tag for the week for this descriptor
  //
  //  weekNum     (Required Number) Week number for this week
  //
  //  weekList    (REQUIRED Array[0..6]) Array of CalendarDayDescr objects for
  //              each day of the week. This may also be null if there are no
  //              school days in the list
  //
  // ===========================================================================
  constructor (weekTag, weekNum, weekList) {

    const daysPerWeek_k = 7;

    if ((weekTag === undefined) || (weekList === undefined) ||
        (weekNum === undefined) ||
        ((weekList !== null) && (weekList.length != daysPerWeek_k))) {
          throw Error ("ERROR: CalendarWeekDescr.constructor called with invalid arguments")
        }

    // Initialize the variables
    this.weekTag = weekTag;
    this.weekNum = weekNum;
    this.eDate = new Date(weekTag + "T00:00:00");
    this.weekList = [...weekList];
  }

// setWeekList
//
// Overwrite the week list array from the constructer call
//
// Arguments:
//
//  weekList    (REQUIRED) Array of day lists for each day of the weeks

  setWeekList (weekList) {
    this.weekList = weekList;
  }

  // ===========================================================================
  // toString
  //
  // Returns a multi-line string with the printable information about the
  // day
  //
  // Arguments:
  //  linePrefix  (OPTIONAL String) The prefix to use for each line
  //
  // Returns:
  //  Multi-line string giving all information about this day

  toString (linePrefix) {
    let myLinePrefix = linePrefix || "";

    var returnString =
    myLinePrefix + "Week " + this.weekNum + " starts on " + this.weekTag +
    " (" + this.eDate + "), and has the following days:\n";
    myLinePrefix += "  ";
    for (const day of this.weekList) {
      returnString += day.toString(myLinePrefix);
    }
    return returnString;

  } // toString
} // class CalendarWeekDescr



// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class Calendar
//
// This class defines the structure of the data structure for the calendar,
// which looks like this:
//
//    _startEDate:  (Date() Object) Midnight of the first day of school
//
//    _startWEDate  (Date() Object) Midnight of the Sunday of the first week of
//                  school (essentiall, _startEDate moved back to the previous
//                  Sunday)
//
//    _endEDate:    (Date() Object) Midnight of the last day of school
//
//    _endWEDate    (Date() Object) Midnight of the Saturday of the last week of
//                  school (essentiall, _endEDate moved forward to the next
//                  Saturday)
//
//    _dayHash      (Hash of CalendarDayDescr) A hash structure, indexed by the
//                  day tag of each day, with the value being the descriptor
//                  for the day.
//
//    _dayList      (Array of String) An array containing the day tags of each
//                  day of the school year, whether an actual school day, a
//                  weekend day, or a holiday. By indexing into this array by
//                  day number, one can extract the day tag (to be used to)
//                  index into _dayHash) for any other day.
//
//    _weekHash     (Hash of CalendarWeekDescr) The calendar data structure,
//                  indexed by week tag of the Sunday of the week, with the
//                  value being the description of that week
//
//    _weekList     (Array of String) An array containing the week tags of
//                  each week of the school year, in order of week. By indexing
//                  into this array by the week number, one can extract the
//                  week tag (to be used to index into _weekHash) for any other
//                  week.



// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=

export default class Calendar {

  // ===========================================================================
  // Constructor
  //
  // Initialize a new Calendar Object
  //
  // Arguments:
  //  startSDate      (REQUIRED String) Starting date for the new school year
  //                  in the form "yyyy-[m]m-[d]d"
  //
  //  endSDate        (REQUIRED String) Ending date for the new school year
  //                  in the form "yyyy-[m]m-[d]d";
  //
  //  defaultWeek     (REQUIRED Array, length 7, of CalendarDayDescr objects)
  //                  The default week list, to be used for any week in which
  //                  there is not an exception
  //
  //  weekExceptions  (REQUIRED hash with the key being the week tag and the
  //                  value being an array, length 7, of CalendarDayDescr objects)
  //                  The exception list for weeks that don't map to the
  //                  default week pattern.
  //
  // Returns:
  //  this

  constructor (startSDate, endSDate, defaultWeek, weekExceptions) {
    const daysPerWeek_k = 7;
    // Make sure the caller provided the required arguments
    if (
          (startSDate === undefined)
      ||  (endSDate === undefined)
      ||  (defaultWeek === undefined)
      ||  (weekExceptions === undefined)
      ||  (typeof(startSDate) != "string")
      ||  (typeof(endSDate) != "string")
//      ||  (!defaultWeek.isArray())
    ) {
      throw Error ("ERROR: Calendar.constructor called with invalid arguments")
    }

    // Force the start and end dates to midnight local time.
    this._startEDate = new Date(startSDate + "T00:00:00");
    this._endEDate = new Date(endSDate + "T00:00:00");
    this._startWEDate = this.getFirstDayOfWeek(this._startEDate);
    this._endWEDate = this.getLastDayOfWeek(this._endEDate);

    // Generate the day and week tag lists for the school year
    [this._dayList, this._weekList] = _helperGenerateWeekList (this, this._startWEDate, this._endWEDate);

    console.log ("_weekList length is " + this._weekList.length);
    console.log ("_dayList length is " + this._dayList.length);


    // Now generate the week hash for each of the week tags
    this._weekHash = new Object();
    let weekNum = 0;
    let weekObject;

    // Loop through all weeks in the school year and build the _weekHash
    // data structure, whose index is the week tag and whose values are the
    // CalendarWeekDescr object for that week
    for (let weekNum = 0; weekNum < this._weekList.length; weekNum++) {
      let weekTag = this._weekList[weekNum];

      // See if the default week or an exception defines the week pattern
      if (weekExceptions[weekTag] === undefined) {
        weekObject = new CalendarWeekDescr(weekTag, weekNum, defaultWeek);
      } else {
        weekObject = new CalendarWeekDescr(weekTag, weekNum, weekExceptions[weekTag]);
      }
      this._weekHash[weekTag] = weekObject;

      // Find the offset of the week tag in the day list. If it can't be found,
      // if the days of the week would wind up outside of the day list array,
      // or if the size of the weekList for this week isn't 7, then that is
      // an internal CalendarPeriodDecr
      let dayOffset = this._dayList.indexOf(weekTag);
      if (dayOffset < 0 ||
          dayOffset + daysPerWeek_k > this._dayList.length ||
          weekObject.weekList.length != daysPerWeek_k) {
        throw Error ("Calendar.constructor found an inconsisent state in " +
                     "searching the day list: " +
                     weekTag + " " +
                     dayOffset + " " +
                     this._dayList.length + " " +
                     this._weekList.length + " " +
                     weekObject.weekList.length)
      }

      // Walk through the list of days in the week and set each date, going from
      //  dayOffset..dayOffset+7
      for (const day of weekObject.weekList) {
        let daySDate = this._dayList[dayOffset];
        day.setEDate(daySDate);
        dayOffset++
      }

    } // for (const weekNum = 0; weekNum < this_.weekList.length; weekNum++)


    return this;


    // Constructor helper functions
    //
    // _helperGenerateWeekList
    //
    // Generate the week tags for each week in the school year and return all
    // tags in an array
    //
    // Arguments:
    //  ctx           "this" isn't available by name in a helper function,
    //                so it has to be passed in
    //
    //  startWEDate   The Date() value of the Sunday before the first day of the
    //                school year
    //
    //  endWEDate     The Date() value of the Saturday after the last day of the
    //                school year
    //
    //
    // Returns:
    //  Two arrays, in this order:
    //    1. An array of the day tags for all days in the school year, including
    //       weekends, holdidays, and the days in the week before the first day
    //       of school and in the week after the last day of school
    //    2. An array of the week tags for all weeks in the school year

    function _helperGenerateWeekList (ctx, startWEDate, endWEDate) {
      const daysPerWeek_k = 7;

      // weekTags will be the array containing the week tags for each week
      // in the school year. Similarly, dayTags will be the array containing
      // the day tags for each day in the school year, including weekens and
      // holidays. Both are returned from this function.
      let weekTags = [];
      let dayTags = [];

      // Loop from the initial value to the point where weekDate is beyond the
      // ending date, call the tag generator for that date and push it onto the end
      // of the array to return
      let weekEDate = new Date(startWEDate.getTime());
      while (weekEDate.getTime() < endWEDate.getTime()) {
        weekTags.push (ctx.getDayTag(weekEDate))
        ctx.advanceToFutureDay(weekEDate, daysPerWeek_k)
      }

      // Now do it again for the day tags
      let dayEDate = new Date(startWEDate.getTime());
      while (dayEDate.getTime() <= endWEDate.getTime()) {
        dayTags.push (ctx.getDayTag(dayEDate))
        ctx.advanceToFutureDay(dayEDate,1);
      }

      // Now return the two arrays of tags
      return [dayTags, weekTags];
    } // Calendar.constructor._helperGenerateWeekList

  } // Calendar.constructor

  // ===========================================================================
  // advanceToFutureDay
  //
  // Advance the argument to the same time on a future day.
  // NOTE: THIS MODIFIES THE ARGUMENT!!!
  //
  // Arguments:
  //  eDate      Date() value for the date to advance
  //
  //  count      Number of days to advance
  //
  // Returns:
  //  No direct return value, but the eDate argument is modified

  advanceToFutureDay (eDate, count) {

    // This is as simple as setting the date of the argument to the current
    // date plus count
    eDate.setDate(eDate.getDate() + count);

  } // Calendar.advanceToNextDay


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
    month.toString().padStart(2,"0") + "-" +
    myEDate.getDate().toString().padStart(2,"0"));

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
  // getWeekByIndex
  //
  // Return the week (CalendarWeekDescr object) for a week based on the week index
  //
  // Arguments:
  //  weekIndex       Week index for which to return the weekly schedule, in
  //                  the range 0.._weekList.length-1
  //
  // Returns
  //  CalendarWeekDescr object for the week, or null if the week doesn't exist.

  getWeekByIndex (weekIndex) {

    // Check for bounds of the argument and return null if the argument is
    // invalid.

    if ((weekTag < 0) ||
    (weekTag >= this._weeklist.length)) {
      return null;
    }

    // Otherwise, let getWeekByTag do the work
    return getWeekByTag (this._weekList[weekIndex]);
  } // Calendar.getWeekByIndex

  // ===========================================================================
  // getWeekByTag
  //
  // Return the week (CalendarWeekDescr object) for a week based on the week tag
  //
  // Arguments:
  //  weekTag       Week tag for which to return the weekly schedule
  //
  // Returns
  //  CalendarWeekDescr object for the week, or null if the week doesn't exist.

  getWeekByTag (weekTag) {

    if (this._weekHash[weekTag] === undefined) {
      return null;
    } else {
      return (this._weekHash[weekTag]);
    }
  } // Calendar.getWeekByTag

  // ===========================================================================
  // getWeekList
  //
  // Return the list of all tags in the calendar
  //
  // Arguments:
  //  None
  //
  // Returns
  //  List of all tags in the calendar

  getWeekList () {
    let weekList = [];             // Create new array to return
    weekList = [...this._weekList]; // copy the values
    return weekList;
  } // Calendar.getWeekList

}  // class Calendar

// Define first and last day of the school year
const MVHSFirstDay_k = "2020-08-12";
const MVHSLastDay_k  = "2021-06-09";

// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SCHOOL CLASS DESCRIPTIONS
//
//// Instantiate class descriptors for each class
//
//                                        Period     class            Room      Teacher
const MVHS_JRU_P0_C =  new CalendarClassDescr(0,   null,               null,   null          );
const MVHS_JRU_P1_C =  new CalendarClassDescr(1,   "Biology",          "113",  "Kim Rogers"  );
const MVHS_JRU_P2_C =  new CalendarClassDescr(2,   "PE",               "Gym",  "Williams"    );
const MVHS_JRU_P3_C =  new CalendarClassDescr(3,   "World Studies",    "602",  "Cardenas"    );
const MVHS_JRU_P4_C =  new CalendarClassDescr(4,   "Survey Comp/Lit",  "215",  "Engel-Hall"  );
const MVHS_JRU_P5_C =  new CalendarClassDescr(5,   "Geometry",         "412",  "Smith"       );
const MVHS_JRU_P6_C =  new CalendarClassDescr(6,   null,               null,   null          );
const MVHS_JRU_P7_C =  new CalendarClassDescr(7,   "IntroCompSci",     "514",  "Dilloughery" );

// END CLASS DESCRIPTIONS
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// PERIOD DESCRIPTIONS
//
// Instantiate period descriptors for periods 1..7 **** FIX FOR PERIOD 0 ****
//
//                                          period  name   startTime  endTime       Comment
const  MVHS_P1A_c =   new CalendarPeriodDescr( 1,   "P1",    "09:30",  "10:45", "Period 1 on A day");
const  MVHS_P1C_c =   new CalendarPeriodDescr( 1,   "P1",    "09:30",  "10:00", "Period 1 on C day");
const  MVHS_P2B_c =   new CalendarPeriodDescr( 2,   "P2",    "09:30",  "10:45", "Period 2 on B day");
const  MVHS_P2C_c =   new CalendarPeriodDescr( 2,   "P2",    "10:10",  "10:40", "Period 2 on C day");
const  MVHS_P3A_c =   new CalendarPeriodDescr( 3,   "P3",    "11:10",  "12:50", "Period 3 on A day");
const  MVHS_P3C_c =   new CalendarPeriodDescr( 3,   "P3",    "10:50",  "11:20", "Period 3 on C day");
const  MVHS_P4B_c =   new CalendarPeriodDescr( 4,   "P4",    "11:00",  "12:15", "Period 4 on B day");
const  MVHS_P4C_c =   new CalendarPeriodDescr( 4,   "P4",    "11:30",  "12:00", "Period 4 on C day");
const  MVHS_P5A_c =   new CalendarPeriodDescr( 5,   "P5",    "13:05",  "14:20", "Period 5 on A day");
const  MVHS_P5C_c =   new CalendarPeriodDescr( 5,   "P5",    "13:00",  "13:30", "Period 5 on C day");
const  MVHS_P6B_c =   new CalendarPeriodDescr( 6,   "P6",    "13:05",  "14:20", "Period 6 on B day");
const  MVHS_P6C_c =   new CalendarPeriodDescr( 6,   "P6",    "13:40",  "14:10", "Period 6 on C day");
const  MVHS_P7A_c =   new CalendarPeriodDescr( 7,   "P7",    "14:30",  "15:45", "Period 7 on A day");
const  MVHS_P7C_c =   new CalendarPeriodDescr( 7,   "P7",    "14:20",  "14:50", "Period 7 on C day");

// END PERIOD DESCRIPTIONS
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// DAY PATTERN DESCRIPTIONS
//
// Type A day, usually Monday and Thursday
const MVHS_A_Day_a = [
  MVHS_P1A_c,
  MVHS_P3A_c,
  MVHS_P5A_c,
  MVHS_P7A_c
];

// Type B day, usually Tuesday and Friday
const MVHS_B_Day_a = [
  MVHS_P2B_c,
  MVHS_P4B_c,
  MVHS_P6B_c
];

// Type C day, usually Wednesday
const MVHS_C_Day_a = [
  MVHS_P1C_c,
  MVHS_P2C_c,
  MVHS_P3C_c,
  MVHS_P4C_c,
  MVHS_P5C_c,
  MVHS_P6C_c,
  MVHS_P7C_c
];

// END DAY PATTERN DESCRIPTIONS
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// WEEK DESCRIPTIONS
//
// This is the default week, which is used for any week that is not in the
// MVHS_Week_Exceptions_h hash below. If a day pattern is [], it means that
// there are no periods on that day
const MVHS_Default_Week_a = [
    new CalendarDayDescr(CalendarDayTypeWeekend_k,   []),
    new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_A_Day_a),
    new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_B_Day_a),
    new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_C_Day_a),
    new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_A_Day_a),
    new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_B_Day_a),
    new CalendarDayDescr(CalendarDayTypeWeekend_k,   [])
];

// This is the beginning-of-year week
const MVHS_HHCAB_Week_a = [
  new CalendarDayDescr(CalendarDayTypeWeekend_k,    []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,    []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,    []),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k,  MVHS_C_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k,  MVHS_A_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k,  MVHS_B_Day_a),
  new CalendarDayDescr(CalendarDayTypeWeekend_k,    [])
];

// This is a Monday holiday week
const MVHS_HABAB_Week_a = [
  new CalendarDayDescr(CalendarDayTypeWeekend_k,    []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,    []),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k,  MVHS_A_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k,  MVHS_B_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k,  MVHS_A_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k,  MVHS_B_Day_a),
  new CalendarDayDescr(CalendarDayTypeWeekend_k,    [])
];

// This is a Monday Staff Dev week
const MVHS_HBCAB_Week_a = [
  new CalendarDayDescr(CalendarDayTypeWeekend_k,   []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,   []),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_B_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_C_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_A_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_B_Day_a),
  new CalendarDayDescr(CalendarDayTypeWeekend_k,   [])
];

// This is a Wednesday holiday week
const MVHS_ABHAB_Week_a = [
  new CalendarDayDescr(CalendarDayTypeWeekend_k,   []),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_A_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_B_Day_a),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,   []),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_A_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_B_Day_a),
  new CalendarDayDescr(CalendarDayTypeWeekend_k,   [])
];

// This is the end-of-year week
const MVHS_ABCHH_Week_a = [
  new CalendarDayDescr(CalendarDayTypeWeekend_k,   []),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_A_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_B_Day_a),
  new CalendarDayDescr(CalendarDayTypeSchoolDay_k, MVHS_C_Day_a),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,   []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,   []),
  new CalendarDayDescr(CalendarDayTypeWeekend_k,   [])
];

// This is a week-long holiday week
const MVHS_HHHHH_Week_a = [
  new CalendarDayDescr(CalendarDayTypeWeekend_k,    []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,    []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,    []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,    []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,    []),
  new CalendarDayDescr(CalendarDayTypeHoliday_k,    []),
  new CalendarDayDescr(CalendarDayTypeWeekend_k,    [])
];

// This is the list of weeks that are different from the default week. The key
// is the week tag of the exceptional week and the value is the week descriptor
// for that week
const MVHS_Week_Exceptions_h = {
  "2020-08-09": MVHS_HHCAB_Week_a,          // Beginning of the school year
  "2020-09-06": MVHS_HABAB_Week_a,          // Labor Day
  "2020-10-11": MVHS_HHCAB_Week_a,          // Columbus Day
  "2020-11-08": MVHS_ABHAB_Week_a,          // Veterans Day
  "2020-11-22": MVHS_HHHHH_Week_a,          // Thanksgiving Break
  "2020-12-20": MVHS_HHHHH_Week_a,          // Holiday Break
  "2020-12-27": MVHS_HHHHH_Week_a,          // Holiday Break
  "2021-01-03": MVHS_HBCAB_Week_a,          // Staff Dev Day
  "2021-01-17": MVHS_HBCAB_Week_a,          // Martin Luther King day
  "2021-02-14": MVHS_HHHHH_Week_a,          // Winter Break
  "2021-03-14": MVHS_HBCAB_Week_a,          // MVHS Recess
  "2021-04-11": MVHS_HHHHH_Week_a,          // Spring Recess
  "2021-05-31": MVHS_HBCAB_Week_a,          // Memorial Day
  "2021-06-06": MVHS_ABCHH_Week_a,          // End of the school year
};

// END WEEK DESCRIPTIONS
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=





// Test code for local testing of the class


if (enableTestCode) {
  // Create new calendar and initialize the weeks, days and periods
  var calendar = new Calendar(
    MVHSFirstDay_k,
    MVHSLastDay_k,
    MVHS_Default_Week_a,
    MVHS_Week_Exceptions_h
  );
//  console.log (calendar._weekHash);

for (let i = 0; i < calendar._weekList.length; i++) {
  let weekTag = calendar._weekList[i];
  console.log ("Processing week " + weekTag)
  let weekHeader = calendar._weekHash[weekTag]
  let message = weekHeader.toString()
  console.log(message)
}
if (false) {
let weekHeader = calendar.getWeekByTag(calendar.getWeekTag(new Date("2020-09-13")));

console.log (calendar._weekList.length);
console.log (calendar._weekList);
console.log (calendar._dayList.length);
console.log (calendar._dayList);
}
if (false) {
let message = "";
message = weekHeader.weekList[0].toString();
console.log (message);
message = weekHeader.toString();
console.log(message);
}
if (false) {

  console.log("Start date is " + calendar.getStartEDate());
  console.log("End date is " + calendar.getEndEDate());
  let weekList = calendar.getWeekList();
  console.log("Tag list for calendar is: [" + weekList + "]");
  let dateTag = calendar.getWeekTag(new Date());
  console.log("Week tag for now is " + dateTag);

  for (const weekTag of weekList) {
    console.log ("Processing header for week " + weekTag)
    let header = calendar.getWeekByTag (weekTag);
    if (header === undefined) {
      console.log ("ERROR: Undefined header for week " + weekTag)
    } else {
      console.log ("Header.weekTag = " + header.weekTag);
      console.log ("Header.eDate = " + header.eDate);
      console.log ("Weekly schedule:");
      let ws = header.weekList;
      for (let day=0; day<7; day++) {
        console.log ("Sched for " + A_DayIndexToName[day] + ": " + ws[day])
      }
    }
  }
}
}
