
// Calendar.js
//
// Calendar-related functions for the MVHS schedule app
//
"use strict";

let CalendarVersion = "1.0.0";

// Revision History
//
//  version    date                     Change
//  ------- ----------  --------------------------------------------------------
//  1.0.0   10/04/2020  First usable release of Calendar.js

// TODO List
//
//  1. Complete the getNextPeriod method. He needs to get both the time remaining
//     in the current period AND the time remaining until the next (real) period
//     starts. Need a method in the period object for isReal();
//
//  2. Either fix the DST bug hack, or convert over to a date library that
//     knows about DST
//
//  3. Figure out a way to move all of the school year definitions out of this
//     file and read it in
//

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
//   The "day index" is the offset in the day tag array of a particular day tag
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
//   tag
//
// Variable/Constant Naming Conventions
//
//   Global variables/constants (and some function variables/contants) have
//   a suffix that identifies their type. These are convention, not enforced
//   by type checking. Such variables/constants end with "_" followed by a
//   single lowercase character, as follows:
//
//       _a    Array object
//       _c    Class object
//       _h    Hash (associative array) object
//       _k    Constant (the c was taken by class)
//       _s    String
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
//   Calendar class.
//
// Other Thoughts
//
//   Consumers of the Calendar are really intended to interact only with the
//   main Calendar class. One instantiates a new Calendar instance with a
//   call to the constructure, e.g., new Calendar (args), which gives you back
//   a populated instance of the class. One can now interact with it using
//   the public methods of the class.
//
//   There are other classes defined in this file for weeks, days, periods, and
//   (school) classes. Instances of these classes are not intended to be
//   created by the consumer of the Calendar class - this is done by the
//   Calendar class constructor. However, these lower-level classes do have
//   methods and public variables that can be accessed when one receives a
//   reference to an instance of one of these classes.
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// MOUNTAIN VIEW HIGH SCHOOL (MVHS) WEEK, DAY, PERIOD AND CLASS DEFINITIONS

const MVHSFirstDay_k = "2020-08-12";
const MVHSLastDay_k  = "2021-06-09";
const MVHSStudent_k = "Jonathan Uhler";

// Define the types of a day
const CalendarDayTypeSchoolDay_k = "School Day";
const CalendarDayTypeWeekend_k = "Weekend";
const CalendarDayTypeHoliday_k = "Holiday"

// Define first and last period
const CalendarPeriodFirst_k = 0;
const CalendarPeriodLast_k = 7;

// =============================================================================
// SCHOOL CLASS DESCRIPTIONS
//
// Each of the following hash definitions provides the information about a
// (school) class for a single period 0..7. The information in each hash
// corresponds to the arguments to the CalendarClassObject constructor.
//
// The keys to the hash identify what information is specified, as follows:
//
//        p = The period number, 0..7
//        c = The name of the (school) class. If this is null, there is no
//            class during that period.
//        r = The room in which the class is held
//        t = The name of the teacher for the class /
//
// The following constants are used throughout, both for readability, and to
// make it possible to change the actual key without having to change every
// use of the key.
const _CCD_KPer = "p";
const _CCD_KCls = "c";
const _CCD_KRmn = "r";
const _CCD_KTch = "t";

const _CalendarClassInfoArray_a = [
//         period                class                          Room                 Teacher
  {[_CCD_KPer]: 0, [_CCD_KCls]:  null,             [_CCD_KRmn]: "",    [_CCD_KTch]: ""            },
  {[_CCD_KPer]: 1, [_CCD_KCls]: "Biology",         [_CCD_KRmn]: "113", [_CCD_KTch]: "Kim Rogers"  },
  {[_CCD_KPer]: 2, [_CCD_KCls]: "PE",              [_CCD_KRmn]: "Gym", [_CCD_KTch]: "Williams"    },
  {[_CCD_KPer]: 3, [_CCD_KCls]: "World Studies",   [_CCD_KRmn]: "602", [_CCD_KTch]: "Cardenas"    },
  {[_CCD_KPer]: 4, [_CCD_KCls]: "Survey Comp/Lit", [_CCD_KRmn]: "215", [_CCD_KTch]: "Engel-Hall"  },
  {[_CCD_KPer]: 5, [_CCD_KCls]: "Geometry",        [_CCD_KRmn]: "412", [_CCD_KTch]: "Smith"       },
  {[_CCD_KPer]: 6, [_CCD_KCls]:  null,             [_CCD_KRmn]: "",    [_CCD_KTch]: ""            },
  {[_CCD_KPer]: 7, [_CCD_KCls]: "IntroCompSci",    [_CCD_KRmn]: "514", [_CCD_KTch]: "Dilloughery" }
];

// END SCHOOL CLASS DESCRIPTIONS
// =============================================================================

// =============================================================================
// PERIOD DESCRIPTIONS
//
// Define the periods for each type of day (see the CalendarDayType*_k above
// for the different types of day).
//
// The _CalendarPeriodDayTypeHash_h constant is a hash of arrays of hashes.
// The top-level hash index is the type of day (CalendarDayType*_k) and
// the value of each of those keys is an array of hashes. Each hash in the
// array proides information for a specific period. The keys to that hash identify
// what information is specified, as follows:
//
//      p  = The period number (-1..7). If this value is -1, it represents a
//           pseudo period that is used to denote a block of time that is not
//           in a real period. The pseudo periods are used for before and after
//           school, passing periods between real periods, and lunch.
//      n  = Name of the period (or pseudo period)
//      st = Start time of the period within the day, in the format hh:mm
//      et = End time of the period within the day, in the format hh:mm
//      c  = A comment about the period. Not used for anything but documentation
//           purposes
//      a  = End time adjustment. To avoid any overlapping time blocks, the
//           end time is backed up by 1ms relative to what was specified in the
//           et key. However, this doesn't work at the end of the day because
//           the end time is specified as 23:59. This key/value tells the
//           code that adjusts the end time to use a different adjustment
//           value for this special case. If the a key/value isn't specified,
//           it defaults to -1
//
// The following constants define the different day types and are the top-level
// index to the _CalendarPeriodDayHash_h hash.
const _CalendarPeriodsForDayType1_k = "PeriodsForADay";
const _CalendarPeriodsForDayType2_k = "PeriodsForBDay";
const _CalendarPeriodsForDayType3_k = "PeriodsForCDay";
const _CalendarPeriodsForWeekend_k = "PeriodsForWeekend";
const _CalendarPeriodsForHoliday_k = "PeriodsForHoliday";

