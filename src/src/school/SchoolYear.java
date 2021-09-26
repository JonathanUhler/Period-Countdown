// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolYear.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/25/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolYear.java
// Class Diagram
/*

+-----------------------------------------------------------------------------------------------------+
|                                              SchoolYear                                             |
+-----------------------------------------------------------------------------------------------------+
| -Days: Map<String, ArrayList<Map<String, Object>>>                                                  |
| -Weeks: Map<String, ArrayList<Map<String, String>>>                                                 |
| -Exceptions: ArrayList<Map<String, String>>                                                         |
| -Info: Map<String, Object>                                                                          |
| -User: Map<String, Map<String, Object>>                                                             |
| -dayTypes: ArrayList<String>                                                                        |
| -weekTypes: ArrayList<String>                                                                       |
| -schoolStructureFile: String                                                                        |
| -userFile: String                                                                                   |
+-----------------------------------------------------------------------------------------------------+
| +SchoolYear(String, String)                                                                         |
+-----------------------------------------------------------------------------------------------------+
| -initSchoolData(): void                                                                             |
| -initUserData(): void                                                                               |
| +getDayTypes(): ArrayList<String>                                                                   |
| +getFirstPeriod(): int                                                                              |
| +getLastPeriod(): int                                                                               |
| +getFirstDate(): String                                                                             |
| +getLastDate(): String                                                                              |
| +getUserNextUp(): int                                                                               |
| +getUserTheme(): String                                                                             |
| +getSchoolFileName(): String                                                                        |
| +getPeriodNameByNumber(int): String                                                                 |
| +getUserDataByPeriod(int): Map<String, Object>                                                      |
| +getDayPattern(String): ArrayList<Map<String, Object>>                                              |
| +getPeriodPatternByIndex(String, int): Map<String, Object>                                          |
| +getPeriodPatternByName(String, String): Map<String, Object>                                        |
| +getPeriodPatternByNumber(int): Map<String, Object>                                                 |
| +getWeekPattern(String): ArrayList<Map<String, String>>                                             |
| +getWeekExceptionByWeekTag(String): Map<String, String>                                             |
| +getDayType(String, int): String                                                                    |
| +setUserSchoolFile(String): void                                                                    |
| +setUserNextUp(int): void                                                                           |
| +setUserTheme(String): void                                                                         |
| +setUserPeriodNameByPeriod(int, String): void                                                       |
| +setUserPeriodInfoByPeriod(int, String, String): void                                               |
| +toString(): String                                                                                 |
+-----------------------------------------------------------------------------------------------------+

*/
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package school;


import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import calendar.*;
import graphics.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class SchoolYear
//
// Defines the structure for the school year
//
public class SchoolYear {

    // Data structures containing the information from the JSON files, parsed and in a more manageable form
    private Map<String, ArrayList<Map<String, Object>>> Days;
    private Map<String, ArrayList<Map<String, String>>> Weeks;
    private ArrayList<Map<String, String>> Exceptions;
    private Map<String, Object> Info;
    private Map<String, Map<String, Object>> User;

    // Lists of the types of weeks and days defined
    private final ArrayList<String> dayTypes;
    private final ArrayList<String> weekTypes;

    // School json files
    private final String schoolStructureFile;
    private final String userFile;


    // ----------------------------------------------------------------------------------------------------
    // public SchoolYear
    //
    // Constructor for SchoolYear class
    //
    // Arguments--
    //
    // schoolStructureFile:     the JSON file containing the structure for all school days, weeks, and week exceptions
    //
    // userFile:                the JSON file containing user specific data
    //
    public SchoolYear(String schoolStructureFile, String userFile) throws Exception {
        // Set json files
        this.schoolStructureFile = schoolStructureFile;
        this.userFile = userFile;

        // Check that all the files exist and are not directories
        CalendarHelper.calendarAssert((new File(schoolStructureFile).exists()) &&
                (!new File(schoolStructureFile).isDirectory()),
                "SchoolYear.SchoolYear constructed with invalid arguments",
                schoolStructureFile);

        this.initSchoolData();
        this.initUserData();

        // Store a list of the types of days and weeks that have been defined in the JSON files
        this.dayTypes = new ArrayList<>(this.Days.keySet());
        this.weekTypes = new ArrayList<>(this.Weeks.keySet());
    }
    // end: public SchoolYear


