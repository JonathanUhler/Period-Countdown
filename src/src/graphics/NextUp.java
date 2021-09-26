// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// NextUp.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/10/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// NextUp.java
// Class Diagram
/*

+-----------------------------------------------------------------------------------------------------+
|                                                NextUp                                               |
+-----------------------------------------------------------------------------------------------------+
| +NONE: int
| +ONE_NAME: int
| +ONE_ALL_INFO: int
| +ALL_NAME: int
| +ALL_ALL_INFO: int
+-----------------------------------------------------------------------------------------------------+
|
+-----------------------------------------------------------------------------------------------------+
|
+-----------------------------------------------------------------------------------------------------+

*/
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package graphics;


import java.util.ArrayList;
import java.util.Calendar;
import school.*;
import calendar.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class NextUp
//
// Handler for the Next Up... classes feature
//
public class NextUp {

    // Constants for the verbosity of the next up display
    public static final int NONE = 0;
    public static final int ONE_NAME = 1;
    public static final int ONE_ALL_INFO = 2;
    public static final int ALL_NAME = 3;
    public static final int ALL_ALL_INFO = 4;


    // ====================================================================================================
    // private String getNextMatchData
    //
    // Overloaded method 1 for getNextMatchData
    //
    // Arguments--
    //
    // epochCalendar:   time to get the next up data for
    //
    // Returns--
    //
    // Call to overloaded method 2 for getNextMatchData
    //
    private String getNextMatchData(Calendar epochCalendar) throws Exception {
        return this.getNextMatchData(epochCalendar, false);
    }
    // end: private String getNextMatchData


    // ====================================================================================================
    // private String getNextMatchData
    //
    // Overloaded method 2 for getNextMatchData
    //
    // Arguments--
    //
    // epochCalendar:   time to get the next up data for
    //
    // Returns--
    //
    // Call to overloaded method 3 for getNextMatchData
    //
    private String getNextMatchData(Calendar epochCalendar, boolean nameOnly) throws Exception {
        return this.getNextMatchData(epochCalendar, nameOnly, false).get(0);
    }
    // end: private String getNextMatchData


    // ====================================================================================================
    // private String getNextMatchData
    //
    // Get a list of next of matches
    //
    // Arguments--
    //
    // epochCalendar:   time to get the next up data for
    //
    // Returns--
    //
    // List of next class matches as strings
    //
    private ArrayList<String> getNextMatchData(Calendar epochCalendar, boolean nameOnly, boolean allMatches) throws Exception {
        SchoolCalendar schoolCalendar = new SchoolCalendar(SchoolDisplay.getSchoolData(), SchoolDisplay.userData); // Create a local instance of the SchoolCalendar class to get access to its methods
        ArrayList<String> matchStrings = new ArrayList<>(); // Initialize an arraylist to hold the next up strings when/if they are found

        PeriodData currentDayData = schoolCalendar.getNextPeriod(schoolCalendar.getPeriodByDateAndTime(epochCalendar)); // Get the data for the current day match
        SchoolDay currentDay = currentDayData.getDay(); // Get the current day object from the current day match data

        // Loop through each of the periods in the current day match
        for (int i = 0; i < currentDay.getPeriodList().size(); i++) {
            // If the period is the last of the day, exit the loop
            if (schoolCalendar.getPeriodByDateAndTime(epochCalendar).getPeriod().getEndTime().equals("23:59")) { break; }

            // Update the current day and period data
            currentDayData = schoolCalendar.getNextPeriod(schoolCalendar.getPeriodByDateAndTime(epochCalendar));
            SchoolPeriod currentPeriod = currentDayData.getPeriod();

            // If the period is real or free (anything but -1/fake)
            if (currentPeriod.getPeriod() != -1) {
                SchoolClass matchClass = currentPeriod.getClassInfo(); // Get the class object for the period

                // Assemble data
                if ((nameOnly && matchClass == null) || (!nameOnly && matchClass == null)) matchStrings.add(currentPeriod.getName() + " | " + currentPeriod.getStartTime() + "-" + currentPeriod.getEndTime()); // If only the name is wanted and there is no class info OR all info is wanted and there is no class info
                else if (nameOnly) matchStrings.add(matchClass.getClassName() + " | " + currentPeriod.getStartTime() + "-" + currentPeriod.getEndTime()); // If only the name is wanted and there is class info
                else matchStrings.add(matchClass.getClassName() + " | " + currentPeriod.getStartTime() + "-" + currentPeriod.getEndTime() + " | " + matchClass.getTeacherName() + ", " + matchClass.getRoomNumber()); // If all info is wanted and there is class info

                // If the caller only wants the next 1 match, exit the loop after 1 match has been found.
                if (!allMatches) break;
            }

            // If the end time of the current period being looked at is the end of the day, exit the loop
            if (currentPeriod.getEndTime().equals("23:59")) { break; }

            // Update the epoch calendar
            epochCalendar = CalendarHelper.createEpochTime(epochCalendar.get(Calendar.YEAR) + "-" +
                    CalendarHelper.padStringLeft(String.valueOf(epochCalendar.get(Calendar.MONTH) + 1), 2, '0') + "-" +
                    CalendarHelper.padStringLeft(String.valueOf(epochCalendar.get(Calendar.DATE)), 2, '0') + "T" +
                    currentPeriod.getStartTime().split(":")[0] + ":" +
                    currentPeriod.getStartTime().split(":")[1] + ":00");
        }

        if (matchStrings.size() == 0) matchStrings.add("None"); // If there have been no matches found, add the term "None"
        return matchStrings; // Return the list of next up matches
    }
    // end: private ArrayList<String> getNextMatchData


    // ====================================================================================================
    private String oneName(Calendar epochCalendar) throws Exception {
        return "<html><br><b>Upcoming Periods</b><br>" + this.getNextMatchData(epochCalendar, true) + "</html>";
    }


    // ====================================================================================================
    private String oneAllInfo(Calendar epochCalendar) throws Exception {
        return "<html><br><b>Upcoming Periods</b><br>" + this.getNextMatchData(epochCalendar) + "</html>";
    }


    // ====================================================================================================
    private String allName(Calendar epochCalendar) throws Exception {
        ArrayList<String> periodInformation = this.getNextMatchData(epochCalendar, true, true);
        StringBuilder periodInfoString = new StringBuilder();

        for (String periodInfo : periodInformation) {
            periodInfoString.append(periodInfo).append("<br>");
        }

        return "<html><br><b>Upcoming Periods</b><br>" + periodInfoString + "</html>";
    }


    // ====================================================================================================
    private String allAllInfo(Calendar epochCalendar) throws Exception {
        ArrayList<String> periodInformation = this.getNextMatchData(epochCalendar, false, true);
        StringBuilder periodInfoString = new StringBuilder();

        for (String periodInfo : periodInformation) {
            periodInfoString.append(periodInfo).append("<br>");
        }

        return "<html><br><b>Upcoming Periods</b><br>" + periodInfoString + "</html>";
    }


    // ====================================================================================================
    public String getNextUpPanel(int verbosity, Calendar epochCalendar) throws Exception {
        if (verbosity == NONE) { return ""; }

        String output;

        switch (verbosity) {
            case ONE_NAME:
                output = oneName(epochCalendar);
                break;
            case ONE_ALL_INFO:
                output = oneAllInfo(epochCalendar);
                break;
            case ALL_NAME:
                output = allName(epochCalendar);
                break;
            case ALL_ALL_INFO:
                output = allAllInfo(epochCalendar);
                break;
            default:
                output = null;
                break;
        }

        return output;
    }

}
// end: public class NextUp