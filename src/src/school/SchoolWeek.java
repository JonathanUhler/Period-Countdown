// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolWeek.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/22/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolWeek.java
// Class Diagram
/*

+-----------------------------------------------------------------------------------------------------+
|                                              SchoolWeek                                             |
+-----------------------------------------------------------------------------------------------------+
| -weekDateTag: String                                                                                |
| -weekArrayIndex: int                                                                                |
| -epochCalendar: Calendar                                                                            |
| -dayArray: ArrayList<SchoolDay>                                                                     |
+-----------------------------------------------------------------------------------------------------+
| +SchoolWeek(String, int)                                                                            |
+-----------------------------------------------------------------------------------------------------+
| +setDayArray(ArrayList<SchoolDay>): void                                                            |
| +getWeekDateTag(): String                                                                           |
| +getWeekArrayIndex(): int                                                                           |
| +getEpochCalendar(): Calendar                                                                       |
| +getDayArray(): ArrayList<SchoolDay>                                                                |
| +toString(): String                                                                                 |
+-----------------------------------------------------------------------------------------------------+

*/
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import java.util.ArrayList;
import java.util.Calendar;
import calendar.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolWeek
//
// Defines the structure for each week of the year
//
public class SchoolWeek {

    private final String weekDateTag; // The string tag representing the SUNDAY of this week in format yyyy-mm-dd
    private final int weekArrayIndex; // The index of the week in the week array

    private final Calendar epochCalendar; // A calendar object representing this week
    private ArrayList<SchoolDay> dayArray; // The array of day objects in this week


    // ====================================================================================================
    // public void setDayArray
    //
    // Update/set the dayArray
    //
    // Arguments--
    //
    // dayArray:    the new array of day objects to set to this.dayArray
    //
    // Returns--
    //
    // None
    //
    public void setDayArray(ArrayList<SchoolDay> dayArray) throws Exception {
        // Check that the arguments are valid
        CalendarHelper.calendarAssert((dayArray != null) &&
                (dayArray.size() == SchoolCalendar.daysPerWeek),
                "SchoolWeek.setDayArray called with invalid arguments",
                String.valueOf(dayArray));

        // Set the day array
        this.dayArray = dayArray;
    }
    // end: public void setDayArray


    // ----------------------------------------------------------------------------------------------------
    // public SchoolWeek
    //
    // Constructor for SchoolWeek class
    //
    // Arguments--
    //
    // weekDateTag:     the string tag that represents the SUNDAY of the week in format yyyy-mm-dd
    //
    // weekArrayIndex:  the index of this week in the week array
    //
    public SchoolWeek(String weekDateTag, int weekArrayIndex) throws Exception {
        // Check that all arguments are valid
        CalendarHelper.calendarAssert((weekDateTag != null),
                "SchoolWeek.SchoolWeek constructed with invalid arguments",
                weekDateTag, String.valueOf(weekArrayIndex));

        // Set class variables with the constructor variables
        this.weekDateTag = weekDateTag;
        this.weekArrayIndex = weekArrayIndex;

        this.epochCalendar = Calendar.getInstance(); // Create a new Calendar instance for the current time/date of the computer
        this.epochCalendar.setTime(CalendarHelper.midnightOfDate(weekDateTag)); // Immediately correct the epochCalendar date/time to reference weekDateTag

        // Initialize the day array
        this.dayArray = new ArrayList<>();
    }
    // end: public SchoolWeek


    // ====================================================================================================
    // GET methods
    public String getWeekDateTag() {
        return weekDateTag;
    }

    public int getWeekArrayIndex() {
        return weekArrayIndex;
    }

    public Calendar getEpochCalendar() {
        return epochCalendar;
    }

    public ArrayList<SchoolDay> getDayArray() {
        return dayArray;
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
        String classDescription = "\n\tweekArrayIndex:\t" + this.getWeekArrayIndex() + "\n\tweekTag:\t" +
                this.getWeekDateTag() + "\n\tdayArray:\n\t\t" + this.getDayArray();
        return super.toString() + ": " + classDescription;
    }
    // end: public String toString

}
// end: public class SchoolWeek