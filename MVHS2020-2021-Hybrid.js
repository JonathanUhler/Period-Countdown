// MVHS.js
//
// School definitions for Mountain View High School, used by Calendar.js
//
"use strict";

const MVHSVersion = "2.2.2";

// Revision History
//
//  version    date                     Change
//  ------- ----------  --------------------------------------------------------
//  1.0.0   10/08/2020  First usable release of MVHS.js
//
//  1.0.1   10/08/2020  Add a module exports statement for runs using node.js
//                      (not needed in the browser because the html file loads
//                      MVHS.js before Calendar.js)
//
//  1.0.2   10/09/2020  Fix up some comments and documentation
//
//  2.0.0   10/09/2020  Change the _periodDayTypeHash structure for each day
//                      type such that the adjustment field becomes a boolean
//                      that indicates whether a period is the last one of the
//                      day and let Calendar.js decide what to do with that
//                      information. This change requires a corresponding change
//                      to Calendar.js
//
//  2.0.1   10/10/2020  Minor documentation change
//
//  2.1.0   12/14/2020  Add the exception for finals week starting 12/13/2020
//
//  2.1.1   01/03/2021  Fix the weeks of 1/3/2021 and 1/17/2021. They should both
//                      be HABAB weeks
//
//  2.1.2   01/21/2021  Fix the week of 1/17/2021 again. This is an advisory week
//                      and Thurs/Fri of that week have modified schedules.
//
//  2.2.0   01/24/2021  Added support for cookies and the ability to change class
//                      choices
//
//  2.2.1   01/26/2021  Added 'teachersFromCookie' and 'roomsFromCookie' as unused
//                      arrays of data that may be implemented in the future
//
//  2.2.2   4/11/2021   MVHS2020-2021.js deprecated in favor of MVHS2020-2021-Hybrid.js

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
// class SchoolYearDefinitions
//
// This class contains all of the descriptions of the school year required to
// build the data structures in Calendar.js. It contains a number of public
// class variables that are used by Calendar.js, primarily in the Calendar
// class constructor.
//
// Public Class Variables:
//
//    version       (String) Version number of the MVHS.js module
//
//    firstDate     (String) First date of the school year, in yyyy-mm-dd format
//
//    lastDate      (String) Last date of the school year, in yyyy-mm-dd format
//
//    studentName   (String) Name of the student to which this information belongs
//
//    firstPeriod   (Number) First period of the school day
//
//    lastPeriod    (Number) Last period of the school day
//
// Accessor functions to extract information from the data structures
//
//    fieldValue = getClassInfo (idx, field)
//                  Return fields from the _classInfoArray structure
//
//    dayTypeValues = getPeriodDayTypes ()
//                  Return all of the day type keys for the _periodDayTypeHash
//                  structure
//
//    periodCount = getPeriodCount (dayType)
//                  Return the number of periods for a day type key in the
//                  _periodDayTypeHash structure
//
//    fieldValue = getPeriodInfo (dayType, idx, field)
//                  Return fields from the _periodDayTypeHash structure
//
//    fieldValue = getDayInfo (weekTag, dayIdx, field)
//                  Return fields from the week array for the specified week
//                  tag. An example of the week array is _MVHS_Default_Week

class SchoolYearDefinitions {

  // ===========================================================================
  // constructor
  //
  // Initialize the SchoolYearDefinitions class. Essentially, everything is in the
  // constructor for simplicity, but only the public class variables are visible.
  // There are exported accessor functions, but no methods.
  //
  // Arguments:
  //  None