// The following constants are used throughout, both for readability, and to
// make it possible to change the actual key without having to change every
// use of the key.
const _CPD_KPer ="p";
const _CPD_KNam = "n";
const _CPD_KStT = "st";
const _CPD_KEnT = "et";
const _CPD_KCmt = "c";
const _CPD_KAdj = "a"

const _CalendarPeriodDayTypeHash_h = {
  [_CalendarPeriodsForDayType1_k] : [
    // Periods for A Day
    //        period                name                        startTime             endTime                Comment                            Adjustment
    {[_CPD_KPer]: -1, [_CPD_KNam]: "Before School", [_CPD_KStT]: "00:00", [_CPD_KEnT]: "09:30", [_CPD_KCmt]: "Before school on A day"},
    {[_CPD_KPer]:  1, [_CPD_KNam]: "P1",            [_CPD_KStT]: "09:30", [_CPD_KEnT]: "10:45", [_CPD_KCmt]: "Period 1 on A day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "P1->P3",        [_CPD_KStT]: "10:45", [_CPD_KEnT]: "11:00", [_CPD_KCmt]: "Passing Period 1->3 on A day"},
    {[_CPD_KPer]:  3, [_CPD_KNam]: "P3",            [_CPD_KStT]: "11:00", [_CPD_KEnT]: "12:15", [_CPD_KCmt]: "Period 3 on A day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "Lunch",         [_CPD_KStT]: "12:15", [_CPD_KEnT]: "13:05", [_CPD_KCmt]: "Lunch on A day"},
    {[_CPD_KPer]:  5, [_CPD_KNam]: "P5",            [_CPD_KStT]: "13:05", [_CPD_KEnT]: "14:20", [_CPD_KCmt]: "Period 5 on A day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "P5->P7",        [_CPD_KStT]: "14:20", [_CPD_KEnT]: "14:30", [_CPD_KCmt]: "Passing Period 5->7 on A day"},
    {[_CPD_KPer]:  7, [_CPD_KNam]: "P7",            [_CPD_KStT]: "14:30", [_CPD_KEnT]: "15:45", [_CPD_KCmt]: "Period 7 on A day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "After School",  [_CPD_KStT]: "15:45", [_CPD_KEnT]: "23:59", [_CPD_KCmt]: "After school on A day",          [_CPD_KAdj]: 60*1000-1}
    //                                                                                                                                                        ^
    //                                                                                                                                                  This adjustment
    //                                                                                                                                                makes the end time
    //                                                                                                                                                1ms before midnight
  ], // [_CalendarPeriodsForDayType1_k]

  [_CalendarPeriodsForDayType2_k] : [
    // Periods for B Day
    //        period                name                        startTime             endTime                Comment                            Adjustment
    {[_CPD_KPer]: -1, [_CPD_KNam]: "Before School", [_CPD_KStT]: "00:00", [_CPD_KEnT]: "09:30", [_CPD_KCmt]: "Before school on B day"},
    {[_CPD_KPer]:  2, [_CPD_KNam]: "P2",            [_CPD_KStT]: "09:30", [_CPD_KEnT]: "10:45", [_CPD_KCmt]: "Period 2 on B day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "P2->P4",        [_CPD_KStT]: "10:45", [_CPD_KEnT]: "11:00", [_CPD_KCmt]: "Passing Period 2->4 on B day"},
    {[_CPD_KPer]:  4, [_CPD_KNam]: "P4",            [_CPD_KStT]: "11:00", [_CPD_KEnT]: "12:15", [_CPD_KCmt]: "Period 4 on B day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "Lunch",         [_CPD_KStT]: "12:15", [_CPD_KEnT]: "13:05", [_CPD_KCmt]: "Lunch on B day"},
    {[_CPD_KPer]:  6, [_CPD_KNam]: "P6",            [_CPD_KStT]: "13:05", [_CPD_KEnT]: "14:20", [_CPD_KCmt]: "Period 6 on B day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "After School",  [_CPD_KStT]: "14:20", [_CPD_KEnT]: "23:59", [_CPD_KCmt]: "After school on B day",          [_CPD_KAdj]: 60*1000-1}
    //                                                                                                                                                        ^
    //                                                                                                                                                  This adjustment
    //                                                                                                                                                makes the end time
    //                                                                                                                                                1ms before midnight
  ], // [_CalendarPeriodsForDayType2_k]

  // Periods for C Day
  //        period                name                        startTime             endTime                Comment                            Adjustment
  [_CalendarPeriodsForDayType3_k] : [
    {[_CPD_KPer]: -1, [_CPD_KNam]: "Before School", [_CPD_KStT]: "00:00", [_CPD_KEnT]: "09:30", [_CPD_KCmt]: "Before school on C day"},
    {[_CPD_KPer]:  1, [_CPD_KNam]: "P1",            [_CPD_KStT]: "09:30", [_CPD_KEnT]: "10:00", [_CPD_KCmt]: "Period 1 on C day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "P1->P2",        [_CPD_KStT]: "10:00", [_CPD_KEnT]: "10:10", [_CPD_KCmt]: "Passing Period 1->2 on C day"},
    {[_CPD_KPer]:  2, [_CPD_KNam]: "P2",            [_CPD_KStT]: "10:10", [_CPD_KEnT]: "10:40", [_CPD_KCmt]: "Period 2 on C day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "P2->P3",        [_CPD_KStT]: "10:40", [_CPD_KEnT]: "10:50", [_CPD_KCmt]: "Passing Period 2->3 on C day"},
    {[_CPD_KPer]:  3, [_CPD_KNam]: "P3",            [_CPD_KStT]: "10:50", [_CPD_KEnT]: "11:20", [_CPD_KCmt]: "Period 3 on C day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "P3->P4",        [_CPD_KStT]: "11:20", [_CPD_KEnT]: "11:30", [_CPD_KCmt]: "Passing Period 3->4 on C day"},
    {[_CPD_KPer]:  4, [_CPD_KNam]: "P4",            [_CPD_KStT]: "11:30", [_CPD_KEnT]: "12:00", [_CPD_KCmt]: "Period 4 on C day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "Lunch",         [_CPD_KStT]: "12:00", [_CPD_KEnT]: "13:00", [_CPD_KCmt]: "Lunch on C day"},
    {[_CPD_KPer]:  5, [_CPD_KNam]: "P5",            [_CPD_KStT]: "13:00", [_CPD_KEnT]: "13:30", [_CPD_KCmt]: "Period 5 on C day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "P5->P6",        [_CPD_KStT]: "13:30", [_CPD_KEnT]: "13:40", [_CPD_KCmt]: "Passing Period 5->6 on C day"},
    {[_CPD_KPer]:  6, [_CPD_KNam]: "P6",            [_CPD_KStT]: "13:40", [_CPD_KEnT]: "14:10", [_CPD_KCmt]: "Period 6 on C day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "P6->P7",        [_CPD_KStT]: "14:10", [_CPD_KEnT]: "14:20", [_CPD_KCmt]: "Passing Period 6->7 on C day"},
    {[_CPD_KPer]:  7, [_CPD_KNam]: "P7",            [_CPD_KStT]: "14:20", [_CPD_KEnT]: "14:50", [_CPD_KCmt]: "Period 7 on C day"},
    {[_CPD_KPer]: -1, [_CPD_KNam]: "After School",  [_CPD_KStT]: "14:50", [_CPD_KEnT]: "23:59", [_CPD_KCmt]: "After school on C day",          [_CPD_KAdj]: 60*1000-1}
    //                                                                                                                                                        ^
    //                                                                                                                                                  This adjustment
    //                                                                                                                                                makes the end time
    //                                                                                                                                                1ms before midnight
  ], // [_CalendarPeriodsForDayType3_k]

  // Periods for Weekend or _CalendarPeriodsForWeekendOrHoliday_k
    //        period                name                        startTime             endTime                Comment                            Adjustment
    [_CalendarPeriodsForWeekend_k] : [
      {[_CPD_KPer]: -1, [_CPD_KNam]: "Weekend",     [_CPD_KStT]: "00:00", [_CPD_KEnT]: "23:59", [_CPD_KCmt]: "Weekend",                          [_CPD_KAdj]: 60*1000-1}
      //                                                                                                                                                        ^
      //                                                                                                                                                  This adjustment
      //                                                                                                                                                makes the end time
      //                                                                                                                                                1ms before midnight
  ], // [_CalendarPeriodsForWeekend_k]

  // Periods for Weekend or _CalendarPeriodsForWeekendOrHoliday_k
    //        period                name                        startTime             endTime                Comment                            Adjustment
    [_CalendarPeriodsForHoliday_k] : [
      {[_CPD_KPer]: -1, [_CPD_KNam]: "Holiday",     [_CPD_KStT]: "00:00", [_CPD_KEnT]: "23:59", [_CPD_KCmt]: "Holiday",                          [_CPD_KAdj]: 60*1000-1}
      //                                                                                                                                                        ^
      //                                                                                                                                                  This adjustment
      //                                                                                                                                                makes the end time
      //                                                                                                                                                1ms before midnight
  ] // [_CalendarPeriodsHoliday_k]

}; // _CalendarPeriodDayTypeHash_h

// END PERIOD DESCRIPTIONS
// =============================================================================

// =============================================================================
// WEEK DESCRIPTIONS
//
// Define the different types of week during the school year. By default, the
// week is described by the SchoolDefaultWeek_a array. Other arrays are
// defined for each week that is different from the default. The naming of the
// other array denotes the type of week that it is based on the second pattern
// of letters, each of which can be "A", "B", "C" or "H", with the following
// meanings
//
//        A = A School Day
//        B = B School Day
//        C = C School Day
//        H = Some form of holiday
//
// So the _MVHS_ABHAB_Week_a name means that the week has an A day on Monday
// and Thursday, a B Day on Tuesday and Friday, and a holiday on Wednesday.
// Nothing enforces this naming convention, so it's there only to aid understanding
// of the definitions.
//
// The default week is used unless there is a week tag match in the _MVHS_Week_Exceptions_h
// hash (indexed by week tag). The value of that hash is the week array to use
// in place of the default week.
//
// Each week array is exactly 7 elements long, corresponding to the 7 days of
// the week, with the 0th element of the array containing the day description
// for Sunday, 1 for Monday, and 6 for Saturday. Each array element is hash
// which provides information about that day of the week. The keys to the hash
// identify what information is specified, as follows:
//
//        dt = Day type; one of CalendarDayType*_k
//        pa = Period array; an array of periods in that day. See Day Pattern
//             Descriptions, above. If the value for a pa key is [], it means
//             that there are no periods in that day.
//
// The following constants are used throughout, both for readability, and to
// make it possible to change the actual key without having to change every
// use of the key.
const _CWD_KDaT = "dt";
const _CWD_KPeA = "pa"

// This is the default week, which is used for any week that is not in the
// _MVHS_Week_Exceptions_h hash below.
const _MVHS_Default_Week_a = [
    {[_CWD_KDaT]: CalendarDayTypeWeekend_k,      [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType3_k  },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
    {[_CWD_KDaT]: CalendarDayTypeWeekend_k,      [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   }
];

// This is the beginning-of-year week
const _MVHS_HHCAB_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k   },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k   },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType3_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   }
];

// This is a Monday holiday week
const _MVHS_HABAB_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k   },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   }
];

// This is a Monday Staff Dev week
const _MVHS_HBCAB_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k   },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType3_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   }
];

// This is a Wednesday holiday week
const _MVHS_ABHAB_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k   },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   }
];

// This is the end-of-year week
const _MVHS_ABCHH_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType3_k  },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k   },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k   },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k   }
];

