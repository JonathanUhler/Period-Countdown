
// Calendar.js
//
// Calendar-related functions for the MVHS schedule app
"use strict";


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
//   the form "yyyy-mm-dd"
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
//   Calendar class constructure. However, these lower-level classes do have
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
// IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT
//
// CalendarClassObject.constructor arguments
//
// Each of the following hash definitions provides the information about a
// (school) class for a single period 0..7. The information in each hash
// corresponds to the arguments to the CalendarClassObject constructor.
//
// The keys to the hash identify what information is specified, as follows:
//
//        p = The period number, 0..7
//        c = The name of the (school) class
//        r = The room in which the class is held
//        t = The name of the teacher for the class /
//
const _CCD_KPer = "p";
const _CCD_KCls = "c";
const _CCD_KRmn = "r";
const _CCD_KTch = "t";
//
/*
//                      Period     class               Room        Teacher
const _MVHS_P0_CLASS_h = {p: 0,   c:  null,              r: "",     t:  null         };
const _MVHS_P1_CLASS_h = {p: 1,   c: "Biology",          r: "113",  t: "Kim Rogers"  };
const _MVHS_P2_CLASS_h = {p: 2,   c: "PE",               r: "Gym",  t: "Williams"    };
const _MVHS_P3_CLASS_h = {p: 3,   c: "World Studies",    r: "602",  t: "Cardenas"    };
const _MVHS_P4_CLASS_h = {p: 4,   c: "Survey Comp/Lit",  r: "215",  t: "Engel-Hall"  };
const _MVHS_P5_CLASS_h = {p: 5,   c: "Geometry",         r: "412",  t: "Smith"       };
const _MVHS_P6_CLASS_h = {p: 6,   c:  null,              r: "",     t: ""};
const _MVHS_P7_CLASS_h = {p: 7,   c: "IntroCompSci",     r: "514",  t: "Dilloughery" };
*/

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
// Instantiate period hashes for periods 1..7 **** FIX FOR PERIOD 0 ****
//
// The following constants define the periods for each day. If the Class Info
// element of the array is null, there is no class for this period. A period
// number of -1 is used to denote a pseudo period for periods that are
// before or after school, passing periods, and lunch.
//
// IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT
// The order of values in each array exactly match the arguments to the
// CalendarPeriodObject constructor, which is called during the calendar creation
// process. If the argument order or definition of that constructor changes, so
// must these array definitions.
//
// CalendarPeriodObject.constructor arguments
/*
//
// Periods for A Day
//                     period    name           startTime  endTime   Class Info               Comment                     Adjustment
const  _MVHS_BSA_a =     [-1,   "Before School",  "00:00",  "09:30", null,                "Before school on A day"];
const  _MVHS_P1A_a =     [ 1,   "P1",             "09:30",  "10:45", _MVHS_P1_CLASS_a,     "Period 1 on A day"];
const  _MVHS_P13A_a =    [-1,   "P1->P3",         "10:45",  "11:00", null,                "Passing Period 1->3 on A day"];
const  _MVHS_P3A_a =     [ 3,   "P3",             "11:00",  "12:15", _MVHS_P3_CLASS_a,     "Period 3 on A day"];
const  _MVHS_LunchA_a =  [-1,   "Lunch",          "12:15",  "13:05", null,                "Lunch on A day"];
const  _MVHS_P5A_a =     [ 5,   "P5",             "13:05",  "14:20", _MVHS_P5_CLASS_a,     "Period 5 on A day"];
const  _MVHS_P57A_a =    [-1,   "P5->P7",         "14:20",  "14:30", null,                "Passing Period 5->7 on A day"];
const  _MVHS_P7A_a =     [ 7,   "P7",             "14:30",  "15:45", _MVHS_P7_CLASS_a,     "Period 7 on A day"];
const  _MVHS_ASA_a =     [-1,   "After School",   "15:45",  "23:59", null,                "After school on A day",         60*1000-1];
//                                                                                                                           ^
//                                                                                                                    This adjustment
//                                                                                                                    makes the end time
//                                                                                                                    1ms before midnight
// Periods for B Day
//                     period    name           startTime  endTime   Class Info               Comment                     Adjustment
const  _MVHS_BSB_a =     [-1,   "Before School",  "00:00",  "09:30", null,                "Before school on B day"];
const  _MVHS_P2B_a =     [ 2,   "P2",             "09:30",  "10:45", _MVHS_P2_CLASS_a,     "Period 2 on B day"];
const  _MVHS_P24B_a =    [-1,   "P2->P4",         "10:45",  "11:00", null,                "Passing Period 2->4 on B day"];
const  _MVHS_P4B_a =     [ 4,   "P4",             "11:00",  "12:15", _MVHS_P4_CLASS_a,     "Period 4 on B day"];
const  _MVHS_LunchB_a =  [-1,   "Lunch",          "12:15",  "13:05", null,                "Lunch on B day"];
const  _MVHS_P6B_a =     [ 6,   "P6",             "13:05",  "14:20", _MVHS_P6_CLASS_a,     "Period 6 on B day"];
const  _MVHS_ASB_a =     [-1,   "After School",   "14:20",  "23:59", null,                "After school on B day",         60*1000-1];
//                                                                                                                           ^
//                                                                                                                    This adjustment
//                                                                                                                    makes the end time
//                                                                                                                    1ms before midnight
// Periods for C Day
//                     period    name           startTime  endTime   Class Info               Comment                     Adjustment
const  _MVHS_BSC_a =     [-1,   "Before School",  "00:00",  "09:30", null,                "Before school on C day"];
const  _MVHS_P1C_a =     [ 1,   "P1",             "09:30",  "10:00", _MVHS_P1_CLASS_a,     "Period 1 on C day"];
const  _MVHS_P12C_a =    [-1,  "P1->P2",          "10:00",  "10:10", null,                "Passing Period 1->2 on C day"];
const  _MVHS_P2C_a =     [ 2,   "P2",             "10:10",  "10:40", _MVHS_P2_CLASS_a,     "Period 2 on C day"];
const  _MVHS_P23C_a =    [-1,  "P2->P3",          "10:40",  "10:50", null,                "Passing Period 2->3 on C day"];
const  _MVHS_P3C_a =     [ 3,   "P3",             "10:50",  "11:20", _MVHS_P3_CLASS_a,     "Period 3 on C day"];
const  _MVHS_P34C_a =    [-1,  "P3->P4",          "11:20",  "11:30", null,                "Passing Period 3->4 on C day"];
const  _MVHS_P4C_a =     [ 4,   "P4",             "11:30",  "12:00", _MVHS_P4_CLASS_a,     "Period 4 on C day"];
const  _MVHS_LunchC_a =  [-1,  "Lunch",           "12:00",  "13:00", null,                "Lunch on C day"];
const  _MVHS_P5C_a =     [ 5,   "P5",             "13:00",  "13:30", _MVHS_P5_CLASS_a,     "Period 5 on C day"];
const  _MVHS_P56C_a =    [-1,  "P5->P6",          "13:30",  "13:40", null,                "Passing Period 5->6 on C day"];
const  _MVHS_P6C_a =     [ 6,   "P6",             "13:40",  "14:10", _MVHS_P6_CLASS_a,     "Period 6 on C day"];
const  _MVHS_P67C_a =    [-1,  "P6->P7",          "14:10",  "14:20", null,                "Passing Period 6->7 on C day"];
const  _MVHS_P7C_a =     [ 7,   "P7",             "14:20",  "14:50", _MVHS_P7_CLASS_a,     "Period 7 on C day"];
const  _MVHS_ASC_a =     [-1,   "After School",   "14:50",  "23:59", null,                "After school on C day",         60*1000-1];
//                                                                                                                           ^
//                                                                                                                    This adjustment
//                                                                                                                    makes the end time
//                                                                                                                    1ms before midnight
*/

