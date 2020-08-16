var canvas = document.getElementById('Periods');
var context = canvas.getContext('2d');

// Define what a period is
function periodDescr(name, periodNum, startH, startM, startS, endH, endM, endS) {
    this.name = name;
    this.periodNum = periodNum;
    this.startH = startH;
    this.startM = startM;
    this.startS = startS;
    this.endH = endH;
    this.endM = endM;
    this.endS = endS;
}

// Define all 7 periods
var P1 = new periodDescr (
    "Biology",
    1,
    9, 30, 59,
    10, 45, 59
)
var P1T = new periodDescr (
    P1.name,
    P1.periodNum,
    9, 30, 59,
    10, 0, 59
)
var P2 = new periodDescr (
    "PE",
    2,
    9, 30, 0,
    10, 44, 59
)
var P2T = new periodDescr (
    P2.name,
    P2.periodNum,
    10, 10, 0,
    10, 39, 59
)
var P3 = new periodDescr (
    "World Studies",
    3,
    11, 0, 0,
    12, 14, 59
)
var P3T = new periodDescr (
    P3.name,
    P3.periodNum,
    10, 50, 0,
    11, 19, 59
)
var P4 = new periodDescr (
    "English",
    4,
    9, 30, 0,
    10, 44, 59
)
var P4T = new periodDescr (
    P4.name,
    P4.periodNum,
    11, 30, 0,
    11, 59, 59
)
var P5 = new periodDescr (
    "Geometry",
    5,
    13, 5, 0,
    14, 19, 59
)
var P5T = new periodDescr (
    P5.name,
    P5.periodNum,
    13, 0, 0,
    13, 29, 59
)
var P6 = new periodDescr (
    "Free",
    6,
    13, 5, 0,
    14, 19, 59
)
var P6T = new periodDescr (
    P6.name,
    P6.periodNum,
    13, 40, 0,
    14, 9, 59
)
var P7 = new periodDescr (
    "Free",
    7,
    14, 30, 0,
    15, 34, 59
)
var P7T = new periodDescr (
    P7.name,
    P7.periodNum,
    14, 20, 0,
    14, 49, 59
)
var PFree = new periodDescr (
    "No School",
    -1,
    0, 0, 0,
    23, 59, 59
)

var oddDay = [
    P1,
    P3,
    P5,
    P7
]
var evenDay = [
    P2,
    P4,
    P6
]
var tutorialDay = [
    P1T,
    P2T,
    P3T,
    P4T,
    P5T,
    P6T,
    P7T
]
var freeDay = [
    PFree
]
// Get the current date
var currentDate = new Date();
var day = currentDate.getDay();
var hour = currentDate.getHours();
var minute = currentDate.getMinutes();
var second = currentDate.getSeconds();
var textPos = {
    x: 10,
    y: 35,
    xOffset: 10,
    yOffset: 70
}

// Get the current day
switch (day) {
    case 1: // Monday
    case 4: // Thursday
        printDay(oddDay);
        break;

    case 2: // Tuesday
    case 5: // Friday
        printDay(evenDay);
        break;

    case 3: // Wednesday
        printDay(tutorialDay);
        break;

    default: // Saturday, Sunday
        printDay(freeDay);
        break;
}

function printDay(dayObj) {
    dayObj.forEach(element => checkTimeAndPrint(element.name, element.periodNum, hour, minute, second, element.startH, element.startM, element.startS, element.endH, element.endM, element.endS)); 
}

function checkTimeAndPrint (n, p, h, m, s, sH, sM, sS, eH, eM, eS) {
    if (checkTime(h, m, s, sH, sM, sS, eH, eM, eS)) {
        printPeriod(n, p);
        timeLeft(h, m, s, eH, eM, eS);
    }
}  

// Check if the current time is in the range of a class
function checkTime(h, m, s, sH, sM, sS, eH, eM, eS) {
    if ((h > sH && h < eH) || (h === sH && m >= sM) || (h === eH && m <= eM) || (m === sM && s >= sS) || (m === eM && s <= eS)) {
        return true;
    }
    else {
        return false;
    }
}

// Called to print the current period on the screen
function printPeriod(periodName, periodNum) {
    context.fillStyle = 'black';
    context.font = "35px Arial";
    if (periodNum === -1) {
        context.fillText(periodName + "  |  Free", textPos.x, textPos.y)
    }
    else if (periodName === "Free" || periodName === "None") {
        context.fillText("No School  |  " + periodName, textPos.x, textPos.y)
    }
    else {
        context.fillText(periodName + "  |  " + "Period " + periodNum, textPos.x, textPos.y)
    }
}


function timeLeft(h, m, s, eH, eM, eS) {
    var sumH, sumM, sumS;
    sumH = eH - h;
    sumM = eM - m;
    sumS = eS - s;

    context.fillStyle = 'black';
    context.font = "59px Arial";
    if (sumM <= 9 && sumS <= 9) {
        context.fillText(sumH + ":0" + sumM + ":0" + sumS, textPos.x - textPos.xOffset, textPos.y + textPos.yOffset)
    }
    else if (sumM <= 9) {
        context.fillText(sumH + ":0" + sumM + ":" + sumS, textPos.x - textPos.xOffset, textPos.y + textPos.yOffset)
    }
    else if (sumS <= 9) {
        context.fillText(sumH + ":" + sumM + ":0" + sumS, textPos.x - textPos.xOffset, textPos.y + textPos.yOffset)
    }
    else {
        context.fillText(sumH + ":" + sumM + ":" + sumS, textPos.x - textPos.xOffset, textPos.y + textPos.yOffset)
    }
} 
