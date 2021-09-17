// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolYear.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/25/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


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
    private String schoolStructureFile;
    private String userFile;


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

        this.initJsonData();

        // Store a list of the types of days and weeks that have been defined in the JSON files
        this.dayTypes = new ArrayList<>(this.Days.keySet());
        this.weekTypes = new ArrayList<>(this.Weeks.keySet());
    }
    // end: public SchoolYear


    // ====================================================================================================
    // private void initJsonData
    //
    // Initialize/update hashmaps representing the json data
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void initJsonData() throws FileNotFoundException {
        // Create a new Gson object to parse
        Gson json = new Gson();

        // Parse all the JSON files and cast
        // HACK: These lines use unsafe casts, but this is *probably* okay in practice since a Python program
        // HACK cont: is responsible for generating the JSON files and the correct info should be there
        this.Days = (Map<String, ArrayList<Map<String, Object>>>) json.fromJson(new FileReader(schoolStructureFile), Map.class).get(SchoolCalendar.getDaysTerm);
        this.Weeks = (Map<String, ArrayList<Map<String, String>>>) json.fromJson(new FileReader(schoolStructureFile), Map.class).get(SchoolCalendar.getWeeksTerm);
        this.Exceptions = (ArrayList<Map<String, String>>) json.fromJson(new FileReader(schoolStructureFile), Map.class).get("Exceptions");
        this.Info = (Map<String, Object>) json.fromJson(new FileReader(schoolStructureFile), Map.class).get(SchoolCalendar.getInfoTerm);
        this.User = (Map<String, Map<String, Object>>) json.fromJson(new FileReader(userFile), Map.class).get(SchoolCalendar.getUserTerm);
    }
    // private void initJsonData


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
        this.initJsonData();
        return (int) (double) this.User.get(SchoolCalendar.getSettingsTerm).get(SchoolCalendar.getNextUpTerm);
    }
    // end: GET methods


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
            if (weekException.get(SchoolCalendar.getWeekTagTerm).equals(weekTag)) {
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
        Map<String, Object> newNextUp = new HashMap<>();
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


    public void setUserPeriodNameByPeriod(int period, String name) throws Exception {
        CalendarHelper.calendarAssert((period >= SchoolCalendar.getFirstPeriod()) &&
                (period <= SchoolCalendar.getLastPeriod()),
                "SchoolYear.setUserPeriodNameByPeriod called with invalid arguments",
                String.valueOf(period));

        Map<String, Object> newPeriod = this.User.get(String.valueOf(period));
        newPeriod.put(SchoolCalendar.getNameTerm, name);
        this.User.put(String.valueOf(period), newPeriod);

        Map<String, Map<String, Map<String, Object>>> editedUser = new HashMap<>();
        editedUser.put(SchoolCalendar.getUserTerm, this.User);

        Writer writer = new FileWriter(SchoolDisplay.userData);
        new Gson().toJson(editedUser, writer);
        writer.close();
    }


    public void setUserPeriodInfoByPeriod(int period, String teacherName, String roomNumber) throws Exception {
        CalendarHelper.calendarAssert((period >= SchoolCalendar.getFirstPeriod()) &&
                (period <= SchoolCalendar.getLastPeriod()),
                "SchoolYear.setUserPeriodNameByPeriod called with invalid arguments",
                String.valueOf(period));

        Map<String, Object> newPeriodInfo = this.User.get(String.valueOf(period));
        newPeriodInfo.put(SchoolCalendar.getTeacherNameTerm, teacherName);
        newPeriodInfo.put(SchoolCalendar.getRoomNumberTerm, roomNumber);
        this.User.put(String.valueOf(period), newPeriodInfo);

        Map<String, Map<String, Map<String, Object>>> editedUser = new HashMap<>();
        editedUser.put(SchoolCalendar.getUserTerm, this.User);

        Writer writer = new FileWriter(SchoolDisplay.userData);
        new Gson().toJson(editedUser, writer);
        writer.close();
    }


    /*
{
    "User": {
        "1": {"Period": "Per 1", "Name": "Algebra 2", "TeacherName": "Migdow", "RoomNumber": 414},
        "2": {"Period": "Per 2", "Name": "CWI", "TeacherName": "Block", "RoomNumber": 701},
        "3": {"Period": "Per 3", "Name": "AP Comp", "TeacherName": "Newton", "RoomNumber": 106},
        "4": {"Period": "Per 4", "Name": "Chemistry", "TeacherName": "Scott", "RoomNumber": 116},
        "5": {"Period": "Per 5", "Name": "Total Fitness", "TeacherName": "Kittle", "RoomNumber": 999},
        "6": {"Period": "Per 6", "Name": "APCS", "TeacherName": "Nguyen", "RoomNumber": 806},
        "7": {"Period": "Per 7", "Name": "Spanish 1", "TeacherName": "Camarillo", "RoomNumber": 607},
        "Settings": {"NextUp": 1}
    }
}
     */


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