// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// display.js
// Period-Countdown
//
// Displays information onto the extension
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=

"use strict"

// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
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
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// Canvas and context properties
var canvas = document.getElementById('Periods');
var context = canvas.getContext('2d');


// Time constants
const DAY = 1000 * 60 * 60 * 24;
const HOUR = 1000 * 60 * 60;
const MINUTE = 1000 * 60;
const SECOND = 1000;


// ====================================================================================================
// function DisplayMessage
//
// Displays a message with comma separated elements
//
// Arguments--
//
// msg:     main message to be displayed
//
// args:    list of extra arguments to attatch
//
// Returns--
//
// None
//
function DisplayMessage(msg, ...args) {
    // Create the message
    let message = "Display Message: " + msg;
    if (args.length > 0) {
      message += " " + args.join(", ");
    }

    // Display the message
    console.log(message);
}
// end: function DisplayMessage


// Create a new instance of the Calendar class (with all information for current classes)
let calendar;
calendar = new Calendar();

// Definitions for the size of position of text
var textDef = {
    textX: 10,
    nameY: 35,
    timeY: 105,
    nameSize: "35px Arial",
    timeSize: "59px Arial"
}

// The following variable lets us force a date and time to see the result.
// If it is set to null, the current date/time is used
// let lookupDateTime = "2021-10-08T12:50:00"
let lookupDateTime = null;
let eDate;

// Define period match variables
let match;
let nextMatch;

// Time left in the current period
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

DisplayMessage("Looking for the day/period for", eDate);

// Start the loop of checking the time, updating text, and updating the timer
refreshPeriod(eDate);
timeLeft = refreshRemainingTime(eDate);
enableTimer();


// ====================================================================================================
// function refreshPeriod
//
// Updates and displays the current period/class text
//
// Arguments--
//
// eDate:   the epic date, set to the JavaScript built-in "new Date()" or the user specified date
//
// Returns--
//
// None
//
function refreshPeriod(eDate) {

    const matchRealPeriod = true; // Only give real periods (only includes actual class periods-includes all class periods)
    const matchPeriodsWithClass = true; // Only includes class periods that are not "null"

    match = calendar.getPeriodByDateAndTime(eDate); // Get the period

    // No match was found, its likely summer/otherwise out of the year's scope
    if (match === null) {
        DisplayMessage("No match on", eDate);

        // Display Summer information information on extension
        printText("Summer", " | ", "Free", textDef.nameSize, textDef.textX, textDef.nameY, 'black');
        return;
    } 

    // Get the next period match
    nextMatch = calendar.getNextPeriod(match, matchRealPeriod, matchPeriodsWithClass);

    // Found a match on both day and period.
    let dObj = match.dObj; // day object
    let pObj = match.pObj; // period object

    // Print day information
    DisplayMessage("DAY INFO:", dObj.dayName, dObj.printDate, "is a", dObj.dayType);
    // Print period information
    DisplayMessage("Period", pObj.name, "starts at", pObj.startSTime, "and ends at", pObj.endSTime);

    // Init cObj with the class information
    let cObj = pObj.classInfoObject;

    // Print class information
    if (pObj.period >= 0 && cObj !== null) { // Only print the class information if there is an avaible class ("null" takes the place of "no class" in the data structure)

        DisplayMessage("The class in this period is", cObj.className, "taught by", cObj.teacher, "in room", cObj.room);

        // Display "during an active period" --> this only includes real periods, not passing periods
        let className = (cObj === null) ? "No class" : cObj.className;

        // Display the real class period on the extension
        printText(className, " | ", pObj.name, textDef.nameSize, textDef.textX, textDef.nameY, 'black');

    } 
    else { // Else it is a free period (passing, lunch, tutorial, holidays etc.)
        DisplayMessage("There is no class during this period");

        // Display period information for free period
        printText(pObj.name, " | ", "Free", textDef.nameSize, textDef.textX, textDef.nameY, 'black');

    }
}
// end: function refreshPeriod


// ====================================================================================================
// function refreshRemainingTime
//
// Get the time remaining information for the given period and print on the extension
//
// Arguments--
//
// eDate:       the epic date, set to the JavaScript built-in "new Date()"
//
// Returns--
//
// timeLeft:    the amount of time remaining in the current period or until the next period
//
function refreshRemainingTime(eDate) {
    // match == null, it is summer
    if (match == null) {
        var yearDefinitions = new SchoolYearDefinitions();
        var firstPeriod = calendar.getPeriodByDateAndTime(new Date(yearDefinitions.firstDateTime));
        timeLeft = calendar.getTimeRemainingUntilPeriod(eDate, firstPeriod.dObj, firstPeriod.pObj, true);
        DisplayMessage("Time remaining in summer is", timeLeft.toString());
    }
    // match was found, it is a real period
    else if (match.pObj.period >= 0) {
        timeLeft = calendar.getTimeRemainingInPeriod(eDate, match.pObj);
        DisplayMessage("Time remaining in the period is", timeLeft.toString());
    } 
    // match was found, it is a free/passing period
    else {
        timeLeft = calendar.getTimeRemainingUntilPeriod(eDate, nextMatch.dObj, nextMatch.pObj, true);
        DisplayMessage("Time remaining until the period is", timeLeft.toString());
    }

    // Print time remaining for any applicable period
    printText(timeLeft.toString(), "", "", textDef.timeSize, textDef.textX, textDef.timeY, 'black');

    return timeLeft;
}
// end: function refreshRemainingTime


// ====================================================================================================
// function getTextWidth
//
// Gets the pixel width of a string given a certain font size and type
//
// Arguments--
//
// text:            the text to check the size of
//
// font:            the font formatting of text
//
// Returns--
//
// metrics.width:   the width of the text
//
function getTextWidth(text, font) {
    context.font = font;
    var metrics = context.measureText(text);
    return metrics.width;
}
// end: function getTextWidth


// ====================================================================================================
// function printText
//
// Prints formatted text
//
// Arguments--
//
// msg1:        the first of two messages that can be displayed
//
// divider:     the choice to have a divider character between the two messages
//
// msg2:        the second message that is displayed
//
// textSize:    the font size of the text to be printed
//
// x:           the x position of the text to be printed
//
// y:           the y position of the text to be printed
//
// color:       the color of the text to be printed
//
// Returns--
//
// None
//
function printText(msg1, divider, msg2, textSize, x, y, color) {
    var textWidth = 999999
    while (textWidth > canvas.width - x) {
        textWidth = getTextWidth(msg1 + divider + msg2, textSize)
        var size = parseInt(textSize.substring(0, 2)) - 1
        textSize = size + textSize.substring(2)
    }
    
    context.fillStyle = color;
    context.font = textSize;
    context.fillText(msg1 + divider + msg2, x, y);
}
// end: function printText


// ====================================================================================================
// function enableTimer
//
// Update the information every second using setInterval()
//
// Arguments--
//
// None
//
// Returns--
//
// None
//
function enableTimer () {
    // Create a 1000ms interval
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
}
// end: function enableTimer
