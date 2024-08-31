"use strict";


const PROGRESS_BAR = "progress_bar";
const TIME_REMAINING = "time_remaining";
const CURRENT_DURATION = "current_duration";
const END_TIME = "end_time";
const EXPIRE_TIME = "expire_time";


class Duration {

    static MINUTES_PER_HOUR = 60;
    static SECONDS_PER_MINUTE = 60;
    static MS_PER_SECOND = 1000;
    static MS_PER_MINUTE = Duration.MS_PER_SECOND * Duration.SECONDS_PER_MINUTE;
    static MS_PER_HOUR = Duration.MS_PER_MINUTE * Duration.MINUTES_PER_HOUR;


    constructor(epoch) {
        if (epoch <= 0) {
            this.hours = 0;
            this.minutes = 0;
            this.seconds = 0;
            this.millis = 0;
        }
        else {
	    this.hours = Math.floor(epoch / Duration.MS_PER_HOUR);
	    epoch -= this.hours * Duration.MS_PER_HOUR;
	    this.minutes = Math.floor(epoch / Duration.MS_PER_MINUTE);
	    epoch -= this.minutes * Duration.MS_PER_MINUTE;
	    this.seconds = Math.floor(epoch / Duration.MS_PER_SECOND);
	    epoch -= this.seconds * Duration.MS_PER_SECOND;
	    this.millis = Math.floor(epoch);
        }
    }


    static fromEndTime(endTimeString) {
        endTimeString = endTimeString.trim().replaceAll(" ", "");

        let start = new Date().getTime();
        let end = Date.parse(endTimeString);
        let deltaEpoch = end - start;

        return new Duration(deltaEpoch);
    }


    static fromDuration(durationString) {
        let components = durationString.split(":");
        if (components.length != 3) {
            throw new Error("invalid duration string: " + durationString);
        }

        let hours = parseInt(components[0]);
        let minutes = parseInt(components[1]);
        let seconds = parseInt(components[2]);
        let epoch =
            hours * Duration.MS_PER_HOUR +
            minutes * Duration.MS_PER_MINUTE +
            seconds * Duration.MS_PER_SECOND;

        return new Duration(epoch);
    }


    isOver() {
        return this.hours <= 0 && this.minutes <= 0 && this.seconds <= 0 && this.millis <= 0;
    }


    portionComplete(remaining) {
        let totalMillis =
            this.hours * Duration.MS_PER_HOUR +
            this.minutes * Duration.MS_PER_MINUTE +
            this.seconds * Duration.MS_PER_SECOND +
            this.millis;
        let remainingMillis =
            remaining.hours * Duration.MS_PER_HOUR +
            remaining.minutes * Duration.MS_PER_MINUTE +
            remaining.seconds * Duration.MS_PER_SECOND +
            remaining.millis;

        return 1.0 - remainingMillis / totalMillis;
    }


    toString() {
        return String(this.hours).padStart(2, "0") + ":" +
            String(this.minutes).padStart(2, "0") + ":" +
            String(this.seconds).padStart(2, "0");
    }

}


function getTimeRemaining(doc = document) {
    let timeRemaining = doc.getElementById(TIME_REMAINING).innerHTML;
    let currentDuration = doc.getElementById(CURRENT_DURATION).innerHTML;
    let endTime = doc.querySelector('meta[name="' + END_TIME + '"]').getAttribute("content");
    let expireTime = doc.querySelector('meta[name="' + EXPIRE_TIME + '"]').getAttribute("content");

    return {
	[TIME_REMAINING]: timeRemaining,
        [CURRENT_DURATION]: currentDuration,
	[END_TIME]: endTime,
	[EXPIRE_TIME]: expireTime
    };
}


function setTimeRemaining(timeInfo) {
    let timeRemaining = timeInfo[TIME_REMAINING];
    let currentDuration = timeInfo[CURRENT_DURATION];
    let endTime = timeInfo[END_TIME];
    let expireTime = timeInfo[EXPIRE_TIME];

    document.getElementById(TIME_REMAINING).innerHTML = timeRemaining;
    document.getElementById(CURRENT_DURATION).innerHTML = currentDuration;
    document.querySelector('meta[name="' + END_TIME + '"]').setAttribute("content", endTime);
    document.querySelector('meta[name="' + EXPIRE_TIME + '"]').setAttribute("content", expireTime);
}


function updateTimeRemaining() {
    let timeRemainingDiv = document.getElementById(TIME_REMAINING);
    let progressBar = document.getElementById(PROGRESS_BAR);
    if (timeRemainingDiv == null) {
        return;
    }
    let timeInfo = getTimeRemaining(document);

    let totalTime = timeInfo[CURRENT_DURATION];
    let endTime = timeInfo[END_TIME];
    let expireTime = timeInfo[EXPIRE_TIME];
    let timeRemaining = Duration.fromEndTime(endTime);
    let currentDuration = Duration.fromDuration(totalTime);
    let timeValid = Duration.fromEndTime(expireTime);

    if (timeValid.isOver() || timeRemaining.isOver()) {
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
    progressBar.value = currentDuration.portionComplete(timeRemaining);
}


setInterval(updateTimeRemaining, 1000);