const _CalendarPeriodsForDayType1_k = "PeriodsForADay";
const _CalendarPeriodsForDayType2_k = "PeriodsForBDay";
const _CalendarPeriodsForDayType3_k = "PeriodsForCDay";

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
  ], // [_MVHS_PeriodsForADay_k]

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
  ], // [_MVHS_PeriodsForBDay_k]

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
  ] // [_MVHS_PeriodsForCDay_k]

}; // _MVHS_Periods_By_Day_Type_h

// END PERIOD DESCRIPTIONS
// =============================================================================

// =============================================================================
// DAY PATTERN DESCRIPTIONS
//
// Type A day, usually Monday and Thursday
/*
const _MVHS_A_Day_a = [
  _MVHS_BSA_a,
  _MVHS_P1A_a,
  _MVHS_P13A_a,
  _MVHS_P3A_a,
  _MVHS_LunchA_a,
  _MVHS_P5A_a,
  _MVHS_P57A_a,
  _MVHS_P7A_a,
  _MVHS_ASA_a
];

// Type B day, usually Tuesday and Friday
const _MVHS_B_Day_a = [
  _MVHS_BSB_a,
  _MVHS_P2B_a,
  _MVHS_P24B_a,
  _MVHS_P4B_a,
  _MVHS_LunchB_a,
  _MVHS_P6B_a,
  _MVHS_ASB_a
];

// Type C day, usually Wednesday
const _MVHS_C_Day_a = [
  _MVHS_BSC_a,
  _MVHS_P1C_a,
  _MVHS_P12C_a,
  _MVHS_P2C_a,
  _MVHS_P23C_a,
  _MVHS_P3C_a,
  _MVHS_P34C_a,
  _MVHS_P4C_a,
  _MVHS_LunchC_a,
  _MVHS_P5C_a,
  _MVHS_P56C_a,
  _MVHS_P6C_a,
  _MVHS_P67C_a,
  _MVHS_P7C_a,
  _MVHS_ASC_a
];
*/

