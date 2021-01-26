// settings.js
//
// Coordination of information from Calendar.js and display.js to allow the user to change settings in a web-frontend

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

let SettingsVersion = "1.0.0";

// Revision History
//
//  version    date                     Change
//  ------- ----------  --------------------------------------------------------
//  1.0.0   1/24/2021   -First working version of settings.js
//
//  2.0.0   1/24/2021   Changes in this version:
//                          -File I/O replaced with cookies
//                          -Default classes set to be "None" for all 7 periods
//                          -Issues with cookies fixed
//                          -Textboxes will now display default values
//
// 3.0.0    1/26/2021   Changes in this version:
//                          -Minimized and cleaned up code for settings
//                          -Rewrote HTML settings elements to minimize code
//                          -Added in functions for utilities
//                          -Added support for utilities
//                          -Condensed utilities and classes into 2 divs

// Version information
this.Version = SettingsVersion
console.log("Settings v" + this.Version);


// ===========================================================================
// function SettingsMessage
//
// Function to emit a message, with optional arguments, which are separated
// by ", "
//
// Arguments--
//
// msg:        Message
//
// args:       Optional list of arguments to output
//
// Returns--
//
// None
//
function SettingsMessage(msg, ...args) {

    let message = "Settings Message: " + msg
    if (args.length > 0) {
      message += " " + args.join(", ")
    }

    console.log(message)

} // end function SettingsMessage


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
// ---------------------
// SETTINGS BUTTON
// ---------------------
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+


var hideElems = ["classes", "submitPeriods", "utils"]
var userClassDefaults = []
// HTML class textbox element IDs
var textIDs = ["P1Text", "P2Text", "P3Text", "P4Text", "P5Text", "P6Text", "P7Text"]
// Settings button icon
document.getElementById("goToSettings").addEventListener("click", openSettingsTab)

// Make the boxes start hidden until the settings button is pressed
for (var i = 0; i < hideElems.length; i++) {
    document.getElementById(hideElems[i]).style.display = 'none'
}

// Button to save the periods
var submitButton = document.getElementById('submitPeriods')
submitButton.addEventListener("click", saveUserDefaults, false)


// =============================================================================
// function saveUserDefaults()
//
// A function that is controlled by the "Save Classes" button and saves all
// choices into an array
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
    userClassDefaults = []

    // Save each of the strings in the textboxes to an array
    for (var i = 0; i <= textIDs.length - 1; i++) {

        var classChoice = document.getElementById(textIDs[i]).value
        if (classChoice.toLowerCase() === "none" || classChoice.toLowerCase() === "n/a") {
            userClassDefaults.push("Free")
        }
        else {
            userClassDefaults.push(classChoice)
        }

    }

    // Debug: display all the choices
    SettingsMessage(`New Classes:`, userClassDefaults)

    // Write the choices to a file to be read by MVHS.js
    removeCookie("periods")
    createCookie("periods", userClassDefaults)

} // end: function saveUserDefaults


// =============================================================================
// function removeCookie
//
// A function to remove an existing cookie by setting its expiration date to the
// past
//
// Arguments--
//
// name:            the name of the cookie to remove
//
// Returns--
//
// None
//
function removeCookie(name) {

    var removeStatement = name + "= ; expires = Thu, 01 Jan 1970 00:00:00 GMT"
    document.cookie = removeStatement

} // end: function removeCookie


// =============================================================================
// function createCookie
//
// A function to create a new cookie
//
// Arguments--
//
// name:            the name of the new cookie
//
// value:           the value that the cookie should have
//
// Returns--
//
// None
//
function createCookie(name, value) {

    var newCookie = name + "=" + value
    document.cookie = newCookie

    SettingsMessage("New cookie data:", document.cookie)

} // end: createCookie


// =============================================================================
// function openSettingsTab()
//
// A function to show and hide the text boxes where class names can be entered
//
// Arguments--
//
// idArray:     the array of class div ids
//
// Returns--
//
// None
//
function openSettingsTab() {

    var period = document.getElementById("classes")

    // Read in cookie data
    try {
        var cookieData = document.cookie
        var periodsFromCookie = cookieData.split("=")
        periodsFromCookie = periodsFromCookie[1].split(",")
    }
    catch {
        periodsFromCookie = ["None", "None", "None", "None", "None", "None", "None"]
    }

    for (var j = 0; j <= periodsFromCookie.length - 1; j++) {
        // Set the default values for the classes
        document.getElementById(textIDs[j]).defaultValue = periodsFromCookie[j];
    }

    // Show or hide the 7 textboxes
    if (getComputedStyle(period).display === 'none') {
        document.getElementById("goToUtilities").style.display = 'none'
        document.getElementById("submitPeriods").style.display = ''
        document.getElementById("classes").style.display = ''
    }
    else {
        document.getElementById("goToUtilities").style.display = ''
        document.getElementById("submitPeriods").style.display = 'none'
        document.getElementById("classes").style.display = 'none'
    }

} // end: function openSettingsTab


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
// ---------------------
// UTILITIES BUTTON
// ---------------------
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+


document.getElementById("goToUtilities").addEventListener("click", openUtilitiesTab)


function openUtilitiesTab() {

    var util = document.getElementById("utils")

    // Show or hide the utilities icons
    if (getComputedStyle(util).display === 'none') {
        document.getElementById("goToSettings").style.display = 'none'
        document.getElementById("classes").style.display = 'none'
        document.getElementById("utils").style.display = ''
    }
    else {
        document.getElementById("goToSettings").style.display = ''
        document.getElementById("classes").style.display = 'none'
        document.getElementById("utils").style.display = 'none'
    }

}

