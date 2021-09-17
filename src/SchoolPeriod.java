// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolPeriod.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/22/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import java.util.HashMap;


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

    private final HashMap<String, Integer> startDayAdjust; // A hashmap containing the hour value and minute value for the start of day adjust
    private final HashMap<String, Integer> endDayAdjust; // A hashmap containing the hour value and minute value for the end of day adjust


    // ====================================================================================================
    // private HashMap<String, Integer> getStringTimeComponents
    //
    // Returns an array containing the hour and minute integer values of a time string in format hh:mm
    //
    // Arguments--
    //
    // stringTime:      the formatted string to get the time components from
    //
    // isEndTime:       whether the stringTime is the end of a period, if true the time needs to be adjusted
    //
    // endOfDayAdjust:  whether the stringTime represents the end of day time, which gets cast to 1ms before midnight
    //
    // errorMessage:    the error message to print if an error occurs
    private HashMap<String, Integer> getStringTimeComponents(String stringTime, boolean isEndTime, boolean endOfDayAdjust, String errorMessage) throws Exception {
        // Check that the stringTime is only 5 characters (2 hours, colon, 2 min) and contains a colon
        CalendarHelper.calendarAssert((stringTime.contains(":")) && (stringTime.length() == 5), "SchoolDay.getStringTimeComponents called with invalid stringTime", stringTime);

        String[] timeSplit = stringTime.split(":"); // Split the stringTime by the colon to get the hour and minute value
        // Parse the hour and minute string values as integers
        int hours = Integer.parseInt(timeSplit[0]);
        int minutes = Integer.parseInt(timeSplit[1]);

        // Check that the hour and minute values are valid
        CalendarHelper.calendarAssert((hours >= 0) && (hours < SchoolCalendar.hoursPerDay) && (minutes >= 0) && (minutes < SchoolCalendar.minutesPerHour),
                "SchoolDay.getStringTimeComponents found incorrect stringTime formatting",
                stringTime, errorMessage);

        // If the stringTime is the end of a period time, return the hours, minutes, and 0 seconds/ms
        if (!isEndTime) {
            int finalHours = hours;
            int finalMinutes = minutes;
            return new HashMap<>() {{
                put(SchoolCalendar.getHoursTerm, finalHours);
                put(SchoolCalendar.getMinutesTerm, finalMinutes);
                put(SchoolCalendar.getSecondsTerm, 0);
                put(SchoolCalendar.getMillisecondsTerm, 0);
            }};
        }

        // If the time should be adjusted for the end of the day, return 1 ms before midnight
        if (endOfDayAdjust) {
            return new HashMap<>() {{
                put(SchoolCalendar.getHoursTerm, SchoolCalendar.hoursPerDay - 1);
                put(SchoolCalendar.getMinutesTerm, SchoolCalendar.minutesPerHour - 1);
                put(SchoolCalendar.getSecondsTerm, SchoolCalendar.secondsPerMinute - 1);
                put(SchoolCalendar.getMillisecondsTerm, SchoolCalendar.millisecondsPerSecond - 1);
            }};
        }

        // If the time is neither the end of day, or the end of a period
        // Move one minute back
        minutes -= 1;
        // Take into account the possibility of negative minutes and adjust hours
        if (minutes < 0) {
            hours -= 1;
            minutes = SchoolCalendar.minutesPerHour - 1;
        }
        // Return the 1 ms before the original time specified in stringTime
        int finalHours1 = hours;
        int finalMinutes1 = minutes;
        return new HashMap<>() {{
            put(SchoolCalendar.getHoursTerm, finalHours1);
            put(SchoolCalendar.getMinutesTerm, finalMinutes1);
            put(SchoolCalendar.getSecondsTerm, SchoolCalendar.secondsPerMinute - 1);
            put(SchoolCalendar.getMillisecondsTerm, SchoolCalendar.millisecondsPerSecond - 1);
        }};
    }
    // end: private HashMap<String, Integer> getStringTimeCompoenents


    // ----------------------------------------------------------------------------------------------------
    // public SchoolPeriod
    //
    // Constructor 1 for SchoolPeriod class
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
        // Call second overloaded SchoolPeriod constructor
        this(period, name, startTime, endTime, endOfDayAdjust, classInfo, "");
    }
    // end: public SchoolPeriod


    // ----------------------------------------------------------------------------------------------------
    // public SchoolPeriod
    //
    // Constructor 2 for SchoolPeriod class
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
        // Check that all the arguments are correct
        CalendarHelper.calendarAssert((period == SchoolCalendar.fakePeriod || period == SchoolCalendar.freePeriod || (period >= SchoolCalendar.getFirstPeriod() && period <= SchoolCalendar.getLastPeriod())) &&
                        (name != null) &&
                        (startTime != null) &&
                        (endTime != null),
                "SchoolPeriod.SchoolPeriod constructed with invalid arguments",
                String.valueOf(period), name, startTime, endTime, String.valueOf(endOfDayAdjust), String.valueOf(classInfo));

        // Set the class variables to the constructor variables
        this.period = period;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classInfo = (classInfo == null || classInfo.getClassName() == null) ? null : classInfo; // classInfo could be null in some cases, determine if it is and set correctly
        this.periodDescription = periodDescription;

        // Create an error message describing all the information from this class
        String errorMessage = period + " | " + name + " | " + startTime + " | " + endTime + " | " + periodDescription;

        // Call the time component helper method for the start and end time to get the hour, minute, second, and ms values for this period
        this.startDayAdjust = getStringTimeComponents(startTime, false, false, errorMessage);
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

    public HashMap<String, Integer> getStartDayAdjust() {
        return startDayAdjust;
    }

    public HashMap<String, Integer> getEndDayAdjust() {
        return endDayAdjust;
    }
    // end: GET methods


    // ====================================================================================================
    // public String toString
    //
    // SchoolPeriod toString method
    //
    // Arguments--
    //
    // None
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