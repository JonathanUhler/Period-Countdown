// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolDay.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/22/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import java.util.ArrayList;
import java.util.Calendar;
import calendar.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolDay
//
// Defines the structure for each day of the week
//
public class SchoolDay {

    private final String dayDateTag; // A string representing the day in format yyyy-mm-dd
    private final int dayArrayIndex; // The index of the day, in an array of all school days in the year (ex:
                                     // index 38 = 39th day in the school year)
    private final String weekDateTag; // A string representing the week in format yyyy-mm-dd. This string
                                      // references is the SUNDAY of the week in question
    private final String dayType; // The type of day (ex: "School Day", "Holiday", etc.)
    private final ArrayList<SchoolPeriod> periodList; // An arraylist of each period object in the day

    private final Calendar epochCalendar; // A calendar object defining the date in dayDateTag
    private final String stringDate; // The string representation of the date in the U.S.A (ex: mm/dd/yyyy)
    private final String stringDay; // The string representation of the day -> the name of the day of the week
                                    // (ex: "Tuesday")


    // ----------------------------------------------------------------------------------------------------
    // public SchoolDay
    //
    // Arguments--
    //
    // dayDateTag:      string representing the day in format yyyy-mm-dd
    //
    // dayArrayIndex:   index of the day, in an array of all school days in the year
    //
    // weekDateTag:     string representing the week in format yyyy-mm-dd. This string references is the
    //                  SUNDAY of the week in question
    //
    // dayType:         type of the day (ex: "School Day", "Holiday", etc.)
    //
    // periodList:      arraylist of each period object in the day
    //
    public SchoolDay(String dayDateTag, int dayArrayIndex, String weekDateTag,
                     String dayType, ArrayList<SchoolPeriod> periodList) throws Exception {
        CalendarHelper.calendarAssert((dayDateTag != null) &&
                (dayArrayIndex >= 0) &&
                (weekDateTag != null) &&
                (dayType != null) &&
                (periodList != null),
                "SchoolDay.SchoolDay constructed with invalid arguments",
                dayDateTag, Integer.toString(dayArrayIndex), weekDateTag,
                dayType, String.valueOf(periodList));

        this.dayDateTag = dayDateTag;
        this.dayArrayIndex = dayArrayIndex;
        this.weekDateTag = weekDateTag;
        this.dayType = dayType;
        this.periodList = periodList;

        this.epochCalendar = Calendar.getInstance();
        this.epochCalendar.setTime(CalendarHelper.midnightOfDate(dayDateTag));

        int month = this.epochCalendar.get(Calendar.MONTH) + 1;
        int date = this.epochCalendar.get(Calendar.DAY_OF_MONTH);
        int year = this.epochCalendar.get(Calendar.YEAR);
        this.stringDate = month + "/" + date + "/" + year;

        int dayIndex = this.epochCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        this.stringDay = SchoolCalendar.dayIndexToName[dayIndex];
    }
    // end: public SchoolDay


    // ====================================================================================================
    // GET methods
    public String getDayDateTag() {
        return dayDateTag;
    }

    public int getDayArrayIndex() {
        return dayArrayIndex;
    }

    public String getWeekDateTag() {
        return weekDateTag;
    }

    public String getDayType() {
        return dayType;
    }

    public ArrayList<SchoolPeriod> getPeriodList() {
        return periodList;
    }

    public Calendar getEpochCalendar() {
        return epochCalendar;
    }

    public String getStringDate() {
        return stringDate;
    }

    public String getStringDay() {
        return stringDay;
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
        String classDescription = "\n\tdayArrayIndex + 1:\t" + (this.getDayArrayIndex() + 1) +
                "\n\tstringDay:\t" + this.getStringDay() + "\n\tstringDate:\t" + this.getStringDate() +
                "\n\tdayType:\t" + this.getDayType() + "\n\tweekTag:\t" + this.getWeekDateTag() +
                "\n\tdayTag:\t" + this.getDayDateTag() + "\n\tperiods:\t" +
                ((this.getPeriodList().size() == 0) ? "no periods" : "periods\n\t\t" + this.getPeriodList());
        return super.toString() + ": " + classDescription;
    }
    // end: public String toString
}
// end: public class SchoolDay