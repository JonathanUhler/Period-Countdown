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

let SettingsVersion = "0.0.0";

// Revision History
//
//  version    date                     Change
//  ------- ----------  --------------------------------------------------------
//

// Version information
this.Version = SettingsVersion
console.log("Settings v" + this.Version);


// openSettingsTab();

// function openSettingsTab() {
//     window.open("/Users/jonathan/Documents/VS\ Code/Period/popup.html", "_blank");
// }

document.getElementById("goToSettings").addEventListener("click", openSettingsTab);

function openSettingsTab() {
    window.open("file:///Users/jonathan/Documents/VS%20Code/Period/popup.html", "_blank")
}

// function openSettings() {
//     var n = document.getElementById("settingsClose");
//     if (n.style.display === "none") {
//         n.style.display = "block";
//     } 
// }
// function closeSettings() {
//     var s = document.getElementById("settingsClose");
//     if (s.style.display === "block") {
//         s.style.display = "none";
//     } 
// }