/*
 * Calendar.js
 *
 * Calendar-related functions for the MVHS schedule app
 */
"use strict";
const enableDebug = false;
const enableTestCode = false;
const mvhsFirstDay = "2020-08-17";
const mvhsLastDay  = "2021-06-09";
const mvhs_Default_Week = [
  "mvhs_Weekend_Day",
  "mvhs_A_Day",
  "mvhs_B_Day",
  "mvhs_C_Day",
  "mvhs_A_Day",
  "mvhs_B_Day",
  "mvhs_Weekend_Day"
  ]
  const mvhs_MonHoliday_Week = [
    "mvhs_Weekend_Day",
    "mvhs_Holiday_Day",
    "mvhs_A_Day",
    "mvhs_B_Day",
    "mvhs_A_Day",
    "mvhs_B_Day",
    "mvhs_Weekend_Day"
    ]
const mvhs_Week_Exceptions = {
    "2020-09-06": mvhs_MonHoliday_Week
}


export default class Calendar {

  // Public methods
  //  new Calendar(startSDate, endSDate)
  //  createDateTag(eDate)
  //  getEndEDate()
  //  getStartEDate()
  //  getHeaderForWeek (dateTag)
  //  getTagList()

  // ===========================================================================
  // Calendar constructor
  //
  // Initialize a new Calendar Object
  //
  // Arguments:
  //  startSDate      Starting date for the new school year
  //  endSDate        Ending date for the new school year
  //  Both startDate and endDate are in the form "yyyy-[m]m-[d]d"

  constructor (startSDate, endSDate) {
    // Force the start and end dates to noon. This handles oddities around
    // daylight saving time
    this._startEDate = new Date(startSDate + "T12:00:00");
    this._endEDate = new Date(endSDate + "T12:00:00");

    // Generate the tag list for the school year
    this._tagList = _helperGenerateTagList (this, this._startEDate, this._endEDate);

    // Create a new calendar object and fill in the entries. _calendar is an
    // object indexed by the date tag for each week of the year
    this._calendar = new Object();

    // Default the values for each week
    for (const weekTag of this._tagList) {
      this._calendar[weekTag] = {
        "weekTag"   : weekTag,
        "eDate"     : new Date(weekTag + "T00:00:00"),
        "weekSched" : mvhs_Default_Week}

        // Handle the exceptions
        if (mvhs_Week_Exceptions[weekTag] !== undefined) {
          this._calendar[weekTag].weekSched =
            mvhs_Week_Exceptions[weekTag];
        }
      }

    return this;

    // Constructor helper functions
    //
    // _helperGenerateTagList
    //
    // Generate the date tags for each week in the school year and return all
    // tags in an array
    //
    // Arguments:
    //  ctx           "this" isn't available by name in a helper function,
    //                so it has to be passed in
    //  startEDate    The epoch date of the first day of the school year
    //  endEDate      The epoch date of the last day of the school year
    //
    // Returns:
    //  An array of the date tags for all weeks in the school year
    //  A date tag looks like "yyyy-mm-dd" and represents the Sunday of the
    //  start of the week.

    function _helperGenerateTagList (ctx, startEDate, endEDate) {
      const msPerWeek = 24*60*60*7*1000;

      // Start out the an empty array in which to return the tags for each weeks
      // in the school year
      let tags = [];

      // Begin with the start date argumment. Note that a simple assignment of
      // startEDate to weekEDate won't work because startEDate is an object and
      // the assignment will leave weekEDate pointing at the formal, which means
      // that it will be modified.
      let weekEDate = new Date(startEDate.getTime());

      // Loop from the initial value to the point where weekDate  is beyond the
      // ending date, call the tag generator for that date and push it onto the end
      // of the array to return
      while (weekEDate.getTime() < endEDate.getTime()) {
        tags.push (ctx.createDateTag(weekEDate))
        weekEDate.setTime(weekEDate.getTime() + msPerWeek);
      }

      return tags;
    } // _helperGenerateTagList

  }

  // ===========================================================================
  // createDateTag
  //
  // convert an epoch date value (e.g., from Date()) to a date tag
  // in the form yyyy-mm-dd. The date tag always represents the Sunday on or
  // before the date specified in the argument, so this is adjusted here.
  //
  // Arguments:
  //  eDate      epoch date value
  //
  // Returns:
  //  Date of the Sunday on or before that specified in the argument, as
  //  a string in the form yyyy-mm-dd. Note that both month and day are left-
  //  padded, if necessary, with a 0 to two digits.

  createDateTag (eDate) {
    const msPerDay = 24*60*60*1000;

    // Back up the date to the previous Sunday. It doesn't matter
    // what the time is on that Sunday, so backing up the date is as simple as
    // multiplying the .getDay() value by the number of ms per day
    var weekEDate = new Date(eDate.getTime());  // Make copy of eDate
    weekEDate.setTime(weekEDate.getTime() - (weekEDate.getDay() * msPerDay));

    // getMonth() returns 0..1 and needs to be adjusted to 1..12
    let month = weekEDate.getMonth() + 1;
    return (weekEDate.getFullYear() + "-" +
    month.toString().padStart(2,"0") + "-" +
    weekEDate.getDate().toString().padStart(2,"0"));
  }

  // ===========================================================================
  // getEndEDate
  //
  // Return the epoch value of the end date of the calendar (defined on the)
  // new Calendar call
  //
  // Arguments:
  //  None
  //
  // Returns
  //  Epoch date of the end of the calendar

  getEndEDate () {
    return new Date(this._endEDate.getTime());
  }

  // ===========================================================================
  // getStartEDate
  //
  // Return the epoch value of the start date of the calendar (defined on the)
  // new Calendar call
  //
  // Arguments:
  //  None
  //
  // Returns
  //  Epoch date of the start of the calendar

  getStartEDate () {
    return new Date(this._startEDate.getTime());
  }

  // ===========================================================================
  // getHeaderForWeek
  //
  // Return the header for a week based on the week tag
  //
  // Arguments:
  //  weekTag       Week tag for which to return the weekly schedule
  //
  // Returns
  //  Header for the week, or undefined if the week doesn't exist. The header is

  getHeaderForWeek (weekTag) {
    if (this._calendar[weekTag] === undefined) {
      return undefined;
    }
    return (this._calendar[weekTag]);
  }

  // ===========================================================================
  // getTagList
  //
  // Return the list of all tags in the calendar
  //
  // Arguments:
  //  None
  //
  // Returns
  //  List of all tags in the calendar

  getTagList () {
    let tagList = [];             // Create new array to return
    tagList = [...this._tagList]; // copy the values
    return tagList;
  }
}  // class Calendar



// Test code for local testing of the class


if (enableTestCode) {

    var calendar = new Calendar(mvhsFirstDay, mvhsLastDay);

    console.log("Start date is " + calendar.getStartEDate());
    console.log("End date is " + calendar.getEndEDate());
    let tagList = calendar.getTagList();
    console.log("Tag list for calendar is: [" + tagList + "]");
    let dateTag = calendar.createDateTag(new Date());
    console.log("Date tag for now is " + dateTag);

    for (const weekTag of tagList) {
	console.log ("Processing header for week " + weekTag)
	let header = calendar.getHeaderForWeek (weekTag);
	if (header === undefined) {
	    console.log ("ERROR: Undefined header for week " + weekTag)
	} else {
	    console.log ("Header.weekTag = " + header.weekTag);
	    console.log ("Header.eDate = " + header.eDate);
	    console.log ("Weekly schedule:");
	    let ws = header.weekSched;
	    for (const day of ws) {
		console.log ("Sched: " + day)
	    }
	}
    }
}
