// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolDisplay.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/2/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package graphics;


import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import com.google.gson.Gson;
import school.*;
import calendar.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolDisplay
//
// Front-end methods for quickly getting and displaying time data from SchoolCalendar
//
public class SchoolDisplay {

    private final SchoolCalendar schoolCalendar; // Class instance of the school year calendar data structure and class

    public static final String defaultSchoolData = "MVHS_School.json"; // Default json data file

    public static final String periodCountdownDirectory = System.getProperty("user.home") + SchoolCalendar.PC_PATH  + "periodcountdown"; // Path to the hidden appdata folder for Period-Countdown

    private static String schoolData = System.getProperty("user.home") + SchoolCalendar.PC_PATH + "periodcountdown" + SchoolCalendar.FILE_SEP + "json" + SchoolCalendar.FILE_SEP + defaultSchoolData; // Mandatory location for the school json data
    public static final String userData = System.getProperty("user.home") + SchoolCalendar.PC_PATH + "periodcountdown" + SchoolCalendar.FILE_SEP + "json" + SchoolCalendar.FILE_SEP + "User.json"; // Mandatory location for the user specific json data

    private String localSchoolData; // Local (within the app files) path to the json data
    {try {localSchoolData = new File(new File(new File(SchoolDisplay.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()).getAbsolutePath()).getParent() + SchoolCalendar.FILE_SEP + "json" + SchoolCalendar.FILE_SEP + defaultSchoolData;} catch (URISyntaxException e) {e.printStackTrace();}}

    private String localUserData; // Local (within the app files) path to the json data
    {try {localUserData = new File(new File(new File(SchoolDisplay.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()).getAbsolutePath()).getParent() + SchoolCalendar.FILE_SEP + "json" + SchoolCalendar.FILE_SEP + "User.json";} catch (URISyntaxException e) {e.printStackTrace();}}

    private PeriodData match; // Current period match
    private PeriodData nextMatch; // Next period match


    // ----------------------------------------------------------------------------------------------------
    // public SchoolDisplay
    //
    public SchoolDisplay() throws Exception {
        // Check that the json data files needed exist where they should and create any files based
        // on defaults if there are no files present
        this.checkForJsonData();
        schoolData = System.getProperty("user.home") +
                SchoolCalendar.PC_PATH + "periodcountdown" + SchoolCalendar.FILE_SEP +
                "json" + SchoolCalendar.FILE_SEP +
                new SchoolYear(localSchoolData, userData).getSchoolFileName();
        this.checkForJsonData();

        CalendarHelper.calendarAssert((new File(schoolData).exists()) &&
                (!new File(schoolData).isDirectory()) &&
                (new File(userData).exists()) &&
                (!new File(userData).isDirectory()),
                "SchoolDisplay.SchoolDisplay could not find json data");

        this.schoolCalendar = new SchoolCalendar(schoolData, userData);
    }
    // end: public SchoolDisplay


    // ====================================================================================================
    // GET methods
    public SchoolCalendar getSchoolCalendar() {
        return schoolCalendar;
    }

    public static String getSchoolData() {
        return schoolData;
    }
    // end: GET methods


    // ====================================================================================================
    // private void checkForJsonData
    //
    // Checks for the presence of required JSON data and creates it if it does not exist
    //
    private void checkForJsonData() throws IOException {
        // Check if there is a directory at .periodcountdown
        if (!new File(periodCountdownDirectory).exists() ||
                !new File(periodCountdownDirectory).isDirectory()) {
            Files.createDirectories(Paths.get(periodCountdownDirectory));
            Files.createDirectories(Paths.get(periodCountdownDirectory + "/json"));
        }

        // Check if there is a school data file
        if (!new File(schoolData).exists() || new File(schoolData).isDirectory()) {
            Gson json = new Gson();
            Map schoolDataCopy = json.fromJson(new FileReader(localSchoolData), Map.class);

            Writer schoolDataWriter = new FileWriter(SchoolDisplay.schoolData);
            new Gson().toJson(schoolDataCopy, schoolDataWriter);
            schoolDataWriter.close();
        }

        // Check if there is a user data file
        if (!new File(userData).exists() || new File(userData).isDirectory()) {
            Gson json = new Gson();
            Map userDataCopy = json.fromJson(new FileReader(localUserData), Map.class);

            Writer userDataWriter = new FileWriter(SchoolDisplay.userData);
            new Gson().toJson(userDataCopy, userDataWriter);
            userDataWriter.close();
        }
    }
    // end: private void checkForJsonData


    // ====================================================================================================
    // public void refreshPeriod
    //
    // Refresh the current period match data -- done automatically when calling refreshTimeRemaining or
    // getPeriodStatus
    //
    // Arguments--
    //
    // epochDate:   the Calendar object to refresh the period match data at
    //
    public void refreshPeriod(Calendar epochDate) throws Exception {
        this.match = this.schoolCalendar.getPeriodByDateAndTime(epochDate);

        if (match != null) {
            nextMatch = this.schoolCalendar.getNextPeriod(match, true, true);
        }
    }
    // end: public void refreshPeriod


    // ====================================================================================================
    // public String getRemainingTime
    //
    // Get the time remaining in a given period as a formatted string
    //
    // Arguments--
    //
    // epochDate:   the Calendar datetime object to get the time remaining at
    //
    // Returns--
    //
    // The time remaining in the period as a string in the format hh:mm:ss
    //
    public String getRemainingTime(Calendar epochDate) throws Exception {
        this.refreshPeriod(epochDate);

        // For the first possibility, if the match is null then it is either summer or there is a gap
        // in the json data. For the summer procedure, the estimated time until the next school year
        // must be calculated
        if (match == null) {
            // When calculating the summer time, assume that school starts at the beginning of the day.
            // Next get the week pattern for the first week (like with normal weeks, assume this is the
            // default week pattern before scanning for an exception to that pattern). Later on, the
            // start time for the first period will also be found after the week pattern has been
            // found
            String firstPeriodStartTime = "00:00:00";

            Calendar firstWeekendDate = this.schoolCalendar.getFirstWeekendDate();
            String firstWeekendString = this.schoolCalendar.getDayTag(firstWeekendDate);
            ArrayList<Map<String, String>> firstWeek = this.schoolCalendar.schoolYear
                    .getWeekPattern(SchoolCalendar.get_DEFAULT_WEEK);
            if (this.schoolCalendar.schoolYear.getWeekExceptionByWeekTag(firstWeekendString) != null) {
                String weekExceptionTag = this.schoolCalendar.schoolYear
                        .getWeekExceptionByWeekTag(firstWeekendString).get(SchoolCalendar.getWeeksTerm);
                firstWeek = this.schoolCalendar.schoolYear
                        .getWeekPattern(weekExceptionTag);
            }

            // Loop through each of the periods in each of the days in the first week to find the
            // real start time of the first period
            for (Map<String, String> dayInWeek : firstWeek) {
                ArrayList<Map<String, Object>> day = this.schoolCalendar.schoolYear
                        .getDayPattern(dayInWeek.get(SchoolCalendar.getDaysTerm));

                for (Map<String, Object> periodInDay : day) {
                    if ((int) (double) periodInDay.get(SchoolCalendar.getPeriodTerm) > 0) {
                        firstPeriodStartTime = periodInDay.get(SchoolCalendar.getStartTimeTerm) + ":00";
                        break;
                    }
                }
            }

            // After the start time and day of the school year have been determined above, we then turn
            // that information into data structures that can be used to find the time remaining
            Calendar firstPeriodEpochDate = CalendarHelper.createEpochTime(
                    this.schoolCalendar.schoolYear.getFirstDate() + "T" + firstPeriodStartTime);
            PeriodData firstPeriodData = this.schoolCalendar.getPeriodByDateAndTime(firstPeriodEpochDate);
            TimeData summerTimeLeft = this.schoolCalendar.getTimeRemainingUntilPeriod(
                    epochDate,
                    firstPeriodData.getDay(),
                    firstPeriodData.getPeriod());

            String timeString =
                    (summerTimeLeft.getDays() * SchoolCalendar.hoursPerDay) + summerTimeLeft.getHours()
                    + ":" +
                    CalendarHelper.padStringLeft(String.valueOf(summerTimeLeft.getMinutes()), 2, '0')
                    + ":" +
                    CalendarHelper.padStringLeft(String.valueOf(summerTimeLeft.getSeconds()), 2, '0');
            return timeString;
        }

        // The second possibility for the match is non-null. This means that there is some match and
        // the time to the next period can likely be calculated
        else {
            // Get some information about the current and next match
            SchoolPeriod matchPObj = match.getPeriod();
            PeriodData nextMatch = this.schoolCalendar.getNextPeriod(match, true, true);
            TimeData timeLeft;

            // If the period is between the minimum and maximum periods (or it is a declared free period),
            // it is a real period and the time can be calculated until the end of that period
            if (
                    (matchPObj.getPeriod() >= this.schoolCalendar.schoolYear.getFirstPeriod() &&
                    matchPObj.getPeriod() <= this.schoolCalendar.schoolYear.getLastPeriod()) ||
                    matchPObj.getPeriod() == SchoolCalendar.freePeriod
            ) {
                timeLeft = this.schoolCalendar.getTimeRemainingInPeriod(epochDate, matchPObj);
                return timeLeft.getHours()
                        + ":" +
                        CalendarHelper.padStringLeft(String.valueOf(timeLeft.getMinutes()), 2, '0')
                        + ":" +
                        CalendarHelper.padStringLeft(String.valueOf(timeLeft.getSeconds()), 2, '0');
            }
            // If the period is not an official period, then the time calculated must include any leftover
            // time from other unofficial periods (ex: if it is lunch and the next period is a passing
            // period, then the time should be the combination of both)
            else {
                timeLeft = this.schoolCalendar.getTimeRemainingUntilPeriod(epochDate, nextMatch.getDay(),
                        nextMatch.getPeriod(), true);
                return timeLeft.getHours()
                        + ":" +
                        CalendarHelper.padStringLeft(String.valueOf(timeLeft.getMinutes()), 2, '0')
                        + ":" +
                        CalendarHelper.padStringLeft(String.valueOf(timeLeft.getSeconds()), 2, '0');
            }
        }
    }
    // end: public String getRemainingTime


    // ====================================================================================================
    // public String getPeriodStatus
    //
    // Returns the type and status of the period at the given time (ex: "Before School | Free" or
    // "History | Per 2")
    //
    // Arguments--
    //
    // epochDate:   the Calendar datetime object to get the period status at
    //
    // Returns--
    //
    // The status message of the period specified in epochDate
    //
    public String getPeriodStatus(Calendar epochDate) throws Exception {
        this.refreshPeriod(epochDate);

        // After refreshing the period there are a few possibilities for what should be done:
        //  If the match found is null, then the epochDate is outside the bounds of data specified
        //  in the school json file, so it is most likely summer (or there is an error in the json data).
        //  If the period from the match is within the available periods and there is class data (meaning
        //  it is not a free period) then return the period number and user-defined name
        //  If it is a free period, then just return the default period name and "Free"

        if (this.match == null)
            return "Summer | Free";

        SchoolPeriod schoolPeriodObject = this.match.getPeriod();
        SchoolClass schoolClassObject = schoolPeriodObject.getClassInfo();

        if (schoolPeriodObject.getPeriod() >= SchoolCalendar.getFirstPeriod() &&
                schoolPeriodObject.getPeriod() <= SchoolCalendar.getLastPeriod() &&
                schoolClassObject != null) {
            return schoolClassObject.getClassName() + " | " + schoolPeriodObject.getName();
        }
        else {
            return schoolPeriodObject.getName() + " | Free";
        }
    }
    // end: public String getPeriodStatus


    // ====================================================================================================
    // public String toString
    //
    // SchoolClass toString method
    //
    // Returns--
    //
    // Textual representation of this class
    //
    @Override
    public String toString() {
        String classDescription = "\n\tschoolData:\t" + schoolData + "\n\tuserData:\t" + userData
                + "\n\tmatch:\t" + this.match;
        return super.toString() + ": " + classDescription;
    }
    // end: public String toString

}
// end: public class SchoolDisplay