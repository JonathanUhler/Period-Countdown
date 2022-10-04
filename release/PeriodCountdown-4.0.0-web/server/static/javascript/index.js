"use strict";


const END_TIME = "end_time";
const TIME_REMAINING = "time_remaining";
const STATUS = "status";
const NEXT_UP = "next_up";



class Duration {

	static DAYS_PER_YEAR = 365;
	static DAYS_PER_WEEK = 7;
	static HOURS_PER_DAY = 24;
	static MINUTES_PER_HOUR = 60;
	static SECONDS_PER_MINUTE = 60;
	static MS_PER_SECOND = 1000;
	static MS_PER_MINUTE = Duration.MS_PER_SECOND * Duration.SECONDS_PER_MINUTE;
	static MS_PER_HOUR = Duration.MS_PER_MINUTE * Duration.MINUTES_PER_HOUR;
	

	constructor(endTimeStr) {
		if (typeof endTimeStr !== "string")
			throw new TypeError("unexpected type: should be (string), found (" + (typeof endTimeStr) + ")");

		// Remove any spaces (such as between the timestamp and zone ID) as these aren't allowed in Date.parse
		endTimeStr = endTimeStr.replaceAll(" ", "");

		var start = new Date().getTime();
		var end = Date.parse(endTimeStr);

		var deltaEpoch = end - start;

		this.hours = 0;
		this.minutes = 0;
		this.seconds = 0;
		this.millis = 0;
		
		if (deltaEpoch > 0) {
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
		return (this.hours <= 0 &&
			    this.minutes <= 0 &&
			    this.seconds <= 0);
	}


	toString() {
		if (this.hours == 0)
			return String(this.minutes).padStart(2, "0") + ":" +
			       String(this.seconds).padStart(2, "0");
		else
			return this.hours + ":" +
			       String(this.minutes).padStart(2, "0") + ":" +
			       String(this.seconds).padStart(2, "0");
	}
	
}



function getComponents() {
	var endTime = document.querySelector('meta[name="' + END_TIME + '"]').getAttribute("content");
	var status = document.getElementById(STATUS).innerHTML;
	var timeRemaining = document.getElementById(TIME_REMAINING).innerHTML;
	var nextUp = document.getElementById(NEXT_UP).innerHTML;

	return {
		[END_TIME]: endTime,
		[STATUS]: status,
		[TIME_REMAINING]: timeRemaining,
		[NEXT_UP]: nextUp
	};
}


function setComponents(endTime, status, timeRemaining, nextUp) {
	if (typeof endTime !== "string" ||
		typeof status !== "string" ||
		typeof timeRemaining !== "string" ||
		typeof nextUp !== "string")
		throw new TypeError("unexpected type: should be (string, string, string, string), found (" +
							(typeof endTime) + ", " +
							(typeof status) + ", " +
							(typeof timeRemaining) + ", " +
							(typeof nextUp) + ")");

	document.querySelector('meta[name="' + END_TIME + '"]').setAttribute("content", endTime);
	document.getElementById(STATUS).innerHTML = status;
	document.getElementById(TIME_REMAINING).innerHTML = timeRemaining;
	document.getElementById(NEXT_UP).innerHTML = nextUp;
}


function updateTimeRemaining() {
	var timeRemainingDiv = document.getElementById("time_remaining");

	var components = getComponents();
	var endTime = components[END_TIME];
	var timeRemaining = new Duration(endTime);

	if (timeRemaining.isOver()) {
		fetch("/").then(function (response) {
			return response.text(); // Return HTML from the server
		}).then(function (html) {
			// Load the new html
			var parser = new DOMParser();
			var newDoc = parser.parseFromString(html, "text/html");

			setComponents(newDoc.querySelector('meta[name="' + END_TIME + '"]').getAttribute("content"),
						  newDoc.getElementById(STATUS).innerHTML,
						  newDoc.getElementById(TIME_REMAINING).innerHTML,
						  newDoc.getElementById(NEXT_UP).innerHTML);
		}).catch(function (error) {
			throw new Error("error fetching updated information from server: " + error);
		})
		return;
	}

	timeRemainingDiv.getElementsByTagName("p")[0].innerHTML = timeRemaining.toString();
}


setInterval(updateTimeRemaining, 1000);