  constructor () {

    // =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
    // MOUNTAIN VIEW HIGH SCHOOL (MVHS) WEEK, DAY, PERIOD AND CLASS DEFINITIONS

    // Export the version number
    this.version = MVHSVersion;

    // Export the day and last day of school
    this.firstDate = "2020-08-12";
    this.lastDate = "2021-06-09";

    // First and last periods of the school day
    const _firstPeriod_k = 0;
    const _lastPeriod_k = 7;
    // Export these
    this.firstPeriod = _firstPeriod_k;
    this.lastPeriod = _lastPeriod_k;

    // Export the student name
    this.studentName = "Student Name"; // This is currently unused, although it 
                                       // may be put into the cookie at some point

    // Define the types of a day
    const _dayTypeSchoolDay_k = "School Day";
    const _dayTypeWeekend_k   = "Weekend";
    const _dayTypeHoliday_k   = "Holiday"

    // =========================================================================
    // SCHOOL CLASS DESCRIPTIONS
    //
    // The following array is indexed by period number (0..7) and provides
    // the details of the class in that period. Each element of the array is a
    // hash that provides the period number (for consistency checking), and the
    // class name, room, and teacher name for the class in that period.
    //
    // The keys to the hash identify what information is specified, as follows:
    //
    //        p:  The period number, 0..7
    //        c:  The name of the (school) class. If this field is null, there
    //            is no class during that period.
    //        r:  The room in which the class is held
    //        t:  The name of the teacher for the class
    //
    // _classInfoArray is a private constant within the class, so information
    // is retrieved via the public accessor function.

    // Read in cookie data
    try {
      var cookieData = document.cookie
      var cookieSections = cookieData.split("=")

      var periodsFromCookie = cookieSections[1].split(",")

      // Account for periods with no classes
      for (var i = 0; i < periodsFromCookie.length; i++) {
        if (periodsFromCookie[i] === "") {
          periodsFromCookie[i] = null
        }
      }

      console.log(periodsFromCookie)
    }
    catch {
      periodsFromCookie = ["None", "None", "None", "None", "None", "None", "None"]
    }

    // Class information array, indexed by period
    const _classInfoArray = [
      // Period Class                    Room                   Teacher
      {p: 0,    c: null,                 r: "",                 t: "" },
      {p: 1,    c: periodsFromCookie[0], r: "",                 t: "" },
      {p: 2,    c: periodsFromCookie[1], r: "",                 t: "" },
      {p: 3,    c: periodsFromCookie[2], r: "",                 t: "" },
      {p: 4,    c: periodsFromCookie[3], r: "",                 t: "" },
      {p: 5,    c: periodsFromCookie[4], r: "",                 t: "" },
      {p: 6,    c: periodsFromCookie[5], r: "",                 t: "" },
      {p: 7,    c: periodsFromCookie[6], r: "",                 t: "" }
    ];

    // -------------------------------------------------------------------------
    // getClassInfo
    //
    // This is the public accessor function for the _classInfoArray data
    //
    // Arguments:
    //  pIdx        (REQUIRED number) The index into the _classInfoArray for the
    //              class period desired
    //
    //  field       (REQUIRED string) The name of the field desired, one of:
    //
    //                "period"  The class period
    //                "class"   The class name. If this value is null, there is
    //                          no class during this period
    //                "room"    The room in which the class is held
    //                "teacher" The name of the teacher for the class
    //
    // Returns:
    //  The desired field if the idx and field arguments are valid for the array
    //  or undefined if there was an error accessing the information.

    this.getClassInfo = function (idx, field) {

      // Return undefined if idx is out of range
      if (idx < _firstPeriod_k || idx > _lastPeriod_k) { return undefined };
      let classInfoElement = _classInfoArray[idx];

      // Force the field name to lower case to eliminate typing errors by the
      // caller and extract and return the requested field
      let lcfield = field.toLowerCase();
      switch (lcfield) {
        case 'period':  return classInfoElement.p; break;
        case 'class':   return classInfoElement.c; break;
        case 'room':    return classInfoElement.r; break;
        case 'teacher': return classInfoElement.t; break;
        default:        return undefined;
      }
    }

    // END SCHOOL CLASS DESCRIPTIONS
    // =========================================================================

    // =========================================================================
    // PERIOD DESCRIPTIONS
    //
    // Define the periods for each type of day (see the _dayType*_k above
    // for the different types of day).
    //
    // The _periodDayTypeHash constant is a hash of arrays of hashes.
    // The top-level hash index is the type of day (_dayType*_k) and
    // the value of each of those keys is an array of hashes. Each hash in the
    // array proides information for a specific period, with the order of the
    // periods being chonological with no gaps or overlaps. The keys to each
    // period hash identify what information is specified, as follows:
    //
    //      p:   The period number (-1..7). If this value is -1, it represents a
    //           pseudo period that is used to denote a block of time that is not
    //           in a real period. The pseudo periods are used for before and after
    //           school, passing periods between real periods, and lunch.
    //      n:   Name of the period (or pseudo period)
    //      st:  Start time of the period within the day, in the format hh:mm
    //      et:  End time of the period within the day, in the format hh:mm
    //      c:   A comment about the period. Not used for anything but documentation
    //           purposes
    //      a:   End of day adjustment. To avoid any overlapping time blocks, the
    //           end time is backed up by 1ms relative to what was specified in the
    //           et key. However, this doesn't work at the end of the day because
    //           the end time is specified as 23:59. This key/value tells the
    //           code that adjusts the end time to use a different adjustment
    //           value for this special case. If the a key/value isn't specified,
    //           it defaults to false
    //
    // _periodDayTypeHash is a private constant within the class, so information
    // is retrieved via the public accessor functions.

    // Create some constants to tie the data structure definitions to the week
    // array usage below.
    const _PeriodsForADay_k     = "PeriodsForADay";
    const _PeriodsForBDay_k     = "PeriodsForBDay";
    const _PeriodsForCDay_k     = "PeriodsForCDay";
    const _PeriodsForAAdvisory_k = "PeriodsForAAdvisory";
    const _PeriodsForBAdvisory_k = "PeriodsForBAdvisory";
    const _PeriodsForFinals1a_k  = "PeriodsForFinals1a"
    const _PeriodsForFinals1b_k  = "PeriodsForFinals1b"
    const _PeriodsForFinals1c_k  = "PeriodsForFinals1c"
    const _PeriodsForFinals1d_k  = "PeriodsForFinals1d"
    const _PeriodsForWeekend_k  = "PeriodsForWeekend";
    const _PeriodsForHoliday_k  = "PeriodsForHoliday";

    // Period descriptions for each type of school day
    const _periodDayTypeHash = {

      [_PeriodsForADay_k]: [
        // Periods for A Day
        //period      name              startTime    endTime        Comment                         eodAdjust
        {p: -1,   n: "Before School", st: "00:00", et: "09:15", c: "Before school on A day"},
        {p:  1,   n: "P1",            st: "09:15", et: "10:30", c: "Period 1 on A day"},
        {p: -1,   n: "P1->P3",        st: "10:30", et: "10:50", c: "Passing Period 1->3 on A day"},
        {p:  3,   n: "P3",            st: "10:50", et: "12:05", c: "Period 3 on A day"},
        {p: -1,   n: "Lunch",         st: "12:05", et: "12:50", c: "Lunch on A day"},
        {p:  5,   n: "P5",            st: "12:50", et: "14:05", c: "Period 5 on A day"},
        {p: -1,   n: "P5->P7",        st: "14:05", et: "14:25", c: "Passing Period 5->7 on A day"},
        {p:  7,   n: "P7",            st: "14:25", et: "15:40", c: "Period 7 on A day"},
        {p: -1,   n: "After School",  st: "15:40", et: "23:59", c: "After school on A day",         a: true}
      ], // [_PeriodsForADay_k]

      [_PeriodsForBDay_k]: [
        // Periods for B Day
        //period      name              startTime    endTime        Comment                         eodAdjust
        {p: -1,   n: "Before School", st: "00:00", et: "09:15", c: "Before school on B day"},
        {p:  2,   n: "P2",            st: "09:15", et: "10:30", c: "Period 2 on B day"},
        {p: -1,   n: "P2->P4",        st: "10:30", et: "10:30", c: "Passing Period 2->4 on B day"},
        {p:  4,   n: "P4",            st: "10:50", et: "12:05", c: "Period 4 on B day"},
        {p: -1,   n: "Lunch",         st: "12:05", et: "12:50", c: "Lunch on B day"},
        {p:  6,   n: "P6",            st: "12:50", et: "14:05", c: "Period 6 on B day"},
        {p: -1,   n: "After School",  st: "14:05", et: "23:59", c: "After school on B day",         a: true}
        ], // [_PeriodsForBDay_k]

      [_PeriodsForCDay_k]: [
        // Periods for C Day
        //period      name              startTime    endTime        Comment                         eodAdjust
        {p: -1,   n: "Before School", st: "00:00", et: "09:30", c: "Before school on C day"},
        {p:  1,   n: "P1",            st: "09:30", et: "10:00", c: "Period 1 on C day"},
        {p: -1,   n: "P1->P2",        st: "10:00", et: "10:10", c: "Passing Period 1->2 on C day"},
        {p:  2,   n: "P2",            st: "10:10", et: "10:40", c: "Period 2 on C day"},
        {p: -1,   n: "P2->P3",        st: "10:40", et: "10:50", c: "Passing Period 2->3 on C day"},
        {p:  3,   n: "P3",            st: "10:50", et: "11:20", c: "Period 3 on C day"},
        {p: -1,   n: "P3->P4",        st: "11:20", et: "11:30", c: "Passing Period 3->4 on C day"},
        {p:  4,   n: "P4",            st: "11:30", et: "12:00", c: "Period 4 on C day"},
        {p: -1,   n: "Lunch",         st: "12:00", et: "13:00", c: "Lunch on C day"},
        {p:  5,   n: "P5",            st: "13:00", et: "13:30", c: "Period 5 on C day"},
        {p: -1,   n: "P5->P6",        st: "13:30", et: "13:40", c: "Passing Period 5->6 on C day"},
        {p:  6,   n: "P6",            st: "13:40", et: "14:10", c: "Period 6 on C day"},
        {p: -1,   n: "P6->P7",        st: "14:10", et: "14:20", c: "Passing Period 6->7 on C day"},
        {p:  7,   n: "P7",            st: "14:20", et: "14:50", c: "Period 7 on C day"},
        {p: -1,   n: "After School",  st: "14:50", et: "23:59", c: "After school on C day",         a: true}
      ], // [_PeriodsForCDay_k]

      [_PeriodsForAAdvisory_k]: [
        // Periods for A Day
        //period      name              startTime    endTime        Comment                                          eodAdjust
        {p: -1,   n: "Before School", st: "00:00", et: "09:30", c: "Before school on A day Advisory schedule"},
        {p:  1,   n: "P1",            st: "09:30", et: "10:40", c: "Period 1 on A day Advisory schedule"},
        {p: -1,   n: "P1->P3",        st: "10:40", et: "10:50", c: "Passing Period 1->3 on A day Advisory schedule"},
        {p:  3,   n: "P3",            st: "10:50", et: "12:20", c: "Period 3 on A day Advisory schedule"},
        {p: -1,   n: "Lunch",         st: "12:20", et: "13:15", c: "Lunch on A day Advisory schedule"},
        {p:  5,   n: "P5",            st: "13:15", et: "14:25", c: "Period 5 on A day Advisory schedule"},
        {p: -1,   n: "P5->P7",        st: "14:25", et: "14:35", c: "Passing Period 5->7 on A day Advisory schedule"},
        {p:  7,   n: "P7",            st: "14:35", et: "15:45", c: "Period 7 on A day Advisory schedule"},
        {p: -1,   n: "After School",  st: "15:45", et: "23:59", c: "After school on A day Advisory schedule",         a: true}
      ], // [_PeriodsForAAdvisory_k]

      [_PeriodsForBAdvisory_k]: [
        // Periods for B Day
        //period      name              startTime    endTime        Comment                                          eodAdjust
        {p: -1,   n: "Before School", st: "00:00", et: "09:30", c: "Before school on B day Advisory schedule"},
        {p:  2,   n: "P2",            st: "09:30", et: "10:40", c: "Period 2 on B day Advisory schedule"},
        {p: -1,   n: "P2->P4",        st: "10:40", et: "10:50", c: "Passing Period 2->4 on B day Advisory schedule"},
        {p:  4,   n: "P4",            st: "10:50", et: "12:20", c: "Period 4 on B day Advisory schedule"},
        {p: -1,   n: "Lunch",         st: "12:20", et: "13:15", c: "Lunch on B day Advisory schedule"},
        {p:  6,   n: "P6",            st: "13:15", et: "14:25", c: "Period 6 on B day Advisory schedule"},
        {p: -1,   n: "After School",  st: "14:25", et: "23:59", c: "After school on B day Advisory schedule",         a: true}
        ], // [_PeriodsForBDay_k]

      [_PeriodsForFinals1a_k]: [
        //period      name              startTime    endTime        Comment                         eodAdjust
        {p: -1,   n: "Before School", st: "00:00", et: "09:30", c: "Before school on Finals1a day"},
        {p:  3,   n: "P3",            st: "09:30", et: "11:15", c: "Period 3 on Finals1a day"},
        {p: -1,   n: "Lunch",         st: "11:15", et: "13:05", c: "Lunch on Finals1a day"},
        {p:  5,   n: "P5",            st: "13:05", et: "14:45", c: "Period 5 on Finals1a day"},
        {p: -1,   n: "After School",  st: "14:45", et: "23:59", c: "After school on Finals1a day",   a: true}
      ], // [_PeriodsForFinals1a_k]

      [_PeriodsForFinals1b_k]: [
        //period      name              startTime    endTime        Comment                         eodAdjust
        {p: -1,   n: "Before School", st: "00:00", et: "09:30", c: "Before school on Finals1b day"},
        {p:  2,   n: "P2",            st: "09:30", et: "11:15", c: "Period 2 on Finals1b day"},
        {p: -1,   n: "Lunch",         st: "11:15", et: "13:05", c: "Lunch on Finals1b day"},
        {p:  7,   n: "P7",            st: "13:05", et: "14:45", c: "Period 7 on Finals1b day"},
        {p: -1,   n: "After School",  st: "14:45", et: "23:59", c: "After school on Finals1b day",   a: true}
      ], // [_PeriodsForFinals1b_k]

      [_PeriodsForFinals1c_k]: [
        //period      name              startTime    endTime        Comment                         eodAdjust
        {p: -1,   n: "Before School", st: "00:00", et: "09:30", c: "Before school on Finals1c day"},
        {p:  1,   n: "P1",            st: "09:30", et: "11:15", c: "Period 1 on Finals1c day"},
        {p: -1,   n: "Lunch",         st: "11:15", et: "13:05", c: "Lunch on Finals1c day"},
        {p:  4,   n: "P4",            st: "13:05", et: "14:45", c: "Period 4 on Finals1c day"},
        {p: -1,   n: "After School",  st: "14:45", et: "23:59", c: "After school on Finals1c day",   a: true}
      ], // [_PeriodsForFinals1c_k]

      [_PeriodsForFinals1d_k]: [
        //period      name              startTime    endTime        Comment                         eodAdjust
        {p: -1,   n: "Before School", st: "00:00", et: "09:30", c: "Before school on Finals1d day"},
        {p:  6,   n: "P6",            st: "09:30", et: "11:15", c: "Period 6 on Finals1d day"},
        {p: -1,   n: "After School",  st: "11:15", et: "23:59", c: "After school on Finals1d day",   a: true}
      ], // [_PeriodsForFinals1d_k]

      [_PeriodsForWeekend_k]: [
        // Periods for Weekend
        //period      name              startTime    endTime        Comment                         eodAdjust
        {p: -1,   n: "Weekend",       st: "00:00", et: "23:59", c: "Weekend",                       a: true}
      ], // [_PeriodsForWeekend_k]

      [_PeriodsForHoliday_k]: [
        // Periods for Weekend
        //period      name              startTime    endTime        Comment                         eodAdjust
        {p: -1,   n: "Holiday",       st: "00:00", et: "23:59", c: "Holiday",                       a: true}
      ] // [_PeriodsForHoliday_k]

    }; // _periodDayTypeHash

    // -------------------------------------------------------------------------
    // getPeriodDayTypes
    //
    // This is the public accessor function to return the day types from the
    // _periodDayTypeHash object
    //
    // Arguments:
    //  None
    //
    // Returns:
    //  Array containing all of the day type keys from _periodDayTypeHash

    this.getPeriodDayTypes = function () {

      // The day types are the own properies of the hash
      let periodDayTypes = Object.getOwnPropertyNames(_periodDayTypeHash);
      return periodDayTypes;

    } // this.getPeriodDayTypes = function ()

    // -------------------------------------------------------------------------
    // getPeriodCount
    //
    // This is the public accessor function to return the number of periods for
    // a specific day type
    //
    // Arguments:
    //  dayType       The day type (top-level) index into _periodDayTypeHash
    //
    // Returns:
    //  The number of periods in that day type

    this.getPeriodCount = function (dayType) {

      // The dayType argument specifies the day type, which is the top-level
      // key into the hash. The value of that is the array containing
      // the hashes for each period, so the length of the array is the
      // period count to return
      let periodsForDay = _periodDayTypeHash[dayType];

      // Check for a bad day type and return undefined if so
      if (periodsForDay === undefined) { return undefined };

      // Otherwise, return the length of the array
      return periodsForDay.length;

    } //this.getPeriodCount = function (dayType)

    // -------------------------------------------------------------------------
    // getPeriodInfo
    //
    // This is the public accessor function to return a field from a period
    // corresponding to the dayType and idx values in the argument
    //
    // Arguments:
    //  dayType     (REQUIRED string) The day type key for the hash
    //
    //  idx         (REQUIRED number) The index into the array selected from
    //              the day type
    //
    //  field       (REQUIRED string) The name of the field desired, one of:
    //
    //                "period"    The class period. If this value is -1, it
    //                            represents a pseudo period, e.g., a passing
    //                            period, in which there is no class
    //
    //                "name"      The name of the period
    //
    //                "starttime" The start time of the period in "hh:mm" format
    //                            representing the time since midnight
    //
    //                "endtime"   The end time of the period in "hh:mm" format
    //                            representing the time since midnight
    //
    //                "comment"   Any comment supplied for the period
    //
    //                "eodadjust" This boolean field specifies whether to special-
    //                            case the end time for this period because it
    //                            is the final period in the day.
    //
    // Returns:
    //  The desired field if all arguments are valid for the hash
    //  or undefined if there was an error accessing the information.

    this.getPeriodInfo = function (dayType, idx, field) {

      // If either the dayType is wrong or the idx value isn't to a valid
      // array entry, return undefined.
      let periodsForDay = _periodDayTypeHash[dayType];
      if (periodsForDay === undefined) { return undefined };
      let periodsForThisDay = periodsForDay[idx]
      if (periodsForThisDay === undefined) { return undefined };

      // Force the field name to lower case to eliminate typing errors by the
      // caller and extract and return the requested field. Note that the
      // eodadj field is optional, so we need to set it to false if it is not
      // supplied
      let lcfield = field.toLowerCase();
      switch (lcfield) {
        case 'period':    return periodsForThisDay.p;  break;
        case 'name':      return periodsForThisDay.n;  break;
        case 'starttime': return periodsForThisDay.st; break;
        case 'endtime':   return periodsForThisDay.et; break;
        case 'comment':   return periodsForThisDay.c;  break;
        case 'eodadjust': return periodsForThisDay.a || false;  break;
        default:          return undefined;

      } // switch (lcfield)
    } // this.getPeriodInfo = function (dayType, idx, field)

    // END PERIOD DESCRIPTIONS
    // =========================================================================

    // =========================================================================
    // WEEK DESCRIPTIONS
    //
    // Define the different types of week during the school year. By default, the
    // week is described by the SchoolDefaultWeek_a array. Other arrays are
    // defined for each week that is different from the default. The naming of the
    // other array denotes the type of week that it is based on the second pattern
    // of letters, each of which can be "A", "B", "C" or "H", with the following
    // meanings
    //
    //        A:  A School Day
    //        B:  B School Day
    //        C:  C School Day
    //        H:  Some form of holiday
    //
    // So the _MVHS_ABHAB_Week_a name means that the week has an A day on Monday
    // and Thursday, a B Day on Tuesday and Friday, and a holiday on Wednesday.
    // Nothing enforces this naming convention, so it's there only to aid
    // understanding of the definitions.
    //
    // The default week is used unless there is a week tag match in the
    // _MVHS_Week_Exceptions hash (indexed by week tag). The value of that hash
    // is the week array to use in place of the default week.
    //
    // Each week array is exactly 7 elements long, corresponding to the 7 days of
    // the week, with the 0th element of the array containing the day description
    // for Sunday, 1 for Monday, and 6 for Saturday. Each array element is a hash
    // which provides information about that day of the week. The keys to the hash
    // identify what information is specified, as follows:
    //
    //        dt:  Day type; one of _dayType*_k
    //        pa:  Period array; an array of periods in that day. See Day Pattern
    //             Descriptions, above.

    // This is the default week, which is used for any week that is not in the
    // _MVHS_Week_Exceptions hash below.
    const _MVHS_Default_Week = [
      {dt: _dayTypeWeekend_k,      pa: _PeriodsForWeekend_k },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForBDay_k    },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForCDay_k    },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForBDay_k    },
      {dt: _dayTypeWeekend_k,      pa: _PeriodsForWeekend_k }
    ];

    // This is the HABAB modified for Advisory week
    const _MVHS_AdvisoryHABAB_Week = [
      {dt: _dayTypeWeekend_k,      pa: _PeriodsForWeekend_k  },
      {dt: _dayTypeHoliday_k,      pa: _PeriodsForHoliday_k  },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForADay_k     },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForBDay_k     },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForAAdvisory_k},
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForBAdvisory_k},
      {dt: _dayTypeWeekend_k,      pa: _PeriodsForWeekend_k  }
    ];

    // This is the ACBAB week for Junior PSAT week
    const _MVHS_ACBAB_Week = [
      {dt: _dayTypeWeekend_k,      pa: _PeriodsForWeekend_k },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForCDay_k    },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForBDay_k    },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,    pa: _PeriodsForBDay_k    },
      {dt: _dayTypeWeekend_k,      pa: _PeriodsForWeekend_k }
    ];

    // This is the beginning-of-year week
    const _MVHS_HHCAB_Week = [
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForCDay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForBDay_k    },
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k }
    ];

    // This is a Monday holiday week
    const _MVHS_HABAB_Week = [
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForBDay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForBDay_k    },
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k }
    ];

    // This is a Monday Staff Dev week
    const _MVHS_HBCAB_Week = [
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForBDay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForCDay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForBDay_k    },
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k }
    ];

    // This is a Wednesday holiday week
    const _MVHS_ABHAB_Week = [
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForBDay_k    },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForBDay_k    },
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k }
    ];

    // This is the end-of-year week
    const _MVHS_ABCHH_Week = [
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForADay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForBDay_k    },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForCDay_k    },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k }
    ];

    // This is a week-long holiday week
    const _MVHS_HHHHH_Week = [
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeHoliday_k,        pa: _PeriodsForHoliday_k },
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k }
    ];

    // This is a Finals 1 week
    const _MVHS_Finals1_Week = [
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForCDay_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForFinals1a_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForFinals1b_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForFinals1c_k },
      {dt: _dayTypeSchoolDay_k,      pa: _PeriodsForFinals1d_k },
      {dt: _dayTypeWeekend_k,        pa: _PeriodsForWeekend_k }
    ];
    // This is the list of weeks that are different from the default week. The key
    // is the week tag of the exceptional week and the value is the _MVHS_*_Week
    // array for that week
    const _MVHS_Week_Exceptions = {
      "2020-08-09": _MVHS_HHCAB_Week,          // Beginning of the school year
      "2020-09-06": _MVHS_HABAB_Week,          // Labor Day
      "2020-10-11": _MVHS_HHCAB_Week,          // Columbus Day
      "2020-11-08": _MVHS_ABHAB_Week,          // Veterans Day
      "2020-11-22": _MVHS_HHHHH_Week,          // Thanksgiving Break
      "2020-12-13": _MVHS_Finals1_Week,        // Finals 1 week
      "2020-12-20": _MVHS_HHHHH_Week,          // Holiday Break
      "2020-12-27": _MVHS_HHHHH_Week,          // Holiday Break
      "2021-01-03": _MVHS_HABAB_Week,          // Staff Dev Day
      "2021-01-17": _MVHS_AdvisoryHABAB_Week,  // Martin Luther King day
      "2021-01-24": _MVHS_ACBAB_Week,          // Junior PSAT Week
      "2021-02-14": _MVHS_HHHHH_Week,          // Winter Break
      "2021-03-14": _MVHS_HABAB_Week,          // MVHS Recess
      "2021-04-11": _MVHS_HHHHH_Week,          // Spring Recess
      "2021-04-25": _MVHS_ACBAB_Week,          // SAT Day
      "2021-05-31": _MVHS_HABAB_Week,          // Memorial Day
      "2021-06-06": _MVHS_ABCHH_Week,          // End of the school year
    };

    // -------------------------------------------------------------------------
    // getDayInfo
    //
    // This is the public accessor function to return a field from a day
    // corresponding to the weekTag, dayIdx, and field values in the argument
    //
    // Arguments:
    //  weekTag     (REQUIRED string) The week tag for the week in question
    //
    //  dayIdx      (REQUIRED number) The day index into the week (Sunday = 0 ...
    //              Saturday = 6)
    //
    //  field       (REQUIRED string) The name of the field desired, one of:
    //
    //                "daytype"       The day type for the day
    //
    //                "periodidx"     The index into _periodDayTypeHash for the day
    //
    // Returns:
    //  The desired field if all arguments are valid for the hash
    //  or undefined if there was an error accessing the information.

    this.getDayInfo = function (weekTag, dayIdx, field) {

      // Assume the default week
      let weekArray = _MVHS_Default_Week;
      // If the exception hash has a match for the week tag, that over-rides
      // the default
      if (_MVHS_Week_Exceptions[weekTag] !== undefined) {
        weekArray = _MVHS_Week_Exceptions[weekTag];
      }
      // dayHash is the information about the day
      let dayHash = weekArray[dayIdx];

      // Force the field name to lower case to eliminate typing errors by the
      // caller and extract and return the requested field
      let lcfield = field.toLowerCase();
      switch (lcfield) {
        case 'daytype':   return dayHash.dt;  break;
        case 'periodidx': return dayHash.pa;  break;
        default:          return undefined;

      } // switch (lcfield)
      return _MVHS_Week_Exceptions;
    }

    // END WEEK DESCRIPTIONS
    // =============================================================================

    // END MVHS WEEK, DAY, PERIOD AND CLASS DEFINITIONS
    // =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
  } // SchoolYearDefinitions.constructor

} // class SchoolYearDefinitions

// For running on node.js, export the class definition so that it can be used
// in Calendar.js via a require statement. Note that this isn't necessary for
// runs in a browser because the html file loads MVHS.js before Calendar.js
if (typeof process !== "undefined") {
  module.exports = SchoolYearDefinitions;
}