// END DAY PATTERN DESCRIPTIONS
// =============================================================================

// =============================================================================
// WEEK DESCRIPTIONS
//
// IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT
//
// Calendar.constructor Day Descriptions
//
// The following array definiitions are each 7 elements long, corresponding to
// the 7 days of the week. Each week of the school year falls into one of these
// patterns. _MVHS_Default_Week_a is the pattern for each week, unless overridden
// by a week that has some form of holiday. For those weeks, the name of the
// week denotes what type of week it is based on the second pattern of letters,
// each of which can be "A", "B", "C" or "H", with the following meanings
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
// Each element of the array describes the day in that position in the week.
// So element 0 of the array represents Sunday, element 1 represents Monday, and so
// on to element 6, which represents Saturday.
//
// Each element is a hash that gives information about that day. The keys
// of the hash are:
//
//        dt = Day type; one of CalendarDayType*
//        pa = Period array; an array of periods in that day. See Day Pattern
//             Descriptions, above. If the value for a pa key is [], it means
//             that there are no periods in that day.
//
const _CWD_KDaT = "dt";
const _CWD_KPeA = "pa"
//
// This is the default week, which is used for any week that is not in the
// _MVHS_Week_Exceptions_h hash below.
const _MVHS_Default_Week_a = [
    {[_CWD_KDaT]: CalendarDayTypeWeekend_k,      [_CWD_KPeA]:      null       },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType3_k  },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
    {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,    [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
    {[_CWD_KDaT]: CalendarDayTypeWeekend_k,      [_CWD_KPeA]:      null       }
];

// This is the beginning-of-year week
const _MVHS_HHCAB_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType3_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       }
];

// This is a Monday holiday week
const _MVHS_HABAB_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       }
];

// This is a Monday Staff Dev week
const _MVHS_HBCAB_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType3_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       }
];

// This is a Wednesday holiday week
const _MVHS_ABHAB_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       }
];

// This is the end-of-year week
const _MVHS_ABCHH_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType1_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType2_k  },
  {[_CWD_KDaT]: CalendarDayTypeSchoolDay_k,      [_CWD_KPeA]: _CalendarPeriodsForDayType3_k  },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null       },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null       }
];

// This is a week-long holiday week
const _MVHS_HHHHH_Week_a = [
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null      },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null      },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null      },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null      },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null      },
  {[_CWD_KDaT]: CalendarDayTypeHoliday_k,        [_CWD_KPeA]:      null      },
  {[_CWD_KDaT]: CalendarDayTypeWeekend_k,        [_CWD_KPeA]:      null      }
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
    let message = "***ERROR: CalendarAssert Assertion Failed: " + msg + ": " +args.join(", ")
    console.log (message)
    throw Error ("Assertion Failed");
  }
} // function CalendarAssert (assertion, msg, ...args)

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

  let message = "Calendar Message: " + msg + ": " +args.join(", ")
  console.log (message)

} // function CalendarMessage (msg, ...args)