// This is a week-long holiday week
const _MVHS_HHHHH_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k  },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k  },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k  },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k  },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k  },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]: _CalendarPeriodsForHoliday_k  },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]: _CalendarPeriodsForWeekend_k  }
];

// This is the list of weeks that are different from the default week. The key
// is the week tag of the exceptional week and the value is the week object
// for that week
const _MVHS_Week_Exceptions_h = {
  "2020-08-09": _MVHS_HHCAB_Week_a,          // Beginning of the school year
  "2020-09-06": _MVHS_HABAB_Week_a,          // Labor Day
  "2020-10-11": _MVHS_HHCAB_Week_a,          // Columbus Day
  "2020-11-08": _MVHS_ABHAB_Week_a,          // Veterans Day
  "2020-11-22": _MVHS_HHHHH_Week_a,          // Thanksgiving Break
  "2020-12-20": _MVHS_HHHHH_Week_a,          // Holiday Break
  "2020-12-27": _MVHS_HHHHH_Week_a,          // Holiday Break
  "2021-01-03": _MVHS_HBCAB_Week_a,          // Staff Dev Day
  "2021-01-17": _MVHS_HBCAB_Week_a,          // Martin Luther King day
  "2021-02-14": _MVHS_HHHHH_Week_a,          // Winter Break
  "2021-03-14": _MVHS_HBCAB_Week_a,          // MVHS Recess
  "2021-04-11": _MVHS_HHHHH_Week_a,          // Spring Recess
  "2021-05-31": _MVHS_HBCAB_Week_a,          // Memorial Day
  "2021-06-06": _MVHS_ABCHH_Week_a,          // End of the school year
};

