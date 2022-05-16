// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// TimeData.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/21/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package calendar;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class TimeData
//
// Period match data structure
//
public class TimeData {

    private int days; // Number of days
    private int hours; // Number of hours
    private int minutes; // Number of minutes
    private int seconds; // Number of seconds
    private int milliseconds; // Number of ms


    // ----------------------------------------------------------------------------------------------------
    // public TimeData
    //
    // Arguments--
    //
    // days:            the number of days
    //
    // hours:           the number of hours
    //
    // minutes:         the number of minutes
    //
    // seconds:         the number of seconds
    //
    // milliseconds:    the number of milliseconds
    //
    public TimeData(int days, int hours, int minutes, int seconds, int milliseconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
    }
    // end: public TimeData


    // ====================================================================================================
    // GET methods
    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMilliseconds() {
        return milliseconds;
    }
    // end: GET methods

}
// end: public class TimeData