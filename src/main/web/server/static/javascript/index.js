"use strict";


const PROGRESS_BAR = "progress_bar";
const TIME_REMAINING = "time_remaining";
const CURRENT_DURATION = "current_duration";
const END_TIME = "end_time";
const EXPIRE_TIME = "expire_time";


/**
 * Represents a duration of time.
 *
 * @author Jonathan Uhler
 */
class Duration {

    /** The number of minutes in one hour. */
    static MINUTES_PER_HOUR = 60;
    /** The number of seconds in one minute. */
    static SECONDS_PER_MINUTE = 60;
    /** The number of milliseconds in one second. */
    static MS_PER_SECOND = 1000;
    /** The number of milliseconds in one minute. */
    static MS_PER_MINUTE = Duration.MS_PER_SECOND * Duration.SECONDS_PER_MINUTE;
    /** The number of milliseconds in one hour. */
    static MS_PER_HOUR = Duration.MS_PER_MINUTE * Duration.MINUTES_PER_HOUR;


    /**
     * A private constructor to create a new {@code Duration} from a count of milliseconds.
     *
     * @param epoch  the number of milliseconds in this duration.
     */
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


    /**
     * Creates a new {@code Duration} between the current time and the provided end time.
     *
     * @param endTimeString  the ending time of the duration as an ISO timestamp in the format
     *                       yyyy-MM-dd'T'HH:mm:ss Z.
     *
     * @return a new {@code Duration} between the current time and the provided end time.
     */
    static fromEndTime(endTimeString) {
        endTimeString = endTimeString.trim().replaceAll(" ", "");

        let start = new Date().getTime();
        let end = Date.parse(endTimeString);
        let deltaEpoch = end - start;

        return new Duration(deltaEpoch);
    }


    /**
     * Creates a new {@code Duration} from a string.
     *
     * @param durationString  a string specifying a number of hours, minutes, and seconds
     *                        separated by colons.
     *
     * @return a new {@code Duration} from the specified string.
     */
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


    /**
     * Returns whether this duration is over.
     *
     * A duration is over when all time units (hours, minutes, etc) are less than or equal to zero.
     *
     * @return whether this duration is over.
     */
    isOver() {
        return this.hours <= 0 && this.minutes <= 0 && this.seconds <= 0 && this.millis <= 0;
    }


    /**
     * Returns the portion of this duration that has been completed by the provided sub-duration.
     *
     * @param remaining  another {@code Duration} object which represents the portion of time
     *                   not yet completed.
     *
     * @return the portion of this duration completed as a decimal between 0 and 1, where the
     *         portion not completed is equal to {@code remaining}.
     */
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


    /**
     * Returns a string representation of this duration.
     *
     * @return a string representation of this duration.
     */
    toString() {
        return String(this.hours).padStart(2, "0") + ":" +
            String(this.minutes).padStart(2, "0") + ":" +
            String(this.seconds).padStart(2, "0");
    }

}



/**
 * Reads the returns information about the time remaining from the DOM.
 *
 * @param doc  the DOM to read from. By default, this is {@code document}.
 *
 * @return the time remaining, current duration, end time, and expire time.
 */
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


/**
 * Updates the time remaining information for the current DOM.
 *
 * @param timeInfo  a structure containing the same information as the value returned from
 *                  {@code getTimeRemaining}.
 */
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


/**
 * Updates the time remaining information and polls the server for new information as needed.
 */
function updateTimeRemaining() {
    // Update time remaining
    let timeRemainingDiv = document.getElementById(TIME_REMAINING);
    if (timeRemainingDiv == null) {
        return;
    }
    let timeInfo = getTimeRemaining(document);

    let endTime = timeInfo[END_TIME];
    let expireTime = timeInfo[EXPIRE_TIME];
    let timeRemaining = Duration.fromEndTime(endTime);
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

    // Update progress bar
    let progressBar = document.getElementById(PROGRESS_BAR);
    if (progressBar != null) {
        let totalTime = timeInfo[CURRENT_DURATION];
        let currentDuration = Duration.fromDuration(totalTime);
        progressBar.value = currentDuration.portionComplete(timeRemaining);
    }

    // Update date/day display
    let now = new Date();
    let month = String(now.getMonth() + 1).padStart(2, "0");
    let date = String(now.getDate()).padStart(2, "0");
    let year = String(now.getFullYear()).slice(-2);
    let day = now.toLocaleDateString("en-US", {weekday: "long"});
    document.getElementById("day").innerHTML = day;
    document.getElementById("date").innerHTML = month + "/" + date + "/" + year;
}


setInterval(updateTimeRemaining, 1000);