// END WEEK DESCRIPTIONS
// =============================================================================

// END MVHS WEEK, DAY, PERIOD AND CLASS DEFINITIONS
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// GLOBAL FUNCTIONS
//
// CalendarAssert
//
// Function to test and assertion and an exception, with traceback
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
    let message = "***ERROR: CalendarAssert Assertion Failed: " + msg + ": " +args.join("\n, ")
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

  let message = "Calendar Message: " + msg + ": " +args.join("\n, ")
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

  let mysDate = sDate + "T00:00:00"; // Force parser to set local time
  return new Date(mysDate);

} // function CalendarMidnightOfDate

// ===========================================================================
// CalendarPadStringLeft
//
// Function to return a string, padded to the left to a specified with width
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
// Function to return a string in an [h]h:mm:ss format
//
// Arguments:
//  hours         (Number in the range 0..23) The number of hours
//                padded
//
//  minutes       (Number in the range 0..59) The number of minutes
//
//  seconds       (Number in the range 0..59) The number of seconds
//
// Returns:
//  A string in the format [h]h:mm:ss

function CalendarHHMMSSAsString (hours, minutes, seconds) {

  let hhmmss =
    (hours.toString() + ":" +
    CalendarPadStringLeft(minutes,2,"0") + ":" +
    CalendarPadStringLeft(seconds,2,"0"));
  return hhmmss;

} // function CalendarHHMMSSAsString
// END GLOBAL FUNCTIONS DEFINITIONS
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// GLOBAL CONSTANTS AND VARIABLES
//
// Define the school year first and last days, the default week pattern,
// and the week exception pattern. You'll need these to call the Calendar
// class constructor to get an instance of the Calendar object.
//
const SchoolFirstDay_k        = MVHSFirstDay_k;
const SchoolLastDay_k         = MVHSLastDay_k;
const SchoolDefaultWeek_a     = _MVHS_Default_Week_a;
const SchoolWeekExceptions_h  = _MVHS_Week_Exceptions_h;

// Maximum print depth for calls to class toString methods
const CalendarMaxToStringDepth_k = 10;