    // ====================================================================================================
    // private void initSchoolData
    //
    // Initialize/update hashmaps representing the json school data
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void initSchoolData() throws FileNotFoundException {
        // Create a new Gson object to parse
        Gson json = new Gson();

        // Parse all the JSON files and cast
        // HACK: These lines use unsafe casts, but this is *probably* okay in practice since a Python program
        // HACK cont: is responsible for generating the JSON files and the correct info should be there
        this.Days = (Map<String, ArrayList<Map<String, Object>>>) json.fromJson(new FileReader(schoolStructureFile), Map.class).get(SchoolCalendar.getDaysTerm);
        this.Weeks = (Map<String, ArrayList<Map<String, String>>>) json.fromJson(new FileReader(schoolStructureFile), Map.class).get(SchoolCalendar.getWeeksTerm);
        this.Exceptions = (ArrayList<Map<String, String>>) json.fromJson(new FileReader(schoolStructureFile), Map.class).get("Exceptions");
        this.Info = (Map<String, Object>) json.fromJson(new FileReader(schoolStructureFile), Map.class).get(SchoolCalendar.getInfoTerm);
    }
    // private void initSchoolData


    // ====================================================================================================
    // private void initUserData
    //
    // Initialize/update hashmaps representing the json user data
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void initUserData() throws FileNotFoundException {
        // Create a new Gson object to parse
        Gson json = new Gson();

        // Parse all the JSON files and cast
        // HACK: These lines use unsafe casts, but this is *probably* okay in practice since a Python program
        // HACK cont: is responsible for generating the JSON files and the correct info should be there
        this.User = (Map<String, Map<String, Object>>) json.fromJson(new FileReader(userFile), Map.class).get(SchoolCalendar.getUserTerm);
    }
    // end: private void initUserData


    // ====================================================================================================
    // GET methods
    public ArrayList<String> getDayTypes() {
        return dayTypes;
    }

    public int getFirstPeriod() {
        return (int) (double) Info.get(SchoolCalendar.getFirstPeriod);
    }

    public int getLastPeriod() {
        return (int) (double) Info.get(SchoolCalendar.getLastPeriod);
    }

    public String getFirstDate() {
        return (String) Info.get(SchoolCalendar.getFirstDate);
    }

    public String getLastDate() {
        return (String) Info.get(SchoolCalendar.getLastDate);
    }

    public int getUserNextUp() throws FileNotFoundException {
        this.initSchoolData();
        this.initUserData();
        return (int) (double) this.User.get(SchoolCalendar.getSettingsTerm).get(SchoolCalendar.getNextUpTerm);
    }

    public String getUserTheme() {
        return (String) this.User.get(SchoolCalendar.getSettingsTerm).get(SchoolCalendar.getThemeTerm);
    }

    public String getSchoolFileName() {
        return (String) this.User.get(SchoolCalendar.getSettingsTerm).get(SchoolCalendar.getSchoolFileTerm);
    }
    // end: GET methods


    // ====================================================================================================
    // public String getPeriodNameByNumber
    //
    // Gets the name of a period from the json data by its period number
    //
    // Arguments--
    //
    // period:  the period number as an interger to get the name for
    //
    // Returns--
    //
    // The name of the period, if found
    //
    public String getPeriodNameByNumber(int period) throws Exception {
        // Check that the period passed in is within the first and last possible periods
        CalendarHelper.calendarAssert((period >= this.getFirstPeriod()) &&
                (period <= this.getLastPeriod()),
                "SchoolYear.getPeriodNameByNumber called with invalid arguments",
                String.valueOf(period));

        // Search through each of the day types
        for (ArrayList<Map<String, Object>> dayTypeFormat : this.Days.values()) {
            // Search through each of the periods in each of the days
            for (Map<String, Object> periodTypeFormat : dayTypeFormat) {
                // Return the period name if found
                if ((int) (double) periodTypeFormat.get(SchoolCalendar.getPeriodTerm) == period) {
                    return (String) periodTypeFormat.get(SchoolCalendar.getNameTerm);
                }
            }
        }

        // If nothing was found, return an empty string
        return "";
    }
    // end: public String getPeriodNameByNumber


    // ====================================================================================================
    // public Map<String, Object> getUserDataByPeriod
    //
    // Get user specific data for a given period number
    //
    // Arguments--
    //
    // period:  the period number to get user data for
    //
    // Returns--
    //
    // User data for the given period
    //
    public Map<String, Object> getUserDataByPeriod(int period) throws Exception {
        CalendarHelper.calendarAssert(this.User.containsKey(String.valueOf(period)),
                "SchoolYear.getUserDataByPeriod called with invalid arguments",
                String.valueOf(period));

        return this.User.get(String.valueOf(period));
    }
    // end: public Map<String, Object> getUserDataByPeriod


