var canvas = document.getElementById('Periods');
var context = canvas.getContext('2d');

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
var dayMap = [
    freeDay,
    oddDay,
    evenDay,
    tutorialDay,
    oddDay,
    evenDay,
    freeDay
]

var currentDate, today, tomorrow, hour, minute, second;
var textPos = {
    x: 10,
    y: 35,
    xOffset: 0,
    yOffset: 70
}

enableTimer();
function enableTimer () {
    setInterval(function () {
        gatherDayInfo();
    }, 1)
}

function gatherDayInfo() {
    context.clearRect(0, 0, canvas.width, canvas.height);
    currentDate = new Date();
    today = currentDate.getDay();
    tomorrow = (today + 1) % 7;
    hour = currentDate.getHours();
    minute = currentDate.getMinutes();
    second = currentDate.getSeconds();
    var todayObj = dayMap[today];
    var tomorrowObj = dayMap[tomorrow];
    printDay(todayObj, tomorrowObj);
}

function printDay(todayObj, tomorrowObj) {
    todayObj.forEach(element => checkTimeAndPrint(element.name, element.periodNum, hour, minute, second, element.startH, element.startM, element.startS, element.endH, element.endM, element.endS, today)); 
}

function checkTimeAndPrint (n, p, h, m, s, sH, sM, sS, eH, eM, eS, type) {
    if (checkTime(h, m, s, sH, sM, sS, eH, eM, eS)) {
        printPeriod(n, p);
        timeLeft(h, m, s, eH, eM, eS, type);
    }
}  

function checkTime(h, m, s, sH, sM, sS, eH, eM, eS) {
    if ((h > sH && h < eH) || (h === sH && m >= sM) || (h === eH && m <= eM) || (m === sM && s >= sS) || (m === eM && s <= eS)) {
        return true;
    }
    else {
        return false;
    }
}

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


function timeLeft(h, m, s, eH, eM, eS, type) {
    var sumH, sumM, sumS;
    sumH = eH - h;
    sumM = eM - m;
    sumS = eS - s;

    if (type === 0) {
        sumH += P1.startH;
        sumM += P1.startM;
    }
    else if (type === 6) {
        sumH += P1.startH + 24;
        sumM += P1.startM;
    }

    if (sumM >= 60) {
        sumH++;
        sumM -= 60;
    }
    if (sumS >= 60) {
        sumM++;
        sumS -= 60;
    }

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
