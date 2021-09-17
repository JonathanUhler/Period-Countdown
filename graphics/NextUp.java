// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// NextUp.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/10/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class NextUp {

    public static final int NONE = 0;
    public static final int ONE_NAME = 1;
    public static final int ONE_ALL_INFO = 2;
    public static final int ALL_NAME = 3;
    public static final int ALL_ALL_INFO = 4;


    private String getNextMatchData(Calendar epochCalendar) throws Exception {
        return this.getNextMatchData(epochCalendar, false);
    }


    private String getNextMatchData(Calendar epochCalendar, boolean nameOnly) throws Exception {
        return this.getNextMatchData(epochCalendar, nameOnly, false).get(0);
    }


    private ArrayList<String> getNextMatchData(Calendar epochCalendar, boolean nameOnly, boolean allMatches) throws Exception {
        SchoolCalendar schoolCalendar = new SchoolCalendar(SchoolDisplay.schoolData, SchoolDisplay.userData);
        ArrayList<String> matchStrings = new ArrayList<>();

        HashMap<String, Object> currentDayData = schoolCalendar.getNextPeriod(schoolCalendar.getPeriodByDateAndTime(epochCalendar));
        SchoolDay currentDay = (SchoolDay) currentDayData.get(SchoolCalendar.getDaysTerm);

        for (int i = 0; i < currentDay.getPeriodList().size(); i++) {
            if (((SchoolPeriod) schoolCalendar.getPeriodByDateAndTime(epochCalendar).get(SchoolCalendar.getPeriodTerm)).getEndTime().equals("23:59")) { break; }

            currentDayData = schoolCalendar.getNextPeriod(schoolCalendar.getPeriodByDateAndTime(epochCalendar));
            SchoolPeriod currentPeriod = (SchoolPeriod) currentDayData.get(SchoolCalendar.getPeriodTerm);

            if (currentPeriod.getPeriod() != -1) {
                SchoolClass matchClass = currentPeriod.getClassInfo();

                if ((nameOnly && matchClass == null) || (!nameOnly && matchClass == null)) matchStrings.add(currentPeriod.getName() + " | " + currentPeriod.getStartTime() + "-" + currentPeriod.getEndTime()); // If only the name is wanted and there is no class info OR all info is wanted and there is no class info
                else if (nameOnly) matchStrings.add(matchClass.getClassName() + " | " + currentPeriod.getStartTime() + "-" + currentPeriod.getEndTime()); // If only the name is wanted and there is class info
                else matchStrings.add(matchClass.getClassName() + " | " + currentPeriod.getStartTime() + "-" + currentPeriod.getEndTime() + " | " + matchClass.getTeacherName() + ", " + matchClass.getRoomNumber()); // If all info is wanted and there is class info

                if (!allMatches) break;
            }

            if (currentPeriod.getEndTime().equals("23:59")) { break; }

            epochCalendar = CalendarHelper.createEpochTime(epochCalendar.get(Calendar.YEAR) + "-" +
                    CalendarHelper.padStringLeft(String.valueOf(epochCalendar.get(Calendar.MONTH) + 1), 2, '0') + "-" +
                    CalendarHelper.padStringLeft(String.valueOf(epochCalendar.get(Calendar.DATE)), 2, '0') + "T" +
                    currentPeriod.getStartTime().split(":")[0] + ":" +
                    currentPeriod.getStartTime().split(":")[1] + ":00");
        }

        if (matchStrings.size() == 0) matchStrings.add("None");
        return matchStrings;
    }


    private String oneName(Calendar epochCalendar) throws Exception {
        return "<html><br><b>Upcoming Periods</b><br>" + this.getNextMatchData(epochCalendar, true) + "</html>";
    }


    private String oneAllInfo(Calendar epochCalendar) throws Exception {
        return "<html><br><b>Upcoming Periods</b><br>" + this.getNextMatchData(epochCalendar) + "</html>";
    }


    private String allName(Calendar epochCalendar) throws Exception {
        ArrayList<String> periodInformation = this.getNextMatchData(epochCalendar, true, true);
        StringBuilder periodInfoString = new StringBuilder();

        for (String periodInfo : periodInformation) {
            periodInfoString.append(periodInfo).append("<br>");
        }

        return "<html><br><b>Upcoming Periods</b><br>" + periodInfoString + "</html>";
    }


    private String allAllInfo(Calendar epochCalendar) throws Exception {
        ArrayList<String> periodInformation = this.getNextMatchData(epochCalendar, false, true);
        StringBuilder periodInfoString = new StringBuilder();

        for (String periodInfo : periodInformation) {
            periodInfoString.append(periodInfo).append("<br>");
        }

        return "<html><br><b>Upcoming Periods</b><br>" + periodInfoString + "</html>";
    }


    public String getNextUpPanel(int verbosity, Calendar epochCalendar) throws Exception {
        if (verbosity == NONE) { return ""; }

        return switch (verbosity) {
            case ONE_NAME -> oneName(epochCalendar);
            case ONE_ALL_INFO -> oneAllInfo(epochCalendar);
            case ALL_NAME -> allName(epochCalendar);
            case ALL_ALL_INFO -> allAllInfo(epochCalendar);
            default -> null;
        };
    }

}
