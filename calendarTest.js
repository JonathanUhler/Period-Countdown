// calendarTest.js
//
// Self-test and example code for the Calendar class
//
"use strict";

// Self-test code is always enabled. The following constant also enables the
// example code
const _enableExampleCode = false;        // True to enable the example code.

// Revision History
//
//  version    date                     Change
//  ------- ----------  --------------------------------------------------------
//  1.0.0   10/21/2020  Move self-test code from Calendar.js to this file

// TODO List
//  1. In the self-test code, find some way to verify getNextPeriod, perhaps by
//     doing something similar to the test for getPeriodByDateAndTime.

// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
//  Copyright 2020 Mike Uhler and Jonathan Uhler. All rights reserved. License
//  under the MIT license:
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
  CalendarMessage ("  _weekTagArray is " + calendar._weekTagArray.length +
  " entries: " + calendar._weekTagArray[0] + ".." + calendar._weekTagArray[calendar._weekTagArray.length-1])

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
  CalendarMessage ("  _dayTagArray is " + calendar._dayTagArray.length +
  " entries: " + calendar._dayTagArray[0] + ".." + calendar._dayTagArray[calendar._dayTagArray.length-1])
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
let calendar;
let CalendarMessage;

// If running on a browser, the html file loads Calendar.js before calendarTest.js
// and the Calendar class is visible. If running on node.js,
// one has to require the file.
if (typeof process !== "undefined") {
  console.log("Attempting to load Calendar.js");
  let reqVal = require('./Calendar.js');
  let Calendar = reqVal.Calendar;
  CalendarMessage = reqVal.CalendarMessage;
  calendar = new Calendar();
} else {
  calendar = new Calendar();
}
CalendarMessage("Created new Calendar instance")
CalendarMessage (`   Calendar v${calendar.getVersion()}, SchoolYearDefinitions v${calendar.schoolVersion}`);

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



// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// EXAMPLE CODE SHOWING HOW TO INTERACT WITH THIS FILE

// The following block of code shows some examples of how to interface to
// Calendar.js from an application. It should be disabled when run with an application
if (_enableExampleCode) {

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
