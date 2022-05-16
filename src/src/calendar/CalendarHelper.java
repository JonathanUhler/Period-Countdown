// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// CalendarHelper.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/22/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package calendar;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class CalendarHelper
//
// A class containing helper methods for the Calendar structure
//
public class CalendarHelper {

    // ====================================================================================================
    // public static void calendarAssert
    //
    // Checks an assertion and throws an appropriate error
    //
    // Arguments--
    //
    // assertion:       the boolean statement to check the validity of
    //
    // failureMessage:  a message describing the failure, if one occurs
    //
    // ...extraArgs:    a variable number of extra arguments to print to help with error debug
    //
    public static void calendarAssert(boolean assertion, String failureMessage, String... extraArgs)
            throws Exception {
        if (!assertion) { // Check if the assertion is not true
            // Create and print the error message
            String err = "ERROR: calendarAssert Assertion Failed: " + failureMessage +
                    ((extraArgs.length > 0) ? ": " + String.join("\n", extraArgs) : "");
            System.out.println(err);
            // Throw a new error to stop the operation
            throw new Exception("Assertion Failed");
        }
    }
    // end: public static void calendarAssert


    // ====================================================================================================
    // public static Date midnightOfDate
    //
    // Returns a datetime string representing midnight of a given date
    //
    // Arguments--
    //
    // stringDate:  the date in format yyyy-mm-dd
    //
    // Returns--
    //
    // The formatted datetime string with the midnight time appended
    //
    public static Date midnightOfDate(String stringDate) throws Exception {
        calendarAssert(stringDate != null,
                "CalendarHelper.midnightOfDate called with invalid arguments",
                stringDate);

        String stringDateAndTime = stringDate + "T00:00:00";
        return new SimpleDateFormat(SchoolCalendar.dateTimeFormat).parse(stringDateAndTime);
    }
    // end: public static Date midnightOfDate


    // ====================================================================================================
    // public static String padStringLeft
    //
    // Pads a given number of characters to the left of a string
    //
    // Arguments--
    //
    // str:         the string to pad to
    //
    // width:       the total width of the resulting string
    //
    // character:   the type of character to pad
    //
    // Returns--
    //
    // The padded string
    //
    public static String padStringLeft(String str, int width, char character) {
        return String.format("%" + width + "s", str).replace(' ', character);
    }
    // end: public static String padStringLeft


    // ====================================================================================================
    // public static String padStringRight
    //
    // Pads a given number of characters to the right of a string
    //
    // Arguments--
    //
    // str:         the string to pad to
    //
    // width:       the total width of the resulting string
    //
    // character:   the type of character to pad
    //
    // Returns--
    //
    // The padded string
    //
    public static String padStringRight(String str, int width, char character) {
        return String.format("%-" + width + "s", str).replace(' ', character);
    }
    // end: public static String padStringRight


    // ====================================================================================================
    // public static String createStringTime
    //
    // Creates a stringTime/sTime formatted string given the components
    //
    // Arguments--
    //
    // hours:   the number of hours
    //
    // minutes: the number of minutes
    //
    // seconds: the number of seconds
    //
    // Returns--
    //
    // The formatted stringTime
    //
    public static String createStringTime(int hours, int minutes, int seconds) throws Exception {
        return createStringTime(hours, minutes, seconds, 0, false);
    }
    // end: public static String createStringTime


    // ====================================================================================================
    // public static String createStringTime
    //
    // Creates a stringTime/sTime formatted string given the components
    //
    // Arguments--
    //
    // hours:               the number of hours
    //
    // minutes:             the number of minutes
    //
    // seconds:             the number of seconds
    //
    // days:                the number of days
    //
    // convertDaysToHours:  whether to convert the number of days to hours
    //
    // Returns--
    //
    // The formatted stringTime
    //
    public static String createStringTime(int hours, int minutes, int seconds,
                                          int days, boolean convertDaysToHours) throws Exception {
        calendarAssert((hours >= 0 && hours <= 23) &&
                        (minutes >= 0 && minutes <= 59) &&
                        (seconds >= 0 && seconds <= 59),
                "CalendarHelper.createStringTime called with invalid arguments",
                String.valueOf(hours), String.valueOf(minutes), String.valueOf(seconds));

        StringBuilder stringTime = new StringBuilder();

        int dayCount = (days > 0 && !convertDaysToHours) ? days : 0;
        if (dayCount > 0)
            stringTime.append(dayCount);

        stringTime.append(hours + ((convertDaysToHours) ? (days * 24) : 0))
                .append(padStringLeft(String.valueOf(minutes), 2, '0'))
                .append(padStringLeft(String.valueOf(seconds), 2, '0'));

        return stringTime.toString();
    }
    // end: public static String createStringTime


    // ====================================================================================================
    // public static Calendar createEpochTime
    //
    // Create a Calendar object from a string representing the time
    //
    // Arguments--
    //
    // stringTime:  a string representing the time to create in format yyyy-mm-ddThh:mm:ss
    //              (with literal 'T' char)
    //
    // Returns--
    //
    // epochTime:   the string time converted to a Calendar object
    //
    public static Calendar createEpochTime(String stringTime) throws Exception {
        calendarAssert(stringTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"),
                "CalendarHelper.createEpochTime called with invalid arguments",
                stringTime);

        String[] dateTimeSplit = stringTime.split("T");
        String[] dateSplit = dateTimeSplit[0].split("-");
        String[] timeSplit = dateTimeSplit[1].split(":");

        Calendar epochTime = Calendar.getInstance();
        epochTime.set(Calendar.YEAR, Integer.parseInt(dateSplit[0]));
        epochTime.set(Calendar.MONTH, Integer.parseInt(dateSplit[1]) - 1);
        epochTime.set(Calendar.DATE, Integer.parseInt(dateSplit[2]));
        epochTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSplit[0]));
        epochTime.set(Calendar.MINUTE, Integer.parseInt(timeSplit[1]));
        epochTime.set(Calendar.SECOND, Integer.parseInt(timeSplit[2]));
        return epochTime;
    }
    // end: public static Calendar createEpochTime


    // ====================================================================================================
    // public static String createWeekTag
    //
    // Create the week tag (string date in format yyyy-mm-dd on the sunday of the week) based on the week
    // containing the date in dayTag
    //
    // Arguments--
    //
    // dayTag:  the day within the week to create the week tag for
    //
    // Returns--
    //
    // The week tag of the week that contains dayTag
    //
    public static String createWeekTag(String dayTag) {
        String[] dayTagSplit = dayTag.split("-");
        Calendar epochDate = Calendar.getInstance();
        epochDate.set(Calendar.YEAR, Integer.parseInt(dayTagSplit[0]));
        epochDate.set(Calendar.MONTH, Integer.parseInt(dayTagSplit[1]));
        epochDate.set(Calendar.DATE, Integer.parseInt(dayTagSplit[2]));

        Calendar newDate = Calendar.getInstance();
        newDate.setTimeInMillis(epochDate.getTimeInMillis());
        newDate.set(Calendar.DATE, epochDate.get(Calendar.DATE) - (epochDate.get(Calendar.DAY_OF_WEEK) - 1));

        return newDate.get(Calendar.YEAR) + "-" +
                newDate.get(Calendar.MONTH) + "-" +
                newDate.get(Calendar.DATE);
    }
    // end: public static String createWeekTag

}
// end: public class CalendarHelper