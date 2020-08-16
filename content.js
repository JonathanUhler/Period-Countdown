var canvas = document.getElementById('Periods');
var context = canvas.getContext('2d');

// Define what a period is
function periodDescr(name, start, end, tStart, tEnd) {
    this.name = name;
    this.start = start;
    this.end = end;
    this.tStart = tStart;
    this.tEnd = tEnd;
}

// Define all 7 periods
var P1 = new periodDescr (
    "Biology",
    0930,
    1045,
    0930,
    1000
)
var P2= new periodDescr (
    "PE",
    0930,
    1045,
    1010,
    1040
)
var P3 = new periodDescr (
    "World Studies",
    1100,
    1215,
    1050,
    1120
)
var P4 = new periodDescr (
    "English",
    0930,
    1045,
    1130,
    1200
)
var P5 = new periodDescr (
    "Geometry",
    1305,
    1420,
    1300,
    1330
)
var P6 = new periodDescr (
    "Free",
    1305,
    1420,
    1340,
    1410
)
var P7 = new periodDescr (
    "Free",
    1430,
    1535,
    1420,
    1450
)
var PFree = new periodDescr (
    "Weekend",
    0000,
    2359,
    0000,
    2359
)

// Which periods are in which day
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
    P1,
    P2,
    P3,
    P4,
    P5,
    P6,
    P7
]
var weekend = [
    PFree
]

console.debug(P1);
