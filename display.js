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

const DisplayVersion = "1.0.0";

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
//
// 1.2.1    10/10/2020  Changes in this version:
//                          -Documentation conventions updated to match Calendar.js
//                          (no functional changes)
//                          -Additional documentation changes
//
// 1.3.0    10/10/2020  Changes in this version:
//                          -Added function printText to handle displaying text
//                          -Removed individual print statements (context.fillText();)
//                          from refreshPeriod and refreshRemainingTime with a call of
//                          printText
//                          -Renamed the textPos object to textDef (text definition) for
//                          clarity
//                          -Removed the x and y offset variables from textDef in favor of
//                          a single x-position variable (textDef.textX) and independant
//                          y-position variables (textDef.nameY for "period name" text and
//                          textDef.timeY for "time remaining" text)


// TO-DO:
//
// 1) Clean-up documentation
//
// 2) Condense context.___ into a function
//
// 3) Condese console.log messages into a function
//
// 4) Get settings to work with the "next-up" periods feature

// Version information
this.Version = DisplayVersion
console.log("Display v" + this.Version);

// Create a new instance of the Calendar class (with all information for current classes)
calendar = new Calendar();

// Text definition object
var textDef = {
    textX: 10,
    nameY: 35,
    timeY: 105,
    nameSize: "35px Arial",
    timeSize: "59px Arial"
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
//
// Returns--
// does not return a value to the caller:       Displays the period name and action
//                                              (like "Free") on the extension
//
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
        printText("Summer", " | ", "Free", textDef.nameSize, textDef.textX, textDef.nameY);

    } 
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

            printText(className, " | ", pObj.name, textDef.nameSize, textDef.textX, textDef.nameY);

        } 
        else {
            console.log ("There is no class during this period");

            // Display "in the school year but not a school day" --> weekends and holidays
            printText(dObj.dayType, " | ", "Free", textDef.nameSize, textDef.textX, textDef.nameY);

        }
    } // end: if (pOjb.period >= 0) ... else
} // end: function refreshPeriod


// =============================================================================
// refreshRemainingTime();
//
// Get the time remaining information for the given period and print the time
// remaining onto the Chrome extension canvas.
//
// Arguments--
//
// eDate:       The epic date, set to the JavaScript built-in "new Date();"
//
// Returns--
//
// timeLeft:        The adjusted amount of time remaining in the current period
//                  (set depending on if the current period is a pseudo-period
//                  or not)
//
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
    printText(timeLeft.toString(), "", "", textDef.timeSize, textDef.textX, textDef.timeY);

    return timeLeft;
} // end: function refreshRemainingTime


// =============================================================================
// printText();
//
// Takes and prints information about the time remaining and the name of the
// current period. Replaces calls to context.fillText with calls to printText
//
// Arguments--
//
// msg1:        The first of two messages that can be displayed (this is the 
//              timestamp, "Biology", "Weekend", etc.)
//
// divider:     The choice to have a divider like " | " between the two messages
//
// msg2:        The second message that is displayed (only used by the name, not
//              the time remaining; this is like "Free" or "P1")
//
// textSize:    The font size of the text to be printed
//
// x:           The x position of the text to be printed
//
// y:           The y position of the text to be printed
//
function printText(msg1, divider, msg2, textSize, x, y) {

    context.font = textSize;
    context.fillText(msg1 + divider + msg2, x, y);

} // end function printText


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
//
// Returns--
//
// does not return a value to the caller:       handles updates (frame by frame)
//                                              of the extension
//
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

    // Update every 1 second --> this leads to the timer trailing by ~1 second for 
    // every 20 hours of run time (if the period remains the same)
    }, 1000)
} // end: function enableTimer

 