// display.js
//
// Coordination of information from Calendar.js into a graphics-based interface

"use strict";

// =+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// Copyright 2020 Jonathan Uhler and Mike Uhler
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

// Canvas and context properties
var canvas = document.getElementById('Periods');
var context = canvas.getContext('2d');

let DisplayVersion = "1.0.0";

// Revision History
//
//  version    date                     Change
//  ------- ----------  --------------------------------------------------------
//  1.0.0   10/04/2020  First usable release of display.js
//
//  1.1.0   10/05/2020  Changes in this version:
//                          -Add new code from Calendar.js ("toString", removal
//                          of CalendarHHMMSSAsString)
//                          -Add "nextMatch" to display the time until the next
//                          period if the current period is a pseudo-period (end-
//                          of-day or start-of-day)
//                              -Change "timeLeft" by one of two values depending
//                              on the type of period
// 
// 1.2.0    10/10/2020  Changes in this version:
//                          -timeLeft.toString replaced with timeLeft.toString();
//                          that calls a function in Calendar.js
//                          -Days do not display anymore and are converted to hours
//                          in Calendar.js

// Version information
this.Version = DisplayVersion
console.log("Display v" + this.Version);


// Create a new instance of the Calendar class (with all information for current classes)
calendar = new Calendar();

// Text information variables
var textPos = {
    x: 10,
    y: 35,
    xOffset: 0,
    yOffset: 70,
    timeSize: "59px Arial",
    nameSize: "35px Arial"
}
// Set common color for all text
context.fillStyle = 'black';

// The following variable lets us force a date and time to see the result.
// If it is set to null, the current date/time is used

// let lookupDateTime = "2020-10-07T09:50:00";
let lookupDateTime = null;
let eDate;

let match;
let nextMatch;

let timeLeft = {
    msTotal: 0,
    dDelta: 0,
    hDelta: 0,
    mDelta: 0,
    sDelta: 0,
}

if (lookupDateTime === null) {
eDate = new Date(); // If null, assume the user is referencing the current date (right now)
} 
else {
eDate = new Date(lookupDateTime); // If not null, set the epic date to the timestamp given
}

console.log ("Looking for the day/period for " + eDate);


refreshPeriod(eDate);
timeLeft = refreshRemainingTime(eDate);
enableTimer();


// =============================================================================
// refreshPeriod();
//
// Responsible for displaying and updating the current period name
//
// Arguments--
//
// eDate:       The epic date, set to the JavaScript built-in "new Date();"
// =============================================================================

function refreshPeriod(eDate) {

    const matchRealPeriod = true; // Only give real periods (only includes actual class periods-includes all class periods)
    const matchPeriodsWithClass = true; // Only includes class periods that are not "null"

    match = calendar.getPeriodByDateAndTime(eDate);
    nextMatch = calendar.getNextPeriod(match, matchRealPeriod, matchPeriodsWithClass);

    if (match === null) {

        // if match is null, then there is no match against the current date/time,
        // which likely means that the date/time is outside the current school year
        console.log ("No match on " + eDate)

        // Display "not in school" information on extension
        context.font = textPos.nameSize;
        context.fillText("Summer | Free", textPos.x, textPos.y) // There is only 1 year worth of data as of now, so this CANNOT display a "time left in period" number

    } 


    // =============================================================================
    // WARNING: The following "else if" conditional can never be true and is never called
    // =============================================================================
    //
    // else if (match.pObj === null) {

    //     // if match.pObj is null, then there was a day match, but there are no
    //     // periods for that day, as would be the case on a weekend or a holiday
    //     let dObj = match.dObj;
    //     console.log (
    //         dObj.dayName + ", " +
    //         dObj.printDate + " is a " +
    //         dObj.dayType + " and has no periods"
    //     );

    //     // Display "during school year but not a day that has school" --> weekends and holidays
    //     context.font = textPos.nameSize;
    //     context.fillText(dObj.dayType + " | Free", textPos.x, textPos.y)

    // } 
    else {

        // Found a match on both day and period.
        let dObj = match.dObj; // day object
        let pObj = match.pObj; // period object

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


        // Init cObj to be used in the next line
        let cObj = pObj.classInfoObject;

        // Print class information
        if (pObj.period >= 0 && cObj !== null) { // Only print the class information if there is an avaible class ("null" takes the place of "no class" in the data structure)

            console.log (
            "The class in this period is " + cObj.className +
            ", taught by " + cObj.teacher  +
            ", in room " + cObj.room
            );

            // Display "during an active period" --> this only includes real periods, NOT passing periods
            // Test if cObj is null, return special case "No class", else return cObj.className
            let className = (cObj === null) ? "No class" : cObj.className

            context.font = textPos.nameSize;
            context.fillText(className + " | " + pObj.name, textPos.x, textPos.y)

        } 
        else {
            console.log ("There is no class during this period");

            // Display "in the school year but not a school day" --> weekends and holidays
            context.font = textPos.nameSize;
            context.fillText(dObj.dayType + " | Free", textPos.x, textPos.y)
        }
//       if (pObj.period < 0) {
//            if (nextMatch !== null) {
//                 timeLeft = calendar.getTimeRemainingUntilPeriod(eDate, nextMatch.dObj, nextMatch.pObj);
//             }
//         }
    } // if (pOjb.period >= 0) ... else
}


// =============================================================================
// refreshRemainingTime();
//
// Get the time remaining information for the given period and print the time
// remaining onto the Chrome extension canvas.
//
// Arguments--
//
// eDate:       The epic date, set to the JavaScript built-in "new Date();"
// =============================================================================

function refreshRemainingTime(eDate) {
    // Print time left in period
    if (match.pObj.period >= 0) {
        timeLeft = calendar.getTimeRemainingInPeriod(eDate, match.pObj);
        console.log ("Time remaining in the period is " + timeLeft.toString());
    } else {
        timeLeft = calendar.getTimeRemainingUntilPeriod(eDate, nextMatch.dObj, nextMatch.pObj, true)
        console.log ("Time remaining until the period is " + timeLeft.toString());
    }

    // Print time remaining for any applicable period
    context.font = textPos.timeSize;
    context.fillText(timeLeft.toString(), textPos.x - textPos.xOffset, textPos.y + textPos.yOffset)

    return timeLeft;
}


// =============================================================================
// enableTimer();
//
// Update the information being displayed on the Chrome extension every second
// (1000 milliseconds) using setInterval(). Clears the canvas and redraws the
// time remaining in the current period using an updated instance of eDate and
// a call of refreshRemainingTime();
//
// Arguments--
//
// enableTimer takes no arguments.
// =============================================================================

function enableTimer () {
    setInterval(function () {

        // If time left in the period goes to 0, the current period will be refreshed
        if (timeLeft.msTotal <= 0) {
            context.clearRect(0, 0, canvas.width, canvas.height);
            refreshPeriod(eDate);
        }

        // Clear the canvas to prevent text overlap
        context.clearRect(0, 50, canvas.width, canvas.height);

        // Update the eDate value to the current date and time
        eDate = new Date();

        timeLeft = refreshRemainingTime(eDate);

    }, 1000) // Update every 1 second --> this leads to the timer trailing by ~1 second for every 20 hours of run time (if the period remains the same)
}

 