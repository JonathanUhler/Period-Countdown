"use strict";


const END_TIME = "end_time";
const EXPIRE_TIME = "expire_time";
const TIME_REMAINING = "time_remaining";


class Duration {

    static MINUTES_PER_HOUR = 60;
    static SECONDS_PER_MINUTE = 60;
    static MS_PER_SECOND = 1000;
    static MS_PER_MINUTE = Duration.MS_PER_SECOND * Duration.SECONDS_PER_MINUTE;
    static MS_PER_HOUR = Duration.MS_PER_MINUTE * Duration.MINUTES_PER_HOUR;


    constructor(endTimeString) {
        endTimeString = endTimeString.trim().replaceAll(" ", "");

        let start = new Date().getTime();
        let end = Date.parse(endTimeString);
        let deltaEpoch = end - start;

        if (deltaEpoch <= 0) {
            this.hours = 0;
            this.minutes = 0;
            this.seconds = 0;
            this.millis = 0;
        }
        else {
	    this.hours = Math.floor(deltaEpoch / Duration.MS_PER_HOUR);
	    deltaEpoch -= this.hours * Duration.MS_PER_HOUR;
	    this.minutes = Math.floor(deltaEpoch / Duration.MS_PER_MINUTE);
	    deltaEpoch -= this.minutes * Duration.MS_PER_MINUTE;
	    this.seconds = Math.floor(deltaEpoch / Duration.MS_PER_SECOND);
	    deltaEpoch -= this.seconds * Duration.MS_PER_SECOND;
	    this.millis = Math.floor(deltaEpoch);
        }
    }


    isOver() {
        return this.hours <= 0 && this.minutes <= 0 && this.seconds <= 0 && this.millis <= 0;
    }


    toString() {
        return String(this.hours).padStart(2, "0") + ":" +
            String(this.minutes).padStart(2, "0") + ":" +
            String(this.seconds).padStart(2, "0");
    }

}


function getTimeRemaining(doc = document) {
    let timeRemaining = doc.getElementById(TIME_REMAINING).innerHTML;
    let endTime = doc.querySelector('meta[name="' + END_TIME + '"]').getAttribute("content");
    let expireTime = doc.querySelector('meta[name="' + EXPIRE_TIME + '"]').getAttribute("content");

    return {
	[TIME_REMAINING]: timeRemaining,
	[END_TIME]: endTime,
	[EXPIRE_TIME]: expireTime
    };
}


function setTimeRemaining(timeInfo) {
    let timeRemaining = timeInfo[TIME_REMAINING];
    let endTime = timeInfo[END_TIME];
    let expireTime = timeInfo[EXPIRE_TIME];

    document.getElementById(TIME_REMAINING).innerHTML = timeRemaining;
    document.querySelector('meta[name="' + END_TIME + '"]').setAttribute("content", endTime);
    document.querySelector('meta[name="' + EXPIRE_TIME + '"]').setAttribute("content", expireTime);
}


function updateTimeRemaining() {
    let timeRemainingDiv = document.getElementById(TIME_REMAINING);
    if (timeRemainingDiv == null) {
        return;
    }
    let timeInfo = getTimeRemaining(document);

    let endTime = timeInfo[END_TIME];
    let expireTime = timeInfo[EXPIRE_TIME];
    let timeRemaining = new Duration(endTime);
    let timeValid = new Duration(expireTime);

    if (timeValid.isOver()) {
        window.location.reload(true);
        return;
    }
    if (timeRemaining.isOver()) {
        fetch("/").then(function (response) {
            return response.text();
        }).then(function (newHtml) {
            let parser = new DOMParser();
            let newDocument = parser.parseFromString(newHtml, "text/html");
            let newTimeInfo = getTimeRemaining(newDocument);
            setTimeRemaining(newTimeInfo);
        }).catch(function (error) {
            throw new Error("error fetching updated information from server: " + error);
        });
    }

    timeRemainingDiv.innerHTML = timeRemaining.toString();
}


setInterval(updateTimeRemaining, 1000);
