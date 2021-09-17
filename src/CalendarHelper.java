// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// CalendarHelper.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/22/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import javax.swing.*;
import java.awt.*;
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
    // Returns--
    //
    // None
    //
    public static void calendarAssert(boolean assertion, String failureMessage, String... extraArgs) throws Exception {
        if (!assertion) { // Check if the assertion is not true
            // Create and print the error message
            String err = "ERROR: calendarAssert Assertion Failed: " + failureMessage + ((extraArgs.length > 0) ? ": " + String.join("\n", extraArgs) : "");
            System.out.println(err);
            // Throw a new error to stop the operation
            throw new Exception("Assertion Failed");
        }
    }
    // end: public static void calendarAssert


    // ====================================================================================================
    // public static void calendarMessage
    //
    // A substitute for System.out.println() to manage console output from a central location
    //
    // Arguments--
    //
    // message: the primary message to print
    //
    // ...args: an optional list of variable arguments to print along with the message
    //
    // Returns--
    //
    // None
    //
    public static void calendarMessage(String message, String... args) {
        String msg = "Calendar Message: " + message + ((args.length > 0) ? String.join(",\n", args) : "");
        System.out.println(msg);
    }
    // end: public static void calendarMessage


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
        // Check that the stringDate is not null
        calendarAssert(stringDate != null, "CalendarHelper.midnightOfDate called with invalid arguments", stringDate);

        String stringDateAndTime = stringDate + "T00:00:00"; // Append the 0 time to set the time to midnight
        return new SimpleDateFormat(SchoolCalendar.dateTimeFormat).parse(stringDateAndTime); // Return the datetime as a Date() object
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
    public static String createStringTime(int hours, int minutes, int seconds, int days, boolean convertDaysToHours) throws Exception {
        calendarAssert((hours >= 0 && hours <= 23) && (minutes >= 0 && minutes <= 59) && (seconds >= 0 && seconds <= 59),
                "CalendarHelper.createStringTime called with invalid argumnts",
                String.valueOf(hours), String.valueOf(minutes), String.valueOf(seconds));

        StringBuilder stringTime = new StringBuilder();

        int dayCount = (days > 0 && !convertDaysToHours) ? days : 0;
        if (dayCount > 0) {
            stringTime.append(dayCount);
        }

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
    // stringTime:  a string representing the time to create in format yyyy-mm-ddThh:mm:ss (with literal 'T' char)
    //
    // Returns--
    //
    // epochTime:   the string time converted to a Calendar object
    //
    public static Calendar createEpochTime(String stringTime) throws Exception {
        // Make sure the stringTime given matches the expected regex
        calendarAssert(stringTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}"),
                "CalendarHelper.createEpochTime called with invalid arguments",
                stringTime);

        String[] dateTimeSplit = stringTime.split("T"); // Split the time into the date and time separately
        String[] dateSplit = dateTimeSplit[0].split("-"); // Split the date into its components
        String[] timeSplit = dateTimeSplit[1].split(":"); // Split the time into its components

        Calendar epochTime = Calendar.getInstance(); // Create a new Calendar instance

        // Set all the information for the calendar date/time
        epochTime.set(Calendar.YEAR, Integer.parseInt(dateSplit[0]));
        epochTime.set(Calendar.MONTH, Integer.parseInt(dateSplit[1]) - 1);
        epochTime.set(Calendar.DATE, Integer.parseInt(dateSplit[2]));
        epochTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSplit[0]));
        epochTime.set(Calendar.MINUTE, Integer.parseInt(timeSplit[1]));
        epochTime.set(Calendar.SECOND, Integer.parseInt(timeSplit[2]));

        return epochTime; // Return the new Calendar object
    }
    // end: public static Calendar createEpochTime


    public static ImageIcon scaleImage(ImageIcon icon, int w, int h) {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();

        if (icon.getIconWidth() > w) {
            nw = w;
            nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }

        if (nh > h) {
            nh = h;
            nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }

        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_DEFAULT));
    }

}
// end: public class CalendarHelper