    // ====================================================================================================
    // public ArrayList<Map<String, Object>> getDayPattern
    //
    // Get method for a day definition/pattern
    //
    // Arguments--
    //
    // dayType: the string representing the type/schedule of the day to get
    //
    // Returns--
    //
    // The JSON data defining the day
    //
    public ArrayList<Map<String, Object>> getDayPattern(String dayType) throws Exception {
        // Check that the day type given exists
        CalendarHelper.calendarAssert((this.Days.containsKey(dayType)),
                "SchoolYear.getDayPattern called with invalid arguments",
                dayType);

        return this.Days.get(dayType);
    }
    // end: public ArrayList<Map<String, Object>> getDayPattern


    // ====================================================================================================
    // public Map<String, Object> getPeriodPatternByIndex
    //
    // Get method for a period definition/pattern by its index in the JSON array
    //
    // Arguments--
    //
    // dayType: the string representing the type/schedule of the day to get
    //
    // index:   the index in the JSON array to get from
    //
    // Returns--
    //
    // The JSON data defining the period
    //
    public Map<String, Object> getPeriodPatternByIndex(String dayType, int index) throws Exception {
        // Check that the day type given exists
        CalendarHelper.calendarAssert((this.Days.containsKey(dayType)),
                "SchoolYear.getPeriodPatternByIndex called with invalid arguments",
                dayType);

        // Check that the index given exists
        CalendarHelper.calendarAssert((this.Days.get(dayType).size() > index),
                "SchoolYear.getPeriodPatternByIndex called with invalid arguments",
                Integer.toString(index));

        return this.Days.get(dayType).get(index);
    }
    // end: public Map<String, Object> getPeriodPatternByIndex


    // ====================================================================================================
    //  public Map<String, Object> getPeriodPatternByName
    //
    // Get method for a period definition/pattern by its name in the JSON array
    //
    // Arguments--
    //
    // dayType: the string representing the type/schedule of the day to get
    //
    // name:    the name of the period to get the JSON data for
    //
    // Returns--
    //
    // The JSON data defining the period
    //
    public Map<String, Object> getPeriodPatternByName(String dayType, String name) throws Exception {
        // Check that the day type given exists
        CalendarHelper.calendarAssert((this.Days.containsKey(dayType)),
                "SchoolYear.getPeriodPatternByName called with invalid arguments",
                dayType);

        // Loop through the JSON array of period data to find the correct one
        for (Map<String, Object> periodPattern : this.Days.get(dayType)) {
            if (periodPattern.get(SchoolCalendar.getNameTerm).equals(name)) {
                // Return if found
                return periodPattern;
            }
        }

        // If nothing was found, return null
        return null;
    }
    // end:  public Map<String, Object> getPeriodPatternByName


    // ====================================================================================================
    //  public Map<String, Object> getPeriodPatternByNumber
    //
    // Get method for a period definition/pattern by its number in the JSON array
    //
    // Arguments--
    //
    // periodID:    the number of the period to get (cannot be -1)
    //
    // Returns--
    //
    // The JSON data defining the period
    //
    public Map<String, Object> getPeriodPatternByNumber(int periodID) throws Exception {
        // Check that the day type given exists
        CalendarHelper.calendarAssert((periodID > 0),
                "SchoolYear.getPeriodPatternByName called with invalid arguments",
                Integer.toString(periodID));

        // Loop through the JSON array of period data to find the correct one
        for (String dayType : this.dayTypes) {
            for (Map<String, Object> periodPattern : this.Days.get(dayType)) {
                if ((int) (double) periodPattern.get(SchoolCalendar.getPeriodTerm) == periodID) {
                    // Return if found
                    return periodPattern;
                }
            }
        }

        // If nothing was found, return null
        return null;
    }
    // end:  public Map<String, Object> getPeriodPatternByNumber


    // ====================================================================================================
    // public ArrayList<Map<String, String>> getWeekPattern
    //
    // Get method for a week definition/schedule
    //
    // Arguments--
    //
    // weekType:    the type of week to get
    //
    // Returns--
    //
    // The data about the given week type
    //
    public ArrayList<Map<String, String>> getWeekPattern(String weekType) throws Exception {
        // Check that the week type given exists
        CalendarHelper.calendarAssert((this.Weeks.containsKey(weekType)),
                "SchoolYear.getWeekPattern called with invalid arguments",
                weekType);

        return this.Weeks.get(weekType);
    }
    // end: public ArrayList<Map<String, String>> getWeekPattern


