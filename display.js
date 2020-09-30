// display.js
//
// Coordination of information from Calendar.js into a graphics-based interface

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


// =============================================================================
// enableTimer
//
// ***SUBJECT TO CHANGE: A function that controls the renew timer (gathers new 
// information and displays it on the Chrome extention).
//
// Arguments:
//
// enableTimer takes no arguments
// =============================================================================
enableTimer();
function enableTimer () {
    setInterval(function () {
        gatherDayInfo();
    }, 10)
}

// =============================================================================
// gatherDayInfo
//
// ***SUBJECT TO CHANGE: A function called by enableTimer that gets updated date
// information. The day variables (today, hour, minute, second) will likely be
// removed. todayObj was a day-map from [DEPRECATED]-content.js that controlled
// the displaying of date information on the extension.
//
// Arguments:
//
// gatherDayInfo takes no arguments
// =============================================================================

function gatherDayInfo() {
    context.clearRect(0, 0, canvas.width, canvas.height);
    currentDate = new Date();
    today = currentDate.getDay();
    hour = currentDate.getHours();
    minute = currentDate.getMinutes();
    second = currentDate.getSeconds();
    var todayObj = dayMap[today];
    printDay(todayObj);
}