// END GLOBAL CONSTANTS AND VARIABLES
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// class CalendarClassObject
//
// This class defines the structure of the object for each (school) class.
//
// Public Class Variables:
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
// Methods:
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
      (period    >= CalendarPeriodFirst_k) && (period         <= CalendarPeriodLast_k) &&
      (className !== undefined)            && ((className     === null) || (typeof className === "string")) &&
      (room      !== undefined)            && (typeof room    === "string") &&
      (teacher   !== undefined)            && (typeof teacher === "string"),
      "CalendarClassObject.constructor called with invalid arguments",
      period, className, room, teacher
    );

    // This information is really just for reporting purposes
    this.period = period;
    this.className = className;
    this.room = room;
    this.teacher = teacher;

    if (_CalendarClassObjectDebug_k) {
      CalendarMessage ("DEBUG: Created new CalendarClassObject instance for period", period);
      CalendarMessage ("DEBUG: ", this.toString("  ", 1));
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
  //              If not specified, defaults to CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this period

  toString (linePrefix, maxDepth) {

    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || CalendarMaxToStringDepth_k;
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
//    period:       (Number) Period number -1..7. If this is -1, it is a pseudo
//                  period, used for before and after school, passing periods
//                  and lunch and there is no classInfoObject.
//
//    name:         (String) Name of the period
//
//    startSTime:   (String) Start time of the period: "hh:mm" using a 24-hour
//                  clock
//
//    startMSTime:  (Number) Milliseconds after midnight for the start time
//
//    endSTime:     (String) End time of the period: "hh:mm" using a 24-hour
//                  clock
//
//    endMSTime:    (Number) Milliseconds after midnight for the end time. This
//                  is actually 1ms less than the end time so that periods don't
//                  overlap. There is a special case for the after school
//                  pseudo period in which this is actually 1ms before
//                  midnight
//
//    comment:      (String) Comment describing the period. May be ""
//
//    classInfoObject:
//                  (CalendarClassObject) Class information for this period. If
//                  there is no class this period, this value is null
//
// Methods:
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
  //  classInfoObject
  //              (REQURIED CalendarClassObject instance) The class information
  //              object corresponding to this period, or null if there is no
  //              class during this period.

  constructor (period, name, startTime, endTime, comment, adjTime, classInfoObject) {

    // Make sure the caller provided the required arguments and argument types
    CalendarAssert (
      (period !== undefined)            && (typeof period    === "number") &&
      ((period === -1) ||
       ((period >= CalendarPeriodFirst_k) && (period <= CalendarPeriodLast_k))) &&
      (name   !== undefined)            && (typeof name      === "string") &&
      (startTime !== undefined)         && (typeof startTime === "string") &&
      (endTime !== undefined)           && (typeof endTime   === "string") &&
      (classInfoObject !== undefined)   && (typeof classInfoObject === "object"),
      "CalendarPeriodObject.constructor called with invalid arguments",
      period, name, startTime, endTime, classInfoObject
    );

    // Make the default end time adjustment -1 ms. If the adjTime argument
    // is specified, use that
    let msAdjust = adjTime || -1;
    this.period = period;
    this.name = name;

    // Save both the string version of start and end times, but also convert
    // them to milliseconds from midnight to make time comparisons easier for
    // users of the data
    let errorString = period + ":" + name + ":" + startTime + ":" + endTime + ":" + comment;
    this.startSTime = startTime;
    this.startMSTime = _helperSTimeToMSTime(startTime, 0, errorString);

    this.endSTime = endTime;
    this.endMSTime = _helperSTimeToMSTime(endTime, msAdjust, errorString);

    // If the period number isn't in the range of the class array, then
    // make classInfoObject null, otherwise extract the class information for this
    // period
    if (classInfoObject === null) {
      this.classInfoObject = null;
    } else {
      this.classInfoObject = classInfoObject;
    }

    // If a comment was not included in the constructor call, make it empty
    this.comment = comment || "";

    if (_CalendarPeriodObjectDebug_k) {
      CalendarMessage ("DEBUG: Created new CalendarPeriodObject instance");
      CalendarMessage ("DEBUG: ", this.toString("  ", 2));
    }

    // Helper function to convert a string in the format "[h]h:[m]m" to
    // the number of milliseconds after midnight, so (h*60+m)*60*1000. Also
    // performs error checking and throws an error if the number is incorrectly
    // formatted
    //
    // Arguments:
    //  sTime       String to convert, in [h]h:[m]m format
    //
    //  adjTime     Number of ms to add to the converted value (typically
    //              provided as a negative number to back the end time up
    //              so that there is no overlap. For example, if -1 is used
    //              then the end time will be 1ms less than the sTime string)
    //
    //  errorString Context string to print if an error is found
    //
    // Returns:
    //  ms after midnight corresponding to the input string

    function _helperSTimeToMSTime (sTime, adjTime, errorString) {
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

      return (((hours * MinutesPerHour_k + minutes) * msPerMinute_k) + adjTime);
    } // CalendarPeriodObject.constructor._helperSTimeToMSTime

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
  //              If not specified, defaults to CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this period

  toString (linePrefix, maxDepth) {

    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || CalendarMaxToStringDepth_k;
    if (myMaxDepth-- <= 0) return "";

    var returnString =
    myLinePrefix + "Period " + this.name;
    if (this.period > 0)  {
      returnString += " (" + this.period + ")";
    }
    myLinePrefix += "  ";
    returnString +=
    myLinePrefix + "starts at " + this.startSTime + " (" + this.startMSTime + ")\n" +
    myLinePrefix + "ends at " + this.endSTime + " (" + this.endMSTime + ")\n";

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
//    dayName:      (String) Name of the day, e.g., "Sunday"
//
//    printDate:    (String) Date in printable format, e.g., "9/30/2020"
//
//    dayType:      (String) Type of day: CalendarDayTypeSchoolDay_k,
//                  CalendarDayTypeWeekend_k, CalendarDayTypeHoliday_k
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
// Methods:
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
  //  dayType     (REQUIRED String) Type of day: one of CalendarDayType*
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

    this.dayTag = dayTag;
    this.dayIdx = dayIdx;
    this.weekTag = weekTag;
    this.dayType = dayType;

    const dayIndexToName_a = [
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
    this.dayName = dayIndexToName_a[this.eDate.getDay()];

    // Build the periodObjectArray
    this.periodObjectArray = [];
    for (let p = 0; p < periodObjectArray.length; p++) {
      this.periodObjectArray.push(periodObjectArray[p]);
    }

    if (_CalendarDayObjectDebug_k) {
      CalendarMessage ("DEBUG: Created new CalendarDayObject instance");
      CalendarMessage ("DEBUG: ", this.toString("  ", 3));
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
  //              If not specified, defaults to CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this day

  toString (linePrefix, maxDepth) {
    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || CalendarMaxToStringDepth_k;
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
// Methods:
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
  //              If not specified, defaults to CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this day

  toString (linePrefix, maxDepth) {
    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || CalendarMaxToStringDepth_k;
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
//    Version       (String) The version number of the Calendar class (and all
//                  other sub-classes) as major.minor.patch.
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
//    _classInfoObjectArray
//                  (Readonly array of CalendarClassObject) An array, indexed by
//                  period, containing the CalendarClassObject instance for each
//                  period
//
//    _periodObjectHash
//                  (Readonly hash of CalendarPeriodObject) A hash, indexed by
//                  The type of school day (see _CalendarPeriodsForDayType*_k) and whose
//                  value is an array of CalendarPeriodObject instances for
//                  each period of that day type. The class information for
//                  real periods will have already been inserted into these
//                  objects
//
// Methods:

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
//    ms = getMsSinceMidnight (eDate)
//                  Returns the number of milliseconds since midnight of the
//                  date/time specified by the eDate argument
//
//    obj = getTimeRemainingInPeriod (eDate, pObj)
//                  Returns an object providing the time remaining in a period
//                  relative to the eDate argument
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
  //  c_startSDate    (OPTIONAL String) Starting date for the new school year
  //                  in the form "yyyy-mm-dd". Defaults to SchoolFirstDay_k
  //                  if not supplied.
  //
  //  c_endSDate      (OPTIONAL String) Ending date for the new school year
  //                  in the form "yyyy-mm-dd". Defaults to SchoolLastDay_k
  //                  if not supplied.
  //
  //  c_defaultWeek   (OPTIONAL Array, length 7, of hashes). The default week
  //                  array, to be used for any week in which there is not an
  //                  exception. Defaults to SchoolDefaultWeek_a if not supplied.
  //                  The length of this array must equal 7, representing the 7
  //                  days in a week. Search for the string
  //
  //                      Calendar.constructor Day Descriptions
  //
  //                  for an explanation of the format of this argument.
  //
  //  c_weekExceptions
  //                  (OPTIONAL hash) A hash that identifies those weeks that
  //                  don't map to the default week, described above. The key
  //                  of the hash is the week tag for the exceptional week and
  //                  the value of the hash is an array in the same format as
  //                  that described for the defaultWeek argument, immediately
  //                  above. Defaults to SchoolWeekExceptions_h if not supplied.
  //
  // Returns:
  //  this

  constructor (c_startSDate, c_endSDate, c_defaultWeek, c_weekExceptions) {
    const daysPerWeek_k = 7;
    const maxDayIdx_k = 365
    const maxWeekIdx_k = 52;

    // Default arguments that are not supplied
    let startSDate = c_startSDate || SchoolFirstDay_k;
    let endSDate = c_endSDate || SchoolLastDay_k;
    let defaultWeek = c_defaultWeek || SchoolDefaultWeek_a;
    let weekExceptions = c_weekExceptions || SchoolWeekExceptions_h;

    // After defaults are applied, make sure the arguments are correct
    CalendarAssert (
      (startSDate     !== undefined)    &&
      (endSDate       !== undefined)    &&
      (defaultWeek    !== undefined)    &&
      (weekExceptions !== undefined)    &&
      (typeof(startSDate) === "string") &&
      (typeof(endSDate)   === "string"),
      "Calendar.constructor called with invalid arguments",
      startSDate, endSDate, defaultWeek
    );
    this.Version = CalendarVersion;

    // Create objects for class information about each period.
    this._classInfoObjectArray = _helperInstantiateClassInfoObjects(_CalendarClassInfoArray_a);
    // Setup for and build period objects for each day type (see _CalendarPeriodsForDayType*_k)
    // Get all of the day types from the hash
    let periodDayTypes = Object.getOwnPropertyNames(_CalendarPeriodDayTypeHash_h);
    this._periodObjectHash = _helperInstantiatePeriodObjects(
      _CalendarPeriodDayTypeHash_h,
      periodDayTypes,
      this._classInfoObjectArray
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

    // Build the remainder of the calendar data structure and setup the
    // class variables _weekTagArray, _weekObjectHash, _dayTagArray and _dayObjectHash, starting
    // with empty values for each
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

      // Determine whether this week should use the default week array, or
      // one from the exceptions hash. The key for the exceptions hash is the
      // week tag, and the value has the same format as the default week array.

      let dayArray;
      if (weekExceptions[weekTag] === undefined) {
        dayArray = [...defaultWeek];
      } else {
        dayArray = [...weekExceptions[weekTag]];
      }
      // dayArray should now be an array of length 7, with each element of the
      // array describing a day of the week, Sunday==0..Saturday==6.
      CalendarAssert (
        dayArray.length === daysPerWeek_k,
        "Calendar.constructor found length error in dayArray",
        dayArray.length, dayArray
      );

      // Loop over each day, create a day object and add it to the day
      // object hash. Also add the day tag to the day tag array
      let dayObjectArray = [];
      for (let d = 0; d < dayArray.length; d++) {
        let dayTag = this.getDayTag(eDate);

        // Extract the day type and the period array from the day array. These
        // are used as args in the creation of the new day object
        let dayType = dayArray[d][ _CWD_KDaT];
        let periodHashName = dayArray[d][_CWD_KPeA];
        // If the periodHashName is null, there are no periods for this day, so
        // make the periodsForThisDay area be empty
        let periodsForThisDay = (periodHashName === null) ? [] : this._periodObjectHash[periodHashName];

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
        );      } // for (let d = 0; d < dayConstructorArgs.length; d++)

        // Put the day object array in the week object
        weekObject.setDayObjectArray (dayObjectArray);

        weekIdx++;
        CalendarAssert (
          weekIdx <= maxWeekIdx_k,
          "Calendar.constructor runaway weekIdx - code logic failure",
          weekIdx
        );

      } // while (eDate.getTime() <= this._endWEDate.getTime())

      return this;

      function _helperInstantiateClassInfoObjects (calendarClassInfoArray) {
        // Loop through _CalendarClassInfoArray_a, create a new object for the
        // class info, and store it in the _classInfoObjectArray based on the
        // period number
        let classInfoObjectArray = [];
        for (const classInfo of calendarClassInfoArray) {
          let period = classInfo[_CCD_KPer];
          CalendarAssert (
            (period >= CalendarPeriodFirst_k) &&
            (period <= CalendarPeriodLast_k),
            "_CalendarClassInfoArray_a contains a period outside of the period bounds",
            period, CalendarPeriodFirst_k, CalendarPeriodLast_k
          );
          let className = classInfo[_CCD_KCls];
          let room = classInfo[_CCD_KRmn];
          let teacher = classInfo[_CCD_KTch];
          let classInfoObject = new CalendarClassObject (period, className, room, teacher);
          classInfoObjectArray[period] = classInfoObject;
        }
        // Now make sure that all entries 0..CalendarPeriodLlast_k either contain
        // a class info object, or are set to null
        for (let p = 0; p <= CalendarPeriodLast_k; p++) {
          if (classInfoObjectArray[p] === undefined) {
            classInfoObjectArray[p] = null;
          }
        }

        return classInfoObjectArray;
      } // function _helperInstantiateClassInfoObjects

      function _helperInstantiatePeriodObjects
      (
        dayTypeHash,
        periodDayTypes,
        classInfoObjectArray
      ) {

        // Start with an empty hash to return to the caller
        let periodObjectHash = Object();

        // periodDayTypes is an array of the day types that will become the
        // keys of the hash
        for (const thisDayType of periodDayTypes) {
          // Initialize the key for the hash, and make the value an empty array
          // into which the CalendarPeriodObject instances will be added
          periodObjectHash[thisDayType] = [];
          // Now loop over each of the elements in the
          for (const singlePeriod of dayTypeHash[thisDayType]) {
            // Extract the information required for the CalendarPeriodObject
            // constructor call.
            let period = singlePeriod[_CPD_KPer];
            let className = singlePeriod[_CPD_KNam];
            let startTime = singlePeriod[_CPD_KStT];
            let endTime = singlePeriod[_CPD_KEnT];
            let comment = singlePeriod[_CPD_KCmt];
            let adjTime = singlePeriod[_CPD_KAdj];
            let classInfoObject = null;
            if ((period >= CalendarPeriodFirst_k) && (period <= CalendarPeriodLast_k)) {
              classInfoObject = classInfoObjectArray[period];
            }

            periodObjectHash[thisDayType].push(
              new CalendarPeriodObject(
                period, className, startTime, endTime, comment, adjTime, classInfoObject
              )
            );
          } // for (const singlePeriod of dayTypeHash[thisDayType])
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
  //  Object containing the matching CalendarDayObject instance (key "dObj"),
  //  the matching CalendarPeriodObject instance (key "pObj"), and the
  //  matching period index (key "pIdx"). The pindex value is used in
  //  conjunction with the getNextPeriod method, below.


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
      dayObject !== undefined,
      "getPeriodByDateAndTime got back an undefined on the _dayObjectHash for tag",
      dayTag
    );

    // If dayObject.periodObjectArray is [], there are no periods that day,
    // so return [dayObject, null, 0] to the caller. This should never happen
    // with the current data structure

    CalendarAssert (
      dayObject.periodObjectArray.length !== 0,
      "getPeriodByDateAndTime got back a periodObjectArray with length zero",
      dayTag, dayObject.dayType
    );
    /*
    if (dayObject.periodObjectArray.length === 0) {
      return {dObj: dayObject, pObj: null, pIdx: 0};
    }
    */

    // At this point, we know that the day has periods, so we have to look
    // for a period whose time surrounds that of the one provided in the
    // argument. To do this, calculate the number of ms since midnight in
    // the eDate arg. This will allow delta comparison in the period
    // object.

    let msSinceMidnight = this.getMsSinceMidnight(eDate);
    for (let index = 0; index < dayObject.periodObjectArray.length; index++) {

      let periodObject = dayObject.periodObjectArray[index];
      if (
        msSinceMidnight >= periodObject.startMSTime &&
        msSinceMidnight <= periodObject.endMSTime
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
  //  lastMatch     The return value from getPeriodByDateAndTime (or getNextPeriod
  //                itself)
  //  matchRealPeriod
  //                True if the next period to return is a "real" period as
  //                opposed to a pseudo-period; false to get the next period
  //                real or not.
  //
  // Returns
  //  Object containing the matching CalendarDayObject instance (key "dObj"),
  //  the matching CalendarPeriodObject instance (key "pObj"), and the
  //  matching period index (key "pIndex"). The pIdx value is used in
  //  conjunction with the getNextPeriod method, below.

  getNextPeriod (lastMatch, matchRealPeriod) {
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

    // Default the matchRealPeriod boolean to false if not supplied
    let matchReal = matchRealPeriod || false;

    // Extract the information from the argument object
    let dayObject = lastMatch.dObj;
    let periodObject = lastMatch.pObj;
    let pIndex = lastMatch.pIdx;


  } // getNextPeriod

  // ===========================================================================
  // getMSSinceMidnight
  //
  // Return the number of milliseconds since midnight of the eDate argument.
  // This is used to do period comparisons on a specific day.
  //
  // Arguments:
  //  eDate         Date() object for the date/time from which to calculate the
  //                number of milliseconds since midnight
  //                itself)
  //
  // Returns
  //  Positive integer corresponding to the number of milliseconds since midnight
  //  for the eDate argument

  getMsSinceMidnight (eDate) {

    let sDate = this.getDayTag(eDate);
    let midnight = CalendarMidnightOfDate(this.getDayTag(eDate));
    return eDate.getTime() - midnight.getTime();

  } // Calendar.getMsSinceMidnight

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
  //  Object the describes the time left in the period as four keys:
  //
  //    msTotal   The number of milliseconds remaining in the period
  //
  //    hDelta    The number of hours remaining in the period
  //
  //    mDelta    The number of minutes remaining in the period
  //
  //    sDelta    The number of seconds remaining in the period
  //
  // If msTotal is <= 0, there is no time left in the period. hDelta, mDelta, and
  // sDelta can be passed to the CalendarHHMMSSAsString function to get a pretty-
  // printed version of the information as a string in [h]h:mm:ss format.

  getTimeRemainingInPeriod (eDate, pObj) {

    const msPerSecond_k = 1000;
    const msPerMinute_k = msPerSecond_k * 60;
    const msPerHour_k = msPerMinute_k * 60;

    // Convert eDate to ms since midnight and the same for the end date of the
    // period
    let eDate_ms = this.getMsSinceMidnight(eDate);
    let periodEnd_ms = pObj.endMSTime + 1;
    let timeLeft = {msTotal: 0, hDelta: 0, mDelta: 0, sDelta:0};
    let delta_ms = periodEnd_ms - eDate_ms;
    if (delta_ms >= 0) {
      timeLeft.msTotal = delta_ms;
      timeLeft.hDelta = Math.floor(delta_ms / msPerHour_k);
      delta_ms -= timeLeft.hDelta * msPerHour_k;
      timeLeft.mDelta = Math.floor(delta_ms / msPerMinute_k);
      delta_ms -= timeLeft.mDelta * msPerMinute_k
      timeLeft.sDelta = Math.floor(delta_ms / msPerSecond_k);
    }
    return timeLeft
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

      return this.Version;

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
  //              If not specified, defaults to CalendarMaxToStringDepth_k.
  //              maxDepth must be > 0 to return a non-"" string and is
  //              decremented before calling the next level down.
  //
  // Returns:
  //  Multi-line string giving all information about this day

  toString (linePrefix, maxDepth) {
    let myLinePrefix = linePrefix || "";
    let myMaxDepth = maxDepth || CalendarMaxToStringDepth_k;
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

const _enableTestCode = false;
const _enableExampleCode = false;

let calendar;
let version;

// The following block of code, if enabled, does self-test of the data structures.
// This code should be enabled and run in a stand-alone environment before
// committing a change to the code. It should be disabled when Calendar.js is
// used with an application.
if (_enableTestCode) {
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
    return true;
  } // test1

  // Test 2: Validate that the first and last tags in the week tag array correspond
  // to the start and end days of the school year.
  function test2(calendar) {
    CalendarMessage ("Test 2: Validate _weekTagArray school year range");
    let firstWeek = calendar.getDayTag(calendar.getFirstDayOfWeek(new Date(SchoolFirstDay_k)));
    if (firstWeek !== calendar._weekTagArray[0]) {
      emitError (2.1, "Error in first _weekTagArray entry", firstWeek, calendar._weekTagArray[0]);
      return false;
    }
    let lastWeek = calendar.getDayTag(calendar.getFirstDayOfWeek(new Date(SchoolLastDay_k)));
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
    return true;
  } // test3

  // Test 4: Validate that the first and last tags in the day tag array correspond
  // to the start and end days of the school year.
  function test4(calendar) {
    CalendarMessage ("Test 4: Validate _dayTagArray school year range");
    let firstDay = calendar.getDayTag(calendar.getFirstDayOfWeek(new Date(SchoolFirstDay_k)));
    if (firstDay !== calendar._dayTagArray[0]) {
      emitError (4.1, "Error in first _dayTagArray entry", firstDay, calendar._dayTagArray[0]);
      return false;
    }

    let lastDay = calendar.getDayTag(calendar.getLastDayOfWeek(new Date(SchoolLastDay_k)));
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
      let expectedMSTime = 0;
      if (dayObj.periodObjectArray.length === 0) {
        emitError (9.1, "periodObjectArray length is zero",
        dayTag, dayObj.dayType);
        return false;
      }
      for (let pIdx = 0; pIdx < dayObj.periodObjectArray.length; pIdx++) {
        let pObj = dayObj.periodObjectArray[pIdx];
        if (pObj.startMSTime !== expectedMSTime) {
          emitError (9.2, "Unexpected startMSTime for period",
          expectedMSTime, dayTag, dayIdx, pIdx, pObj.startMSTime, pObj.endMSTime,
          pObj.period, pObj.name, pObj.comment);
          return false;
        }
        expectedMSTime = pObj.endMSTime + 1;
      } // for (pIdx = 0; pIdx < dayObj.periodObjectArray.length; pIdx++)
      if (expectedMSTime !== msPerDay_k) {
        let pIdx = dayObj.periodObjectArray.length-1;
        let pObj = dayObj.periodObjectArray[pIdx];
        emitError (9.3, "Final period did not end at 1ms before midnight",
        expectedMSTime, msPerDay_k, dayTag, dayIdx, pIdx, pObj.startMSTime, pObj.endMSTime,
        pObj.period, pObj.name, pObj.comment);
        return false;
      }
    } // for (let dayIdx = 0; dayIdx < dayTagArray.length; dayIdx++)
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
          let startMSTime = pObj.startMSTime;
          let endMSTime = pObj.endMSTime;
          let date = dayObj.eDate.getTime() + startMSTime;
          let eDate = new Date(date);

          let match = calendar.getPeriodByDateAndTime(eDate);
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

          date = dayObj.eDate.getTime() + endMSTime;
          eDate = new Date(date);
          if (dayTag === "2021-03-14") continue; //***HACK FOR DST BUG***

          match = calendar.getPeriodByDateAndTime(eDate);
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
      return true;
    } // test9

  // TODO: Additional tests to write
  //
  //  - Find some way to verify getNextPeriod, perhaps by doing something similar
  //    to the previous test

  // Create new calendar using argument defaults and initialize the weeks, days
  // and periods
  calendar = new Calendar();
  version = calendar.getVersion();
  console.log ("Calendar v" + version);

  // Run tests and report results

  if (test1 (calendar)) CalendarMessage ("  Test passed");
  if (test2 (calendar)) CalendarMessage ("  Test passed");
  if (test3 (calendar)) CalendarMessage ("  Test passed");
  if (test4 (calendar)) CalendarMessage ("  Test passed");
  if (test5 (calendar)) CalendarMessage ("  Test passed");
  if (test6 (calendar)) CalendarMessage ("  Test passed");
  if (test7 (calendar)) CalendarMessage ("  Test passed");
  if (test8 (calendar)) CalendarMessage ("  Test passed");
  if (test9 (calendar)) CalendarMessage ("  Test passed");
  if (test10(calendar)) CalendarMessage ("  Test passed");

} // if (_enableTestCode)

// END TEST CODE FOR LOCAL TESTING OF THE CLASSES
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// EXAMPLE CODE SHOWING HOW TO INTERACT WITH THIS FILE

// The following block of code shows some examples of how to interface to
// Calendar.js from an application. It should be disabled when run with an application
if (_enableExampleCode) {
  // If _enableTestCode is false, then we need to call the Calendar constructor
  // here. If it's true, then the calendar variable already holds the instance
  // to the calendar.

  calendar = new Calendar();
  if (!_enableTestCode) {
    version = calendar.getVersion();
    console.log ("Calendar v" + version);
  }

  // The following variable lets us force a date and time to see the result.
  // If it is set to null, the current date/time is used
  // ************************************************
//  let lookupDateTime = "2020-11-01T22:59:59";     //*
  let lookupDateTime = null;                      //*
  // ************************************************

  let eDate;
  if (lookupDateTime === null) {
    eDate = new Date();
  } else {
    eDate = new Date(lookupDateTime);
  }
  console.log ("Looking for the day/period for " + eDate);

  let match = calendar.getPeriodByDateAndTime(eDate);

  if (match === null) {

    // if match is null, then there is no match against the current date/time,
    // which likely means that the date/time is outside the current school year
    console.log ("No match on " + eDate)

  } else if (match.pObj === null) {

    // if match.pObj is null, then there was a day match, but there are no
    // periods for that day, as would be the case on a weekend or a holiday
    let dObj = match.dObj;
    console.log (
      dObj.dayName + ", " +
      dObj.printDate + " is a " +
      dObj.dayType + " and has no periods"
    );

  } else {

    // Found a match on both day and period.
    let dObj = match.dObj;
    let pObj = match.pObj;

    // Print day information
    console.log (
      dObj.dayName + ", " +
      dObj.printDate + " is a " +
      dObj.dayType
    );

    // Print period information
    console.log (
      "Period " + pObj.name +
      " starts at " + pObj.startSTime +
      " and ends at " + pObj.endSTime
    );

    // Print class information
    if (pObj.period >= 0) {

      let cObj = pObj.classInfoObject;
      console.log (
        "The class in this period is " + cObj.className +
        ", taught by " + cObj.teacher  +
        ", in room " + cObj.room
      );

    } else {

      console.log ("There is no class during this period");

    } // if (pOjb.period >= 0) ... else

    // Print time left in period
    let timeLeft = calendar.getTimeRemainingInPeriod(eDate, match.pObj);
    console.log (
      "Time remaining in the period is " +
      CalendarHHMMSSAsString(timeLeft.hDelta, timeLeft.mDelta, timeLeft.sDelta)
    );

  }  // if (match === null) ... else if ... else

} // if (_enableExampleCode)

// END EXAMPLE CODE SHOWING HOW TO INTERACT WITH THIS FILE
// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