    // ====================================================================================================
    // public Map<String, String> getWeekExceptionByWeekTag
    //
    // Get method for a week exception by its week tag
    //
    // Arguments--
    //
    // weekTag: the week tag string to get the week exception for
    //
    // Returns--
    //
    // The data about the week exception
    //
    public Map<String, String> getWeekExceptionByWeekTag(String weekTag) {
        // Loop through the JSON array of week exceptions to find the correct one
        for (Map<String, String> weekException : this.Exceptions) {
            if (CalendarHelper.createWeekTag(weekException.get(SchoolCalendar.getWeekTagTerm)).equals(CalendarHelper.createWeekTag(weekTag))) {
                // Return if found
                return weekException;
            }
        }

        // If nothing was found, return null
        return null;
    }
    // end: public Map<String, String> getWeekExceptionByWeekTag


    // ====================================================================================================
    // public String getDayType
    //
    // Get method for the type of a day
    //
    // Arguments--
    //
    // weekTag:     the yyyy-mm-dd date string that represents the Sunday for the week with the day to get
    //
    // dayIndex:    the index of the day (0-6 for Sunday-Saturday) to get from the week
    //
    // Returns--
    //
    // The type of the day
    //
    public String getDayType(String weekTag, int dayIndex) throws Exception {
        // Check that all arguments are valid
        CalendarHelper.calendarAssert((weekTag != null) &&
                        (dayIndex >= 0) &&
                        (dayIndex <= (SchoolCalendar.daysPerWeek - 1)),
                "SchoolYear.getDayName called with invalid arguments",
                weekTag, Integer.toString(dayIndex));

        // Get the structure for the default week
        ArrayList<Map<String, String>> weekStructure = getWeekPattern(SchoolCalendar.get_DEFAULT_WEEK);

        // Check if there is an exception for the weekTag given, if there is then use that week structure
        if (getWeekExceptionByWeekTag(weekTag) != null) {
            weekStructure = getWeekPattern(getWeekExceptionByWeekTag(weekTag).get(SchoolCalendar.getWeeksTerm));
        }

        // Return the type of day
        return weekStructure.get(dayIndex).get(SchoolCalendar.getDaysTerm);
    }
    // end: public String getDayType


    // ====================================================================================================
    // public void setUserSchoolFile
    //
    // Sets the new name of the user's school data json file
    //
    // Arguments--
    //
    // file:    the name of the new file
    //
    // Returns--
    //
    // None
    //
    public void setUserSchoolFile(String file) throws Exception {
        // Create and store the new next up data
        Map<String, Object> newSchoolFile = this.User.get(SchoolCalendar.getSettingsTerm);
        newSchoolFile.put(SchoolCalendar.getSchoolFileTerm, file);
        this.User.put(SchoolCalendar.getSettingsTerm, newSchoolFile);

        // Create the entire file structure for the user file
        Map<String, Map<String, Map<String, Object>>> editedUser = new HashMap<>();
        editedUser.put(SchoolCalendar.getUserTerm, this.User);

        // Write out the data
        Writer writer = new FileWriter(SchoolDisplay.userData);
        new Gson().toJson(editedUser, writer);
        writer.close();
    }
    // end: public void setUserSchoolFile


    // ====================================================================================================
    // public void setUserNextUp
    //
    // Set the next up verbosity level for the user file
    //
    // Arguments--
    //
    // verbosity:   the verbosity of the next up display to write to the file
    //
    // Returns--
    //
    // None
    //
    public void setUserNextUp(int verbosity) throws Exception {
        // Check that the verbosity level is between the least and most verbose
        CalendarHelper.calendarAssert((verbosity >= NextUp.NONE) &&
                (verbosity <= NextUp.ALL_ALL_INFO),
                "SchoolYear.setUserNextUp called with invalid arguments",
                String.valueOf(verbosity));

        // Create and store the new next up data
        Map<String, Object> newNextUp = this.User.get(SchoolCalendar.getSettingsTerm);
        newNextUp.put(SchoolCalendar.getNextUpTerm, verbosity);
        this.User.put(SchoolCalendar.getSettingsTerm, newNextUp);

        // Create the entire file structure for the user file
        Map<String, Map<String, Map<String, Object>>> editedUser = new HashMap<>();
        editedUser.put(SchoolCalendar.getUserTerm, this.User);

        // Write out the data
        Writer writer = new FileWriter(SchoolDisplay.userData);
        new Gson().toJson(editedUser, writer);
        writer.close();
    }
    // end: public void setUserNextUp