// DateAtMidnight
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

function DateAtMidnight (sDate) {

  // Verify the existence and type of the argument
  CalendarAssert (
    (sDate !== undefined) &&
    (typeof sDate == "string"),
    "DateAtMidnight called with invalid argument",
    sDate, typeof sDate
  );

  let mysDate = sDate + "T00:00:00"; // Force parser to set local time
  return new Date(mysDate);

}

function _leftPad (value, width, character) {
  return value.toString().padStart(width, character)
}
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
// This class defines the structure of the object for each (school) class,
// which looks like this:
//
//  Public Class Variables
//
//    period:       (Positive Integer) Period number
//
//    className:    (String) Name of the class.
//
//    room:         (String) Room in which the class is held
//
//    teacher:      (String) Name of the teacher for the class
//
//  Methods
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
  //  className   (REQUIRED String) Name of the class
  //
  //  room:       (REQUIRED String) Room in which the class is held
  //
  //  teacher:    (REQUIRED String) Name of the teacher for the class
  //
  // IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT
  // Do not change the order, type, or definition of these arguments without
  // updating the (school) class definition arrays above. Search for the string
  // "CalendarClassObject.constructor arguments"

  constructor (period, className, room, teacher) {
    // Make sure the caller provided the required arguments
    CalendarAssert(
      (period     !== undefined) &&
      (className  !== undefined) &&
      (room       !== undefined) &&
      (teacher    !== undefined),
      "CalendarClassObject.constructor called with invalid arguments",
      className, room, teacher
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
// This class defines the structure of the object for each period of the day,
// which looks like this:
//
//  Public Class Variables
//
//    period:       (Number) Period number 0..7. If this is -1, it is a pseudo
//                  period, used for before and after school, passing periods
//                  and lunch
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
//  Methods
//
//    setEDate (sDate)
//                  Set the eDate for the period from an sDate
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
  //  classInfoObject
  //             (REQURIED CalendarClassObject instance) The class information
  //             object corresponding to this period, or null if there is no
  //             class during this period.
  //
  //  comment     (OPTIONAL String) Comment describing the period
  //
  //  adjTime     (OPTIONAL Integer) Number of ms to adjust the endMSTime
  //              class variable relative to the end time specified in the
  //              endTime argument. This is used for the after school
  //              pseudo period, whose end time is 23:59, to pad the time
  //              up to the end of the hour (midnight minus 1 ms)
  //
  // IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT
  // Do not change the order, type, or definition of these arguments without
  // updating the period definition arrays above. Search for the string
  // "CalendarPeriodObject.constructor arguments"

  constructor (period, name, startTime, endTime, comment, adjTime, classInfoObject) {

    // Make sure the caller provided the required arguments
    CalendarAssert (
      (period           !== undefined) &&
      (name             !== undefined) &&
      (startTime        !== undefined) &&
      (endTime          !== undefined) &&
      (classInfoObject  !== undefined),
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

/*
  // ===========================================================================
  // setEDate
  //
  // Set the eDate value in the class period object
  //
  // Arguments:
  //  sDate      (REQUIRED String) The date to use to set the variables
  //
  // Returns:
  //  None

  setEDate (sDate) {

    // Create a new Date() object, forcing the time to midnight of the day
    this.eDate = DateAtMidnight(sDate);

  } // CalendarPeriodObject.setEDate
  */

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
// This class defines the structure of the object for each day of the week,
// which looks like this:
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
//    eDate:        (Date() object) Date for this day
//
//    periodObjectArray
//                  (Array of CalendarPeriodObject) Array containing the
//                  objects for each period of the day. If the length
//                  of this array is 0, there are no periods in the day

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
  //
  // IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT IMPORTANT
  // Do not change the order, type, or definition of these arguments without
  // updating the day definition arrays above. Search for the string
  // "CalendarDayObject.constructor arguments"


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
    this.eDate = DateAtMidnight(dayTag);

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

  } // CalendarDayObject.constructor

  // ===========================================================================
  // setEDate
  //
  // Set the eDate, printDate, and dayName value in the class period object
  //
  // Arguments:
  //  sDate      (REQUIRED String) The date to use to set the variables
  //
  // Returns:
  //  None
/*
  setEDate (sDate) {

    const dayIndexToName_a = [
          "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        ];

    // Create a new Date() object, forcing the time to midnight of the day
    this.eDate = DateAtMidnight(sDate);

    // Generate a printable date
    let month = this.eDate.getMonth() + 1;
    this.printDate =
    month.toString() + "/" +
    this.eDate.getDate().toString() + "/" +
    this.eDate.getFullYear().toString();

    // Create the printable day name
    this.dayName = dayIndexToName_a[this.eDate.getDay()];

  } // CalendarDayObject.setEDate
*/
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
// which looks like this:
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
    this.eDate = DateAtMidnight(weekTag);

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
// This class defines the structure of the data structure for the calendar,
// which looks like this:
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
//                  The type of school day (see _MVHS_Periodsfor*Day_k) and whose
//                  value is and array of CalendarPeriodObject instances for
//                  each period of that day type. The class information for
//                  real periods will have already been inserted into these
//                  objects



// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=

class Calendar {

  // ===========================================================================
  // Constructor
  //
  // Initialize a new Calendar Object
  //
  // Arguments:
  //  startSDate      (OPTIONAL String) Starting date for the new school year
  //                  in the form "yyyy-mm-dd". Defaults to SchoolFirstDay_k
  //                  if not supplied.
  //
  //  endSDate        (OPTIONAL String) Ending date for the new school year
  //                  in the form "yyyy-mm-dd". Defaults to SchoolLastDay_k
  //                  if not supplied.
  //
  //  defaultWeek     (OPTIONAL Array, length 7, of hashes). The default week
  //                  array, to be used for any week in which there is not an
  //                  exception. Defaults to SchoolDefaultWeek_a if not supplied.
  //                  The length of this array must equal 7, representing the 7
  //                  days in a week. Search for the string
  //
  //                      Calendar.constructor Day Descriptions
  //
  //                  for an explanation of the format of this argument.
  //
  //  weekExceptions  (OPTIONAL hash) A hash that identifies those weeks that
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

    // Create objects for class information about each period.
    this._classInfoObjectArray = _helperInstantiateClassInfoObjects();
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

    function _helperInstantiateClassInfoObjects () {
      // Loop through _CalendarClassInfoArray_a, create a new object for the
      // class info, and store it in the _classInfoObjectArray based on the
      // period number
      let classInfoObjectArray = [];
      for (const classInfo of _CalendarClassInfoArray_a) {
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

          periodObjectHash[thisDayType].push(new CalendarPeriodObject(
            period, className, startTime, endTime, comment, adjTime, classInfoObject
          ))
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

    let eDate = DateAtMidnight(sDate);
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
    _leftPad(month, 2,"0") + "-" +
    _leftPad(myEDate.getDate(), 2, "0"))

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
    //  Object containing the matching CalendarDayObject instance (key "dayObj"),
    //  the matching CalendarPeriodObject instance (key "pObj"), and the
    //  matching period index (key "pIndex"). The pindex value is used in
    //  conjunction with the getNextPeriod method, below.


    getPeriodByDateAndTime (eDate) {

      // Make sure the specified date is in the calendar. Return null if not
      if (
        (eDate.getTime() < this._startWEDate.getTime()) ||
        (eDate.getTime() > this._endWEDate.getTime())
      ) {
        return null;
      };

      // Convert the date to a day tag and get the day object for that date
      let dayTag = this.getDayTag(eDate);
      let dayObject = this._dayObjectHash[dayTag];

      // If dayObject.periodObjectArray is null, there are no periods that day,
      // so return [dayObject, null, 0] to the caller
      if (dayObject.periodObjectArray === null) {
        return {dObj: dayObject, pObj: null, pindex: 0};
      }

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
          return {dObj: dayObject, pObj: periodObject, pIndex: index};
        }
      }

      // If we get here, there was no match, so return null
      return null;

    } // Calendar.getPeriodByDateAndTime

    getNextPeriod (lastMatch, matchRealPeriod) {
      if (
        (lastMatch === undefined) ||
        (typeof lastMatch !== "object") ||
        (lastMatch.dObj === undefined) ||
        (lastMatch.pObj === undefined) ||
        (lastMatch.pIndex === undefined)
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
      let pIndex = lastMatch.pIndex;


    } // getNextPeriod

    getMsSinceMidnight (eDate) {
      let sDate = this.getDayTag(eDate);
      let midnight = DateAtMidnight(this.getDayTag(eDate));
      return eDate.getTime() - midnight.getTime();

    } // Calendar.getMsSinceMidnight

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
    // getDayList
    //
    // Return the list of all day tags in the calendar
    //
    // Arguments:
    //  None
    //
    // Returns
    //  List of all day tags in the calendar

    getDayList () {
      let dayList = [...this._dayTagArray]; // Return a copy of the list
      return dayList;

    } // Calendar.getDayList

    // ===========================================================================
    // getWeekList
    //
    // Return the list of all week tags in the calendar
    //
    // Arguments:
    //  None
    //
    // Returns
    //  List of all week tags in the calendar

    getWeekList () {
      let weekList = [...this._weekTagArray]; // Return a copy of the list
      return weekList;
    } // Calendar.getWeekList


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

if (_enableTestCode) {
  // Emit error message for a test failure
  function emitError (testNum, message, ...args) {
    let errorString = "** ERROR in test " + testNum + " " + message;
    for (let i = 0; i < args.length; i++) {
      if (i === 0)  errorString += ": " + args[i];
      else          errorString += ", " + args[i];
    }
    throw Error(errorString);
  }

  // Test 1: Validate that the different paths into _weekTagArray result in the same
  // values, and that the array contents match
  function test1(calendar) {
    CalendarMessage ("Test 1: Validate _weekTagArray");
    let weekList = calendar.getWeekList();
    if (weekList.length !== calendar._weekTagArray.length) {
      emitError (1.1, "weekList length test failed", weekList.length, calendar._weekTagArray.length)
      return false;
    }
    for (let i = 0; i < weekList.length; i++) {
      if (weekList[i] !== calendar._weekTagArray[i]) {
        emitError (1.2, "weekList mismatch", i, weekList[i], calendar._weekTagArray[i])
        return false;
      }
    }
    return true;
  }

  // Test 2: Validate that the first and last tags in the week list correspond
  // to the start and end days of the school year.
  function test2(calendar) {
    CalendarMessage ("Test 2: Validate _dayTagArray school year range");
    let firstWeek = calendar.getDayTag(calendar.getFirstDayOfWeek(new Date(MVHSFirstDay_k)));
    if (firstWeek !== calendar._weekTagArray[0]) {
      emitError (2.1, "Error in first _weekTagArray entry", firstWeek, calendar._weekTagArray[0]);
      return false;
    }
    let lastWeek = calendar.getDayTag(calendar.getFirstDayOfWeek(new Date(MVHSLastDay_k)));
    if (lastWeek !== calendar._weekTagArray[calendar._weekTagArray.length - 1]) {
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
    return true;
  }

  // Test 3: Validate that the different paths into _dayTagArray result in the same
  // values, and that the array contents match
  function test3(calendar) {
    CalendarMessage ("Test 3: Validate _dayTagArray");
    let dayList = calendar.getDayList();
    if (dayList.length !== calendar._dayTagArray.length) {
      emitError (3.1, "Daylist length test failed", dayList.length, calendar._dayTagArray.length)
      return false;
    }
    for (let i = 0; i < dayList.length; i++) {
      if (dayList[i] !== calendar._dayTagArray[i]) {
        emitError (3.2, "dayList mismatch", i, dayList[i], calendar._dayTagArray[i]);
        return false;
      }
    }
    return true;
  }

  // Test 4: Validate that the first and last tags in the day list correspond
  // to the start and end days of the school year.
  function test4(calendar) {
    CalendarMessage ("Test 4: Validate _dayTagArray school year range");
    let firstDay = calendar.getDayTag(calendar.getFirstDayOfWeek(new Date(MVHSFirstDay_k)));
    if (firstDay !== calendar._dayTagArray[0]) {
      emitError (4.1, "Error in first _dayTagArray entry", firstDay, calendar._dayTagArray[0]);
      return false;
    }

    let lastDay = calendar.getDayTag(calendar.getLastDayOfWeek(new Date(MVHSLastDay_k)));
    if (lastDay !== calendar._dayTagArray[calendar._dayTagArray.length - 1]) {
      emitError (4.2, "Error in last _dayTagArray entry", lastDay, calendar._dayTagArray[calendar._dayTagArray.length-1]);
      return false;
    }

    let expectedEDate = calendar.convertSDateToEDate(calendar._dayTagArray[0]);
    for (let i = 0; i < calendar._dayTagArray.length; i++) {
      let actualEDate = calendar.convertSDateToEDate(calendar._dayTagArray[i]);
      if (actualEDate.getTime() !== expectedEDate.getTime()) {
        emitError (4.3, "dayList date not expected", i, actualEDate, expectedEDate)
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
    return true;
  }


  // Test 5: Validate that the lengths of _dayTagArray and _weekTagArray are
  //  consistent.
  function test5(calendar) {
    CalendarMessage ("Test 5: Validate length consistency of _dayTagArray and _weekTagArray");
    let dayList = calendar.getDayList();
    let weekList = calendar.getWeekList();
    if (dayList.length !== weekList.length * 7) {
      emitError(5.1, "Length mismatch between _dayTagArray and _weekTagArray", dayList.length, weekList.length)
    }
    return true;
  }

  // Test6: Validate that the eDate in the week object matches the week tag
  function test6(calendar) {
    CalendarMessage ("Test 6: Validate dates in weeks and days");
    for (const weekTag of calendar.getWeekList()) {
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
  }
  // Create new calendar using argument defaults and initialize the weeks, days
  // and periods
  let calendar = new Calendar();

  // Run tests and report results

  if (test1(calendar)) CalendarMessage ("  Test passed");
  if (test2(calendar)) CalendarMessage ("  Test passed");
  if (test3(calendar)) CalendarMessage ("  Test passed");
  if (test4(calendar)) CalendarMessage ("  Test passed");
  if (test5(calendar)) CalendarMessage ("  Test passed");
  if (test6(calendar)) CalendarMessage ("  Test passed");


  if (true) {
    let eDate = new Date("2020-09-28T11:45:00");

    console.log("Processing " + eDate)
//    let day = calendar.getDayByTag("2020-09-28")
//    console.log(day.toString())
    let match = calendar.getPeriodByDateAndTime(eDate);
    console.log ("Return value is " + match)
    if (match === null) {
      console.log("No match on " + eDate)
    } else if (match.pObj === null) {
      console.log (match.dayObj.toString("", 1))
    } else {
      console.log(
        match.dObj.dayName + " " +
        match.dObj.printDate + " " +
        match.dObj.dayType + " " +
        match.dObj.dayTag + " " +
        match.dObj.weekTag + " "

      )
      console.log(match.pObj.toString("",2))
      console.log("Found at index " + match.pIndex)
      let timeLeft = calendar.getTimeRemainingInPeriod(eDate, match.pObj);
      console.log ("Time remaining is " + timeLeft.hDelta + ":" + _leftPad(timeLeft.mDelta,2,"0") + ":" + _leftPad(timeLeft.sDelta,2,"0"));
    }

  }

  if (false) {
    let weekTag = calendar._weekTagArray[0];
    CalendarMessage ("Processing week " + weekTag)
    let weekObject = calendar._weekObjectHash[weekTag]
    let message = weekObject.toString()
    CalendarMessage(message)
  }


  if (false) {
    let weekTag = calendar._weekTagArray[0];
    CalendarMessage ("Processing week " + weekTag)
    let weekObject = calendar._weekObjectHash[weekTag]
    let message = weekObject.toString()
    CalendarMessage(message)

  }
  if (false) {
    for (let i = 0; i < calendar._weekTagArray.length; i++) {
      let weekTag = calendar._weekTagArray[i];
      CalendarMessage ("Processing week " + weekTag)
      let weekObject = calendar._weekObjectHash[weekTag]
      let message = weekObject.toString()
      CalendarMessage (message)
    }
  }

  // END TEST CODE FOR LOCAL TESTING OF THE CLASSES
  // =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=

} // if (_enableTestCode)
