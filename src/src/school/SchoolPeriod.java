// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolPeriod.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/22/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import calendar.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolPeriod
//
// Defines the structure for each period of the day
//
public class SchoolPeriod {

    private final int period; // Integer period number of the class
    private final String name; // The name of the class
    private final String startTime; // The time, in format hh:mm the class starts at
    private final String endTime; // The time, in format hh:mm the class ends at
    private final SchoolClass classInfo; // The SchoolClass object that describes this period
    private final String periodDescription; // An optional comment/description about this period

    private final TimeData startDayAdjust; // A hashmap containing the hour value and minute value for the start of day adjust
    private final TimeData endDayAdjust; // A hashmap containing the hour value and minute value for the end of day adjust


    // ====================================================================================================
    // private TimeData getStringTimeComponents
    //
    // Returns a TimeData object representing the specified stringTime
    //
    // Arguments--
    //
    // stringTime:      the formatted string to get the time components from
    //
    // isEndTime:       whether the stringTime is the end of a period, if true the time needs to be adjusted
    //
    // endOfDayAdjust:  whether the stringTime represents the end of day time, which gets cast to 1ms before
    //                  midnight
    //
    // errorMessage:    the error message to print if an error occurs
    //
    // Returns--
    //
    // A TimeData object that represents a time based on stringTime under a few conditions:
    //  If isEndTime is true, the time returned is 1 ms before stringTime (04:32 --> 04:31:59:999)
    //  If isEndTime is true and endOfDayAdjust is true, the time is 1ms before the end of the day (eg
    //  11:59:59:999)
    //  Otherwise the time returned is the hours and minutes of stringTime without any sec/ms
    //
    private TimeData getStringTimeComponents(String stringTime, boolean isEndTime,
                                             boolean endOfDayAdjust, String errorMessage) throws Exception {
        CalendarHelper.calendarAssert((stringTime.contains(":")) && (stringTime.length() == 5),
                "SchoolDay.getStringTimeComponents called with invalid stringTime",
                stringTime);

        String[] timeSplit = stringTime.split(":");
        int hours = Integer.parseInt(timeSplit[0]);
        int minutes = Integer.parseInt(timeSplit[1]);

        CalendarHelper.calendarAssert((hours >= 0) &&
                        (hours < SchoolCalendar.hoursPerDay) &&
                        (minutes >= 0) &&
                        (minutes < SchoolCalendar.minutesPerHour),
                "SchoolDay.getStringTimeComponents found incorrect stringTime formatting",
                stringTime, errorMessage);

        // Handle the special conditions of isEndTime and endOfDayAdjust
        // If isEndTime is true, then the time returned must be 1 ms before stringTime in order to avoid
        // millisecond overlap
        // If endOfDayAdjust is true, then the time returned should always be 1 ms before midnight no matter
        // what stringTime is
        if (isEndTime) {
            // In order to return the time 1ms before stringTime, 1 minute (at minimum) must be removed. If
            // minutes was already 0, then it must be set back to 59 and hours must be 1 less
            minutes -= 1;
            if (minutes < 0) {
                hours -= 1;
                minutes = SchoolCalendar.minutesPerHour - 1;
            }
            return new TimeData(0,
                    hours, minutes,
                    SchoolCalendar.secondsPerMinute - 1,
                    SchoolCalendar.millisecondsPerSecond - 1);
        }
        if (endOfDayAdjust)
            return new TimeData(0,
                    SchoolCalendar.hoursPerDay - 1,
                    SchoolCalendar.minutesPerHour - 1,
                    SchoolCalendar.secondsPerMinute - 1,
                    SchoolCalendar.millisecondsPerSecond - 1);

        // The normal case if both isEndTime and endOfDayAdjust are false is to return the time specified
        // in stringTime
        return new TimeData(0, hours, minutes, 0, 0);
    }
    // end: private TimeData getStringTimeCompoenents


    // ----------------------------------------------------------------------------------------------------
    // public SchoolPeriod
    //
    // Arguments--
    //
    // period:          the period number
    //
    // name:            the name of the class
    //
    // startTime:       the start ime of the class, in format hh:mm
    //
    // endTime:         the end time of the class, in format hh:mm
    //
    // endOfDayAdjust:  whether the period is the last of the day
    //
    // classInfo:       the SchoolClass object describing the class
    //
    public SchoolPeriod(int period, String name, String startTime, String endTime, boolean endOfDayAdjust, SchoolClass classInfo) throws Exception {
        this(period, name, startTime, endTime, endOfDayAdjust, classInfo, "");
    }
    // end: public SchoolPeriod


    // ----------------------------------------------------------------------------------------------------
    // public SchoolPeriod
    //
    // Arguments--
    //
    // period:              the period number
    //
    // name:                the name of the class
    //
    // startTime:           the start ime of the class, in format hh:mm
    //
    // endTime:             the end time of the class, in format hh:mm
    //
    // endOfDayAdjust:      whether the period is the last of the day
    //
    // classInfo:           the SchoolClass object describing the class
    //
    // periodDescription:   an optional comment/description about the class
    //
    public SchoolPeriod(int period, String name, String startTime, String endTime, boolean endOfDayAdjust, SchoolClass classInfo, String periodDescription) throws Exception {
        CalendarHelper.calendarAssert((period == SchoolCalendar.fakePeriod || period == SchoolCalendar.freePeriod || (period >= SchoolCalendar.getFirstPeriod() && period <= SchoolCalendar.getLastPeriod())) &&
                        (name != null) &&
                        (startTime != null) &&
                        (endTime != null),
                "SchoolPeriod.SchoolPeriod constructed with invalid arguments",
                String.valueOf(period), name, startTime, endTime, String.valueOf(endOfDayAdjust), String.valueOf(classInfo));

        this.period = period;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classInfo = (classInfo == null || classInfo.getClassName() == null) ? null : classInfo;
        this.periodDescription = periodDescription;

        String errorMessage = period + " | " + name + " | " + startTime +
                " | " + endTime + " | " + periodDescription;
        this.startDayAdjust = getStringTimeComponents(startTime, false,
                false, errorMessage);
        this.endDayAdjust = getStringTimeComponents(endTime, true, endOfDayAdjust, errorMessage);
    }
    // end: public SchoolPeriod


    // ====================================================================================================
    // GET methods
    public int getPeriod() {
        return period;
    }

    public String getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public SchoolClass getClassInfo() {
        return classInfo;
    }

    public String getPeriodDescription() {
        return periodDescription;
    }

    public TimeData getStartDayAdjust() {
        return startDayAdjust;
    }

    public TimeData getEndDayAdjust() {
        return endDayAdjust;
    }
    // end: GET methods


    // ====================================================================================================
    // public String toString
    //
    // SchoolPeriod toString method
    //
    // Returns--
    //
    // Textual representation of this class
    //
    @Override
    public String toString() {
        String classDescription = "\n\tperiod:\t" + this.getPeriod() + "\n\tname:\t" + this.getName() +
                "\n\tstartTime:\t" + this.getStartTime() + "\n\tendTime:\t" + this.getEndTime() +
                "\n\tclassInfo:\n\t\t" + this.getClassInfo();
        return super.toString() + ": " + classDescription;
    }
    // end: public String toString
}
// end: public class SchoolPeriod