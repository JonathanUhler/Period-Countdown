// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolDisplay.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/2/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolDisplay.java
// Class Diagram
/*

+-----------------------------------------------------------------------------------------------------+
|                                            SchoolDisplay                                            |
+-----------------------------------------------------------------------------------------------------+
| -schoolCalendar: SchoolCalendar                                                                     |
| +defaultSchoolData: String                                                                          |
| +periodCountdownDirectory: String                                                                   |
| -schoolData: String                                                                                 |
| +userData: String                                                                                   |
| -localSchoolData: String                                                                            |
| -localUserData: String                                                                              |
| -match: PeriodData                                                                                  |
| -nextMatch: PeriodData                                                                              |
+-----------------------------------------------------------------------------------------------------+
| +SchoolDisplay()                                                                                    |
+-----------------------------------------------------------------------------------------------------+
| +getSchoolCalendar(): SchoolCalendar                                                                |
| +getSchoolData(): String                                                                            |
| -checkForJsonData(): void                                                                           |
| +refreshPeriod(Calendar): void                                                                      |
| +getRemainingTime(Calendar): String                                                                 |
| +getPeriodStatus(Calendar): String                                                                  |
| +toString(): String                                                                                 |
+-----------------------------------------------------------------------------------------------------+

*/
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
    // Arguments--
    //
    // None
    //
    public SchoolDisplay() throws Exception {
        // Make sure the JSON data is in its place
        this.checkForJsonData();

        schoolData = System.getProperty("user.home") + SchoolCalendar.PC_PATH + "periodcountdown" + SchoolCalendar.FILE_SEP + "json" + SchoolCalendar.FILE_SEP + new SchoolYear(localSchoolData, userData).getSchoolFileName();

        this.checkForJsonData();

        // Check that the json data exists where it should
        CalendarHelper.calendarAssert((new File(schoolData).exists()) &&
                (!new File(schoolData).isDirectory()) &&
                (new File(userData).exists()) &&
                (!new File(userData).isDirectory()),
                "SchoolDisplay.SchoolDisplay could not find json data");

        // Initialize the school calendar instance for this class
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
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void checkForJsonData() throws IOException {
        // Check if there is a directory at .periodcountdown
        if (!new File(periodCountdownDirectory).exists() || !new File(periodCountdownDirectory).isDirectory()) {
            Files.createDirectories(Paths.get(periodCountdownDirectory)); // Create the .periodcountdown directory
            Files.createDirectories(Paths.get(periodCountdownDirectory + "/json")); // Create the .periodcountdown/json directory
        }

        // Check if there is a school data file
        if (!new File(schoolData).exists() || new File(schoolData).isDirectory()) {
            Gson json = new Gson();
            Map schoolDataCopy = json.fromJson(new FileReader(localSchoolData), Map.class); // Copy the school data from the local file within the app

            Writer schoolDataWriter = new FileWriter(SchoolDisplay.schoolData); // Write the local data to a new file in .periodcountdown/json
            new Gson().toJson(schoolDataCopy, schoolDataWriter);
            schoolDataWriter.close();
        }

        // Check if there is a user data file
        if (!new File(userData).exists() || new File(userData).isDirectory()) {
            Gson json = new Gson();
            Map userDataCopy = json.fromJson(new FileReader(localUserData), Map.class); // Copy the user data from the local file within the app

            Writer userDataWriter = new FileWriter(SchoolDisplay.userData); // Write the local data to a new file in .periodcountdown/json
            new Gson().toJson(userDataCopy, userDataWriter);
            userDataWriter.close();
        }
    }
    // end: private void checkForJsonData


    // ====================================================================================================
    // public void refreshPeriod
    //
    // Refresh the current period match data -- done automatically when calling refreshTimeRemaining or getPeriodStatus
    //
    // Arguments--
    //
    // epochDate:   the Calendar object to refresh the period match data at
    //
    // Returns--
    //
    // None
    //
    public void refreshPeriod(Calendar epochDate) throws Exception {
        this.match = this.schoolCalendar.getPeriodByDateAndTime(epochDate); // Set the class's match data

        // If there was a match, set the next period match as well
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
        this.refreshPeriod(epochDate); // Refresh the period match data

        // If the match is null, it is the summer
        if (match == null) {
            String firstPeriodStartTime = "00:00:00"; // Initialize a variable to serve as the start time for the first period of the upcoming school year

            String firstWeekendString = this.schoolCalendar.getDayTag(this.schoolCalendar.getFirstWeekendDate()); // Get the first weekend tag for the upcoming year
            ArrayList<Map<String, String>> firstWeek = this.schoolCalendar.schoolYear.getWeekPattern(SchoolCalendar.get_DEFAULT_WEEK); // Initially set the first week pattern as the deafult week

            // Check if there is a week exception for the first week of school and set firstWeek accordingly
            if (this.schoolCalendar.schoolYear.getWeekExceptionByWeekTag(firstWeekendString) != null) {
                firstWeek = this.schoolCalendar.schoolYear.getWeekPattern(this.schoolCalendar.schoolYear.getWeekExceptionByWeekTag(firstWeekendString).get(SchoolCalendar.getWeeksTerm));
            }

            // Loop through each of the days in the first week of the upcoming year
            for (Map<String, String> dayInWeek : firstWeek) {
                // Get the day in the week
                ArrayList<Map<String, Object>> day = this.schoolCalendar.schoolYear.getDayPattern(dayInWeek.get(SchoolCalendar.getDaysTerm));

                // Loop through each of the periods for each of the days
                for (Map<String, Object> periodInDay : day) {
                    // Find the first period
                    if ((int) (double) periodInDay.get(SchoolCalendar.getPeriodTerm) > 0) {
                        // Set the time for the very first period of the upcoming school year
                        firstPeriodStartTime = periodInDay.get(SchoolCalendar.getStartTimeTerm) + ":00";
                        break;
                    }
                }
            }

            // Get a Calendar object representing the very first period and day of the school year
            Calendar firstPeriodEpochDate = CalendarHelper.createEpochTime(this.schoolCalendar.schoolYear.getFirstDate() + "T" + firstPeriodStartTime);
            PeriodData firstPeriodHashmap = this.schoolCalendar.getPeriodByDateAndTime(firstPeriodEpochDate);
            // Get the time left between the current time and the first period of the year -- this is the time left in the summer
            TimeData summerTimeLeft = this.schoolCalendar.getTimeRemainingUntilPeriod(epochDate, firstPeriodHashmap.getDay(), firstPeriodHashmap.getPeriod());

            // Create and return a formatted string for the time
            String timeString = summerTimeLeft.getHours() + ":" +
                    CalendarHelper.padStringLeft(String.valueOf(summerTimeLeft.getMinutes()), 2, '0') + ":" +
                    CalendarHelper.padStringLeft(String.valueOf(summerTimeLeft.getSeconds()), 2, '0');
            return timeString;
        }
        // If the match is not null, it is during the school year
        else {
            // Get some information about the current and next match
            SchoolPeriod matchPObj = match.getPeriod();
            PeriodData nextMatch = this.schoolCalendar.getNextPeriod(match, true, true);
            TimeData timeLeft;

            // If the period is between the minimum and maximum periods, it is a real period
            if (
                    (matchPObj.getPeriod() >= this.schoolCalendar.schoolYear.getFirstPeriod() && // If the period number is between the min and max
                    matchPObj.getPeriod() <= this.schoolCalendar.schoolYear.getLastPeriod()) ||
                    matchPObj.getPeriod() == SchoolCalendar.freePeriod // Or the period number is -2 (free period)
            ) {
                timeLeft = this.schoolCalendar.getTimeRemainingInPeriod(epochDate, matchPObj); // Calculate the time left
                // Create and return the formatted string
                String timeString = timeLeft.getHours() + ":" +
                        CalendarHelper.padStringLeft(String.valueOf(timeLeft.getMinutes()), 2, '0') + ":" +
                        CalendarHelper.padStringLeft(String.valueOf(timeLeft.getSeconds()), 2, '0');
                return timeString;
            }
            // The period is not a real period
            else {
                // Get the time left
                timeLeft = this.schoolCalendar.getTimeRemainingUntilPeriod(epochDate, nextMatch.getDay(), nextMatch.getPeriod(), true);
                // Create and return the formatted string
                String timeString = timeLeft.getHours() + ":" +
                        CalendarHelper.padStringLeft(String.valueOf(timeLeft.getMinutes()), 2, '0') + ":" +
                        CalendarHelper.padStringLeft(String.valueOf(timeLeft.getSeconds()), 2, '0');
                return timeString;
            }
        }
    }
    // end: public String getRemainingTime


    // ====================================================================================================
    // public String getPeriodStatus
    //
    // Returns the type and status of the period at the given time (ex: "Before School | Free" or "History | Per 2")
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
        this.refreshPeriod(epochDate); // Refresh the period match data

        // If the match is null, it is summer
        if (this.match == null) {
            return "Summer | Free";
        }

        // If the match is not null, get the period and class objects for the match
        SchoolPeriod schoolPeriodObject = this.match.getPeriod();
        SchoolClass schoolClassObject = schoolPeriodObject.getClassInfo();

        // If the match is a real period, return the name of the class and the period number
        if (schoolPeriodObject.getPeriod() >= SchoolCalendar.getFirstPeriod() && schoolPeriodObject.getPeriod() <= SchoolCalendar.getLastPeriod() && schoolClassObject != null) {
            return schoolClassObject.getClassName() + " | " + schoolPeriodObject.getName();
        }
        // If the match is not a real period, return the period name and the status as free
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
        String classDescription = "\n\tschoolData:\t" + schoolData + "\n\tuserData:\t" + userData
                + "\n\tmatch:\t" + this.match;
        return super.toString() + ": " + classDescription;
    }
    // end: public String toString

}
// end: public class SchoolDisplay