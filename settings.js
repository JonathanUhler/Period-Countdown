// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// settings.js
// Period-Countdown
//
// Manages the user using the settings and utilities menus
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=

"use strict";

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


// ====================================================================================================
// function SettingsMessage
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
function SettingsMessage(msg, ...args) {
    // Create the message
    let message = "Settings Message: " + msg;
    if (args.length > 0) {
      message += " " + args.join(", ");
    }
    // Display the message
    console.log(message);
}
// end function SettingsMessage


// ====================================================================================================
// function fade
//
// Fades an html element
//
// Arguments--
//
// element: the name of the HTML element to fade
//
// Returns--
//
// None
//
function fade(element) {
    var op = 1; // initial opacity
    var timer = setInterval(function () {
        if (op <= 0.1) {
            clearInterval(timer);
            document.getElementById(element).style.display = 'none';
        }

        document.getElementById(element).style.opacity = op;
        document.getElementById(element).style.filter = 'alpha(opacity=' + op * 100 + ")";

        op -= op * 0.5;

    }, 30);
}
// end: function fade


// ====================================================================================================
// function unfade
//
// Unfades an html element
//
// Arguments--
//
// element: the name of the HTML element to unfade
//
// Returns--
//
// None
//
function unfade(element) {
    var op = 0.1; // initial opacity
    document.getElementById(element).style.display = 'block';
    var timer = setInterval(function () {
        if (op >= 1) {
            clearInterval(timer);
        }

        document.getElementById(element).style.opacity = op;
        document.getElementById(element).style.filter = 'alpha(opacity=' + op * 100 + ")";

        op += op * 0.5;

    }, 50);
} // end: function fade


// MARK: Settings button

var hideElems = ["classes", "submitPeriods", "utils"];
var userClassDefaults = [];
// HTML class textbox element IDs
var textIDs = ["P1Text", "P2Text", "P3Text", "P4Text", "P5Text", "P6Text", "P7Text"];
// Settings button icon
document.getElementById("goToSettings").addEventListener("click", openSettingsTab);

// Make the boxes start hidden until the settings button is pressed
for (var i = 0; i < hideElems.length; i++) {
    document.getElementById(hideElems[i]).style.display = 'none';
}

// Button to save the periods
var submitButton = document.getElementById('submitPeriods');
submitButton.addEventListener("click", saveUserDefaults, false);


// ====================================================================================================
// function saveUserDefaults
//
// Saves class names
//
// Arguments--
//
// None
//
// Returns--
//
// None
//
function saveUserDefaults() {
    // Clear all previous choices
    userClassDefaults = [];

    // Save each of the strings in the textboxes to an array
    for (var i = 0; i <= textIDs.length - 1; i++) {
        // Get the value
        var classChoice = document.getElementById(textIDs[i]).value;
        // Check for free periods
        if (classChoice.toLowerCase() === "none" || classChoice.toLowerCase() === "n/a" || classChoice.toLowerCase() === "free") {
            userClassDefaults.push(null);
        }
        // Otherwise push the name
        else {
            userClassDefaults.push(classChoice);
        }
    }

    // Display all the choices
    SettingsMessage(`New Classes:`, userClassDefaults);

    // Write the choices to a file to be read by MVHS.js
    removeCookie("periods");
    createCookie("periods", userClassDefaults);
}
// end: function saveUserDefaults


// ====================================================================================================
// function removeCookie
//
// Removes an existing cookie
//
// Arguments--
//
// name:    the name of the cookie to remove
//
// Returns--
//
// None
//
function removeCookie(name) {
    var removeStatement = name + "= ; expires = Thu, 01 Jan 1970 00:00:00 GMT";
    document.cookie = removeStatement;
}
// end: function removeCookie


// ====================================================================================================
// function createCookie
//
// Creates a new cookie
//
// Arguments--
//
// name:    the name of the new cookie
//
// value:   the value that the cookie should have
//
// Returns--
//
// None
//
function createCookie(name, value) {
    var newCookie = name + "=" + value;
    document.cookie = newCookie;

    SettingsMessage("New cookie data:", document.cookie);
}
// end: createCookie


// ====================================================================================================
// function openSettingsTab
//
// A function to show and hide the text boxes where class names can be entered
//
// Arguments--
//
// None
//
// Returns--
//
// None
//
function openSettingsTab() {
    // Get the classes div
    var period = document.getElementById("classes");

    // Read in cookie data
    try {
        var cookieData = document.cookie;
        var periodsFromCookie = cookieData.split("=");
        periodsFromCookie = periodsFromCookie[1].split(",");
    }
    catch { // Cookie was empty/didn't exist, default to all none
        periodsFromCookie = ["None", "None", "None", "None", "None", "None", "None"];
    }

    for (var j = 0; j <= periodsFromCookie.length - 1; j++) {
        // Set the default values for the classes
        document.getElementById(textIDs[j]).defaultValue = periodsFromCookie[j];
    }

    // Show or hide the 7 textboxes
    if (getComputedStyle(period).display === 'none') {
        fade("goToUtilities");
        unfade("submitPeriods");
        unfade("classes");
    }
    else {
        unfade("goToUtilities");
        fade("submitPeriods");
        fade("classes");
    }
}
// end: function openSettingsTab


// MARK: Utilities button

document.getElementById("goToUtilities").addEventListener("click", openUtilitiesTab);


// ====================================================================================================
// function openUtilitiesTab
//
// Opens the utilities menu
//
// Arguments--
//
// None
//
// Returns--
//
// None
//
function openUtilitiesTab() {
    var util = document.getElementById("utils");

    // Show or hide the utilities icons
    if (getComputedStyle(util).display === 'none') {
        fade("goToSettings");
        fade("classes");
        unfade("utils");
    }
    else {
        unfade("goToSettings");
        fade("classes");
        fade("utils");
    }
}
// end: function openUtilitiesTab
