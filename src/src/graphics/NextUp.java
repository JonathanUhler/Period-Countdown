// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// NextUp.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/10/21
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
    private ArrayList<String> getNextMatchData(Calendar epochCalendar, boolean nameOnly, boolean allMatches)
            throws Exception {
        String schoolDataFile = SchoolDisplay.getSchoolData();
        SchoolCalendar schoolCalendar = new SchoolCalendar(schoolDataFile, SchoolDisplay.userData);
        ArrayList<String> matchStrings = new ArrayList<>();

        // If there is no match for this time period, then it is probably summer. In order to avoid
        // an error by passing null into methods later on, this routine can be ended here
        PeriodData lastMatch = schoolCalendar.getPeriodByDateAndTime(epochCalendar);
        if (lastMatch == null) {
            matchStrings.add("None");
            return matchStrings;
        }

        PeriodData currentDayData = schoolCalendar.getNextPeriod(lastMatch);
        SchoolDay currentDay = currentDayData.getDay();

        // Loop through each of the periods in the matching day. If the period found is either the last
        // period of the day or the first period found when only one is requested, then the loop can
        // exit. Otherwise, keep looping and adding the period data to the matchStrings list
        for (int i = 0; i < currentDay.getPeriodList().size(); i++) {
            if (lastMatch.getPeriod().getEndTime().equals("23:59"))
                break;

            currentDayData = schoolCalendar.getNextPeriod(lastMatch);
            SchoolPeriod currentPeriod = currentDayData.getPeriod();

            if (currentPeriod.getPeriod() != -1) {
                SchoolClass matchClass = currentPeriod.getClassInfo();

                // Possibility 1: The period is a free period (no matter how much info is requested). In this
                //                case, use the default name of the period (ex: "Period 5")
                if ((nameOnly && matchClass == null) || (!nameOnly && matchClass == null))
                    matchStrings.add(currentPeriod.getName() + " | " +
                            currentPeriod.getStartTime() + "-" +
                            currentPeriod.getEndTime());
                // Possibility 2: Only the name of the period is requested, and it is not a free period. Use
                //                the user-defined name for the period (ex: "Chemistry")
                else if (nameOnly)
                    matchStrings.add(matchClass.getClassName() + " | " +
                            currentPeriod.getStartTime() + "-" +
                            currentPeriod.getEndTime());
                // Possibility 3: All the information about the period is requested. Return the entire
                //                string of information including the teacher name and room number
                else
                    matchStrings.add(matchClass.getClassName() + " | " +
                            currentPeriod.getStartTime() + "-" +
                            currentPeriod.getEndTime() + " | " +
                            matchClass.getTeacherName() + ", " +
                            matchClass.getRoomNumber());

                if (!allMatches)
                    break;
            }

            if (currentPeriod.getEndTime().equals("23:59"))
                break;

            // Each time through the loop update the time to check for during the next iteration through
            // the loop so that a list of periods can be found instead of just returning the same period
            // over and over
            epochCalendar = CalendarHelper.createEpochTime(epochCalendar.get(Calendar.YEAR) + "-" +
                    CalendarHelper.padStringLeft(String.valueOf(epochCalendar.get(Calendar.MONTH)+1), 2, '0')
                    + "-" +
                    CalendarHelper.padStringLeft(String.valueOf(epochCalendar.get(Calendar.DATE)), 2, '0')
                    + "T" +
                    currentPeriod.getStartTime().split(":")[0] + ":" +
                    currentPeriod.getStartTime().split(":")[1] + ":00");
            lastMatch = schoolCalendar.getPeriodByDateAndTime(epochCalendar);
        }

        if (matchStrings.size() == 0)
            matchStrings.add("None");
        return matchStrings;
    }
    // end: private ArrayList<String> getNextMatchData


    // ====================================================================================================
    // private String one
    //
    // Get the next up string for the next class only
    //
    // Arguments--
    //
    // epochCalendar:   the calendar object representing the time to get the next up data for
    //
    // nameOnly:        whether to include only the class names or the class names + room/teacher info
    //
    // Returns--
    //
    // The next up string
    //
    private String one(Calendar epochCalendar, boolean nameOnly) throws Exception {
        return "<html><br><b>Upcoming Periods</b><br>" +
                this.getNextMatchData(epochCalendar, nameOnly) +
                "</html>";
    }
    // end: private String one


    // ====================================================================================================
    // private String all
    //
    // Get the next up string for all next classes in the day
    //
    // Arguments--
    //
    // epochCalendar:   the calendar object representing the time to get the next up data for
    //
    // nameOnly:        whether to include only the class names or the class names + room/teacher info
    //
    // Returns--
    //
    // The next up string
    //
    private String all(Calendar epochCalendar, boolean nameOnly) throws Exception {
        // Get the matching period information for the rest of the day and add the requested information
        // to a string that is returned and displayed with the proper formatting using html tags
        ArrayList<String> periodInformation = this.getNextMatchData(epochCalendar, nameOnly, true);
        StringBuilder periodInfoString = new StringBuilder();

        for (String periodInfo : periodInformation)
            periodInfoString.append(periodInfo).append("<br>");

        return "<html><br><b>Upcoming Periods</b><br>" + periodInfoString + "</html>";
    }
    // end: private String all


    // ====================================================================================================
    // public String getNextUpPanel
    //
    // Gets the next up string of classes
    //
    // Arguments--
    //
    // verbosity:       how verbose the list of classes should be
    //
    // epochCalendar:   the calendar object representing the time to get the data for
    //
    // Returns--
    //
    // The next up string
    //
    public String getNextUpPanel(int verbosity, Calendar epochCalendar) throws Exception {
        if (verbosity == NONE)
            return "";

        return switch (verbosity) {
            case ONE_NAME -> one(epochCalendar, true);
            case ONE_ALL_INFO -> one(epochCalendar, false);
            case ALL_NAME -> all(epochCalendar, true);
            case ALL_ALL_INFO -> all(epochCalendar, false);
            default -> null;
        };
    }
    // end: public String getNextUpPanel

}
// end: public class NextUp