    // ====================================================================================================
    // public void setUserTheme
    //
    // Update the user's chosen hex theme
    //
    // Arguments--
    //
    // theme:   the hex number that represents a color
    //
    // Returns--
    //
    // None
    //
    public void setUserTheme(String theme) throws Exception {
        // Create and store the new theme data
        Map<String, Object> newTheme = this.User.get(SchoolCalendar.getSettingsTerm);
        newTheme.put(SchoolCalendar.getThemeTerm, theme);
        this.User.put(SchoolCalendar.getSettingsTerm, newTheme);

        // Create the entire file structure for the user file
        Map<String, Map<String, Map<String, Object>>> editedUser = new HashMap<>();
        editedUser.put(SchoolCalendar.getUserTerm, this.User);

        // Write out the data
        Writer writer = new FileWriter(SchoolDisplay.userData);
        new Gson().toJson(editedUser, writer);
        writer.close();
    }
    // end: public void setUserTheme


    // ====================================================================================================
    // public void setUserPeriodNameByPeriod
    //
    // Set the name of a user's period by the period number
    //
    // Arguments--
    //
    // period:  the period number to set the name for
    //
    // name:    the new name to set
    //
    // Returns--
    //
    // None
    //
    public void setUserPeriodNameByPeriod(int period, String name) throws Exception {
        // Check that the period number is valid
        CalendarHelper.calendarAssert((period >= SchoolCalendar.getFirstPeriod()) &&
                (period <= SchoolCalendar.getLastPeriod()),
                "SchoolYear.setUserPeriodNameByPeriod called with invalid arguments",
                String.valueOf(period));

        // Create and store the new period name data
        Map<String, Object> newPeriod = this.User.get(String.valueOf(period));
        newPeriod.put(SchoolCalendar.getNameTerm, name);
        this.User.put(String.valueOf(period), newPeriod);

        // Add the edited data to the larger user data
        Map<String, Map<String, Map<String, Object>>> editedUser = new HashMap<>();
        editedUser.put(SchoolCalendar.getUserTerm, this.User);

        // Write out the entire new data
        Writer writer = new FileWriter(SchoolDisplay.userData);
        new Gson().toJson(editedUser, writer);
        writer.close();
    }
    // end: public void setUserPeriodNameByPeriod


    // ====================================================================================================
    // setUserPeriodInfoByPeriod
    //
    // Set the teacher name and room number of a period by its period number
    //
    // Arguments--
    //
    // period:      the period number to set the information for
    //
    // teacherName: the name of the teacher to set
    //
    // roomNUmber:  the room number to set
    //
    // Returns--
    //
    // None
    //
    public void setUserPeriodInfoByPeriod(int period, String teacherName, String roomNumber) throws Exception {
        // Check that the period number is valid
        CalendarHelper.calendarAssert((period >= SchoolCalendar.getFirstPeriod()) &&
                (period <= SchoolCalendar.getLastPeriod()),
                "SchoolYear.setUserPeriodNameByPeriod called with invalid arguments",
                String.valueOf(period));

        // Set the data to a new object
        Map<String, Object> newPeriodInfo = this.User.get(String.valueOf(period));
        newPeriodInfo.put(SchoolCalendar.getTeacherNameTerm, teacherName);
        newPeriodInfo.put(SchoolCalendar.getRoomNumberTerm, roomNumber);
        this.User.put(String.valueOf(period), newPeriodInfo);

        // Store the data in the larger group of user's data
        Map<String, Map<String, Map<String, Object>>> editedUser = new HashMap<>();
        editedUser.put(SchoolCalendar.getUserTerm, this.User);

        // Write out the entire data file
        Writer writer = new FileWriter(SchoolDisplay.userData);
        new Gson().toJson(editedUser, writer);
        writer.close();
    }
    // end: public void setUserPeriodInfoByPeriod


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
        String classDescription = "\n\tdayTypes:\t" + this.dayTypes + "\n\tweekTypes:\t" + this.weekTypes;
        return super.toString() + ": " + classDescription;
    }
    // end: public String toString

}