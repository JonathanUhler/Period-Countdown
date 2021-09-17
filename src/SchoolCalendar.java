// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// SchoolCalendar.java
// Period-Countdown
//
// Created by Jonathan Uhler on 8/21/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import java.util.*;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class Calendar
//
// Defines the structure for the calendar operations and data structure
//
public class SchoolCalendar {

    // Terms used to index for specific information
    public static final String getPeriodTerm = "Period";
    public static final String getNameTerm = "Name";
    public static final String getRoomNumberTerm = "RoomNumber";
    public static final String getTeacherNameTerm = "TeacherName";
    public static final String getStartTimeTerm = "StartTime";
    public static final String getEndTimeTerm = "EndTime";
    public static final String getCommentTerm = "Comment";
    public final static String getAdjustTerm = "Adjust";
    public static final String getWeekTagTerm = "WeekTag";
    public static final String getWeeksTerm = "Weeks";
    public static final String getDaysTerm = "Days";
    public static final String getInfoTerm = "Info";
    public static final String getUserTerm = "User";
    public static final String getSettingsTerm = "Settings";
    public static final String getNextUpTerm = "NextUp";

    public static final String getFirstPeriod = "FirstPeriod";
    public static final String getLastPeriod = "LastPeriod";
    public static final String getFirstDate = "FirstDate";
    public static final String getLastDate = "LastDate";
    public static final String get_DEFAULT_WEEK = "DEFAULT";

    public static final String getIndexTerm = "Index";
    public static final String getHoursTerm = "Hours";
    public static final String getMinutesTerm = "Minutes";
    public static final String getSecondsTerm = "Seconds";
    public static final String getMillisecondsTerm = "Milliseconds";

    // Constant date format
    public static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

    // Constant list of day names for the week
    public static final String[] dayIndexToName = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    // Constant unit equalities for time
    public static final int weeksPerYear = 52;
    public static final int daysPerWeek = 7;
    public static final int hoursPerDay = 24;
    public static final int minutesPerHour = 60;
    public static final int secondsPerMinute = 60;
    public static final int millisecondsPerSecond = 1000;
    public static final int msPerSecond = 1000;
    public static final int msPerMinute = msPerSecond * 60;
    public static final int msPerHour = msPerMinute * 60;
    public static final int msPerDay = msPerHour * 24;

    private static int firstPeriod = 1; // Constant for the first possible period, updated later based on Info.json
    private static int lastPeriod = 7; // Constant for the last possible period, updated later based on Info.json
    public static final int fakePeriod = -1; // Constant for the special case of "fake" periods such as passing periods
    public static final int freePeriod = -2; // Constant for the special case of free periods that are important such as lunch, tutorial, and brunch

    private final String firstStringDate; // The date string in format yyyy-mm-dd of the first day of school
    private final String lastStringDate; // The date string in format yyyy-mm-dd of the last day of school
    private final Calendar firstEpochDate = Calendar.getInstance(); // The date object based on firstStringDate
    private final Calendar lastEpochDate = Calendar.getInstance(); // The date object based on lastStringDate
    private final Calendar firstWeekendDate; // The date object for the closest Sunday before firstStringDate
    private final Calendar lastWeekendDate; // The date object for the closest Saturday after lastStringDate

    public final SchoolYear schoolYear; // The global instance of the SchoolYear definitions for this class
    private final ArrayList<SchoolClass> schoolClassArray = new ArrayList<>(); // An array of each type of class
    private final HashMap<String, ArrayList<SchoolPeriod>> schoolPeriodArray = new HashMap<>(); // An array of each type of period

    private final ArrayList<String> dayTagArray; // An array of day tag strings in format yyyy-mm-dd
    private final HashMap<String, SchoolDay> schoolDayHashMap; // A hashmap with keys as day tags and values as the SchoolDay object for that day
    private ArrayList<String> weekTagArray; // An array of week tag strings in format yyyy-mm-dd
    private HashMap<String, SchoolWeek> schoolWeekHashMap; // A hashmap with keys as week tags and values as the SchoolWeek object for that week


    // ====================================================================================================
    // private void instantiateClassObjects
    //
    // Initializes all the SchoolClass objects in schoolClassArray
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void instantiateClassObjects() throws Exception {
        // Add null to schoolClassArray to sets its length as one, this is a bit of a hacky solution
        this.schoolClassArray.add(null);

        // Loop through each of the periods in the day
        for (int p = SchoolCalendar.firstPeriod; p <= SchoolCalendar.lastPeriod; p++) {
            // Get the basic information for the period from the JSON data
            int period = (int) (double) this.schoolYear.getPeriodPatternByNumber(p).get(SchoolCalendar.getPeriodTerm);
            String name = (String) this.schoolYear.getUserDataByPeriod(p).get(SchoolCalendar.getNameTerm);
            String room = (String) this.schoolYear.getUserDataByPeriod(p).get(SchoolCalendar.getRoomNumberTerm);
            String teacher = (String) this.schoolYear.getUserDataByPeriod(p).get(SchoolCalendar.getTeacherNameTerm);

            // Check that all the JSON data is valid
            CalendarHelper.calendarAssert((period == p) &&
                    (name != null) &&
                    (teacher != null),
                    "SchoolCalendar.instantiateClassObjects called and found an error with JSON data",
                    Integer.toString(period), name, room, teacher);

            // Create and add a new instance of SchoolClass with the information to the schoolClassArray
            SchoolClass schoolClass = new SchoolClass(period, name, room, teacher);
            this.schoolClassArray.add(p, schoolClass);
        }
    }
    // end: private void instantiateClassObjects


    // ====================================================================================================
    // private void instantiatePeriodObjects
    //
    // Initializes all the SchoolPeriod objects in schoolPeriodArray
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    private void instantiatePeriodObjects() throws Exception {
        // Get a list of the types of days that have been defined in the JSON data
        ArrayList<String> dayTypes = this.schoolYear.getDayTypes();

        // Loop through each of the types of day
        for (String dayType : dayTypes) {
            // Initialize each key/value pair in the hashmap with the dayType and an empty array for the periods
            this.schoolPeriodArray.put(dayType, new ArrayList<>());

            // Get the number of periods in the day
            int periodCount = this.schoolYear.getDayPattern(dayType).size();

            // Loop through each of the periods in the day
            for (int p = 0; p < periodCount; p++) {
                // Get all the information for the peirod from the JSON data
                int period = (int) (double) this.schoolYear.getPeriodPatternByIndex(dayType, p).get(SchoolCalendar.getPeriodTerm);
                String name = (String) this.schoolYear.getPeriodPatternByIndex(dayType, p).get(SchoolCalendar.getNameTerm);
                String startTime = (String) this.schoolYear.getPeriodPatternByIndex(dayType, p).get(SchoolCalendar.getStartTimeTerm);
                String endTime = (String) this.schoolYear.getPeriodPatternByIndex(dayType, p).get(SchoolCalendar.getEndTimeTerm);
                boolean eodAdjust = (boolean) this.schoolYear.getPeriodPatternByIndex(dayType, p).get(SchoolCalendar.getAdjustTerm);
                String comment = (String) this.schoolYear.getPeriodPatternByIndex(dayType, p).get(SchoolCalendar.getCommentTerm);
                SchoolClass classInfo = (period >= SchoolCalendar.firstPeriod && period <= SchoolCalendar.lastPeriod) ? this.schoolClassArray.get(period) : null;

                // Check that the JSON data is all valid
                CalendarHelper.calendarAssert((name != null) &&
                        (startTime != null) &&
                        (endTime != null) &&
                        (comment != null),
                        "SchoolCalendar.instantiatePeriodObjects called and found an error with JSON data",
                        Integer.toString(period), name, startTime, endTime, Boolean.toString(eodAdjust), comment);

                // Create and add a new SchoolPeriod object with the JSON information
                this.schoolPeriodArray.get(dayType).add(new SchoolPeriod(period, name, startTime, endTime, eodAdjust, classInfo, comment));
            }
        }
    }
    // end: private void instantiatePeriodObjects


    // ----------------------------------------------------------------------------------------------------
    // public SchoolCalendar
    //
    // Constructor for SchoolCalendar class
    //
    // Arguments--
    //
    // schoolStructureFile: the file with the entire structure for the school year
    //
    // userFile:            the file containing user specific data
    //
    public SchoolCalendar(String schoolStructureFile, String userFile) throws Exception {
        // Initialize the class instance of the SchoolYear object
        schoolYear = new SchoolYear(schoolStructureFile, userFile);

        // From the schoolYear data, set the first and last period of the year
        firstPeriod = this.schoolYear.getFirstPeriod();
        lastPeriod = this.schoolYear.getLastPeriod();

        // From the schoolYear data, set the first and last date of the school year
        this.firstStringDate = this.schoolYear.getFirstDate();
        this.lastStringDate = this.schoolYear.getLastDate();

        // Use the first and last date of the year to get the first and last date as Date objects
        this.firstEpochDate.setTime(CalendarHelper.midnightOfDate(this.firstStringDate));
        this.lastEpochDate.setTime(CalendarHelper.midnightOfDate(this.lastStringDate));

        // Use the first and last date to get the first Sunday and last Saturday of the school year
        // These variables define the bounds of the entire school year
        this.firstWeekendDate = this.getFirstDayOfWeek(this.firstEpochDate);
        this.lastWeekendDate = this.getLastDayOfWeek(this.lastEpochDate);

        // Instantiate the class and period objects
        this.instantiateClassObjects();
        this.instantiatePeriodObjects();

        // Initialize day and week information
        this.dayTagArray = new ArrayList<>();
        this.schoolDayHashMap = new HashMap<>();
        int dayIndex = 0;
        this.weekTagArray = new ArrayList<>();
        this.schoolWeekHashMap = new HashMap<>();
        int weekIndex = 0;

        Calendar epochCalendar; // Create a new Calendar instance
        epochCalendar = (Calendar) this.firstWeekendDate.clone(); // Set the time of the calendar to the first Sunday of the year

        // Loop through each millisecond of the school year
        while (epochCalendar.getTime().getTime() <= this.lastWeekendDate.getTime().getTime()) {
            String weekTag = this.getDayTag(epochCalendar); // Get the week tag for the week being parsed
            this.weekTagArray.add(weekTag); // Add the week tag to the week tag array

            SchoolWeek schoolWeek = new SchoolWeek(weekTag, weekIndex); // Create a week object with the tag and index
            this.schoolWeekHashMap.put(weekTag, schoolWeek); // Put the week object into the hashmap with its week tag

            ArrayList<SchoolDay> schoolDayArray = new ArrayList<>(); // Create an array of school day object to use later
            // Loop through each day of the week for each of the weeks in the year
            for (int d = 0; d < SchoolCalendar.daysPerWeek; d++) {
                String dayTag = this.getDayTag(epochCalendar); // Get the day tag for the day

                String dayType = this.schoolYear.getDayType(weekTag, d); // Get the type of the day defined in the json data
                ArrayList<SchoolPeriod> periodsForDay = this.schoolPeriodArray.get(dayType); // Store the list of periods in the day

                SchoolDay day = new SchoolDay(dayTag, dayIndex, weekTag, dayType, periodsForDay); // Create a new day object
                schoolDayArray.add(day); // Add the day to the array of school days
                this.dayTagArray.add(dayTag); // Add the day tag
                this.schoolDayHashMap.put(dayTag, day); // Add the day and its tag to the day hashmap

                dayIndex++; // Update the day index
                this.advanceToFutureDay(epochCalendar, 1); // Advance the epoch date

                // Check that the dayIndex never exceeds the number of days per week
                CalendarHelper.calendarAssert((dayIndex <= 365),
                        "SchoolCalendar.SchoolCalendar dayIndex runaway",
                        Integer.toString(dayIndex));
            }

            weekIndex++; // Update the week index
            schoolWeek.setDayArray(schoolDayArray);
            // Check that the weekIndex never exceeds the number of weeks per year
            CalendarHelper.calendarAssert((weekIndex <= SchoolCalendar.weeksPerYear - 1),
                    "SchoolCalendar.SchoolCalendar weekIndex runaway",
                    Integer.toString(weekIndex));
        }
    }
    // end: public SchoolCalendar


    // ====================================================================================================
    // GET methods
    public static int getFirstPeriod() {
        return firstPeriod;
    }

    public static int getLastPeriod() {
        return lastPeriod;
    }

    public Calendar getFirstWeekendDate() {
        return firstWeekendDate;
    }

    public SchoolYear getSchoolYear() {
        return schoolYear;
    }
    // end: GET methods


    // ====================================================================================================
    // public Date getFirstDayOfWeek
    //
    // Returns the closest Sunday to a given date
    //
    // Arguments--
    //
    // epochDate:   the date to get the closest Sunday of
    //
    // Returns--
    //
    // newDate:     the date representing the Sunday closest to epochDate
    //
    public Calendar getFirstDayOfWeek(Calendar epochDate) {
        Calendar newDate = Calendar.getInstance(); // Initialize a new date object to use
        newDate.setTimeInMillis(epochDate.getTimeInMillis());
        newDate.set(Calendar.DATE, epochDate.get(Calendar.DATE) - (epochDate.get(Calendar.DAY_OF_WEEK) - 1)); // Set the day of the new object to the Sunday
        return newDate; // Return the new object
    }
    // end: public Date getFirstDayOfWeek


    // ====================================================================================================
    // public Date getLastDayOfWeek
    //
    // Get the closest Saturday to a given date
    //
    // Arguments--
    //
    // epochDate:   the date to get the end of the week for
    //
    // Returns--
    //
    // newDate:     the date representing the closets Saturday to epochDate
    //
    public Calendar getLastDayOfWeek(Calendar epochDate) {
        int highestDay = 7; // Set the highest number of day that can exist

        Calendar newDate = Calendar.getInstance(); // Initialize a new date object to use
        newDate.setTimeInMillis(epochDate.getTimeInMillis());
        newDate.set(Calendar.DATE, epochDate.get(Calendar.DATE) + (highestDay - (epochDate.get(Calendar.DAY_OF_WEEK) - 1)));
        return  newDate; // Return the new object
    }
    // end: public Date getLastDayOfWeek


    // ====================================================================================================
    // public String getDayTag
    //
    // Get the day tag in format yyyy-mm-dd of a given date
    //
    // Arguments--
    //
    // epochDate:   the date to get the day tag of
    //
    // Returns--
    //
    // The day tag for epochDate
    //
    public String getDayTag(Calendar epochDate) {
        // Form and return the day tag
        return epochDate.get(Calendar.YEAR) + "-" +
                CalendarHelper.padStringLeft(Integer.toString(epochDate.get(Calendar.MONTH) + 1), 2, '0') + "-" +
                CalendarHelper.padStringLeft(Integer.toString(epochDate.get(Calendar.DATE)), 2, '0');
    }
    // end: public String getDayTag


    // ====================================================================================================
    // public void advanceToFutureDay
    //
    // Advances the value of a Date object to a day some number of days in the future
    //
    // Arguments--
    //
    // epochDate:       the date object to advance the day of
    //
    // daysToAdvance:   the number of days to advance
    //
    // Returns--
    //
    // None, the epochDate is pass by reference and is updated without a return
    //
    public void advanceToFutureDay(Calendar epochDate, int daysToAdvance) {
        epochDate.set(Calendar.DATE, epochDate.get(Calendar.DATE) + daysToAdvance);
    }
    // end: public void advanceToFutureDay


    // ====================================================================================================
    // public HashMap<String, Object> getPeriodByDateAndTime
    //
    // Get information about a period given a date and time
    //
    // Arguments--
    //
    // epochDate:   the datetime object to search for a period in
    //
    // Returns--
    //
    // The period that overlaps with epochDate in the format:
    //      SchoolYear.getDaysTerm -> day object that contains the period found
    //      SchoolYear.getPeriodTerm -> period object for the match
    //      SchoolYear.getIndexTerm -> the period index in the day object's period array
    //
    public HashMap<String, Object> getPeriodByDateAndTime(Calendar epochDate) throws Exception {
        Calendar lastMillisecondOfYear = Calendar.getInstance();
        lastMillisecondOfYear.setTime(new Date(this.lastWeekendDate.getTime().getTime())); // Get a date object with the last millisecond of the school year
        this.advanceToFutureDay(lastMillisecondOfYear, 1); // Advance the last ms object by 1 day
        long lastMillisecond = lastMillisecondOfYear.getTime().getTime() - 1; // Get the last ms of the year + 1 day as a long value
        // Check that the epochDate is within the bounds of the school year. If not, return null as no period can be found
        if (epochDate.getTime().getTime() < this.firstWeekendDate.getTime().getTime() || epochDate.getTime().getTime() > lastMillisecond) {
            return null;
        }

        String dayTag = this.getDayTag(epochDate); // Get the day tag for the epochDate
        SchoolDay day = this.schoolDayHashMap.get(dayTag); // get the day object for the epochDate based on its day tag

        // Check that a day object exists for the tag
        CalendarHelper.calendarAssert((day != null),
                "SchoolCalendar.getPeriodByDateAndTime found an error with this.schoolDayHashMap",
                dayTag, String.valueOf(day));

        // Check that the day object contains things
        CalendarHelper.calendarAssert((day.getPeriodList().size() != 0),
                "SchoolCalendar.getPeriodByTimeAndDate found zero periods in day object",
                dayTag, String.valueOf(day));

        // Loop through each of the periods in the day object
        for (int i = 0; i < day.getPeriodList().size(); i++) {
            // From the list of periods in the day object, get the period object for the index in the loop
            SchoolPeriod period = day.getPeriodList().get(i);

            Calendar startTime = Calendar.getInstance();
            startTime.setTime(new Date(day.getEpochCalendar().getTime().getTime())); // Get the start time for the day
            startTime.set(Calendar.HOUR_OF_DAY, period.getStartDayAdjust().get(SchoolCalendar.getHoursTerm)); // Set the hours to the start time of the period
            startTime.set(Calendar.MINUTE, period.getStartDayAdjust().get(SchoolCalendar.getMinutesTerm)); // Set the minutes to the start time of the period
            startTime.set(Calendar.SECOND, period.getStartDayAdjust().get(SchoolCalendar.getSecondsTerm));

            // Do the same for the end time of the period
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(new Date(day.getEpochCalendar().getTime().getTime()));
            endTime.set(Calendar.HOUR_OF_DAY, period.getEndDayAdjust().get(SchoolCalendar.getHoursTerm));
            endTime.set(Calendar.MINUTE, period.getEndDayAdjust().get(SchoolCalendar.getMinutesTerm));
            endTime.set(Calendar.SECOND, period.getEndDayAdjust().get(SchoolCalendar.getSecondsTerm));

            // Check if the time given is in the period
            if (epochDate.getTime().getTime() >= startTime.getTime().getTime() && epochDate.getTime().getTime() <= endTime.getTime().getTime()) {
                int finalI = i;
                // If the period match was found, return the information as a hashmap
                return new HashMap<>() {{
                    put(SchoolCalendar.getDaysTerm, day);
                    put(SchoolCalendar.getPeriodTerm, period);
                    put(SchoolCalendar.getIndexTerm, finalI);
                }};
            }
        }

        // If nothing was found, return null
        return null;
    }
    // end: public HashMap<String, Object> getPeriodByDateAndTime


    // ====================================================================================================
    // private HashMap<String, Object> getNextPeriodThisDay
    //
    // Get the next period in a given day
    //
    // Arguments--
    //
    // lastMatch:                   the last period match, the same data type as the return of getPeriodByDateAndTime
    //
    // matchOnlyRealPeriod:         whether to only match real class periods (ex 1...7, excluding passing, lunch, etc.)
    //
    // matchOnlyPeriodWithClass:    whether to only match periods with classes (excluding free periods even if they are real)
    //
    // Returns--
    //
    // match:                       a hashmap representation of the next period match for the day
    //
    private HashMap<String, Object> getNextPeriodThisDay(HashMap<String, Object> lastMatch, boolean matchOnlyRealPeriod, boolean matchOnlyPeriodWithClass) {
        SchoolDay dayObj = (SchoolDay) lastMatch.get(SchoolCalendar.getDaysTerm); // Get the day object from the last period match
        int periodIndex = (int) lastMatch.get(SchoolCalendar.getIndexTerm); // Get the period index from the last match


        HashMap<String, Object> match = null; // Initialize a new hashmap to add to and return later

        // Loop through each of the periods in the day object
        for (int i = periodIndex; i < dayObj.getPeriodList().size(); i++) {
            SchoolPeriod periodObj = dayObj.getPeriodList().get(i); // Get each period in the day object

            // If only real periods should be matched and the period is not real, then continue
            if ((periodObj.getPeriod() <= SchoolCalendar.getFirstPeriod() && periodObj.getPeriod() >= SchoolCalendar.getLastPeriod()) && matchOnlyRealPeriod) { continue; }
            // If the period should have a class and doesn't, then continue
            if (periodObj.getClassInfo() == null && matchOnlyPeriodWithClass) { continue; }

            // Add the data for the next period match
            match = new HashMap<>() {{
                put(SchoolCalendar.getDaysTerm, dayObj);
                put(SchoolCalendar.getPeriodTerm, periodObj);
                put(SchoolCalendar.getIndexTerm, periodIndex);
            }};
            break;
        }

        // Return the next period match for the day
        return match;
    }
    // end: private HashMap<String, Object> getNextPeriodThisDay


    // ====================================================================================================
    // public HashMap<String, Object> getNextPeriod
    //
    // Overloaded method 1 for getNextPeriod
    //
    // Arguments--
    //
    // lastMatch:   the last period match
    //
    // Returns--
    //
    // Call to overloaded method 2 for getNextPeriod
    //
    public HashMap<String, Object> getNextPeriod(HashMap<String, Object> lastMatch) throws Exception {
        return this.getNextPeriod(lastMatch, false);
    }
    // end: public HashMap<String, Object> getNextPeriod

    // ====================================================================================================
    // public HashMap<String, Object> getNextPeriod
    //
    // Overloaded method 2 for getNextPeriod
    //
    // Arguments--
    //
    // lastMatch:           the last period match
    //
    // matchOnlyRealPeriod: whether to only match real school periods
    //
    // Returns--
    //
    // Call to overloaded method 3 for getNextPeriod
    //
    public HashMap<String, Object> getNextPeriod(HashMap<String, Object> lastMatch, boolean matchOnlyRealPeriod) throws Exception {
        return this.getNextPeriod(lastMatch, matchOnlyRealPeriod, false);
    }
    // end: public HashMap<String, Object> getNextPeriod


    // ====================================================================================================
    // public HashMap<String, Object> getNextPeriod
    //
    // Returns the next period match after a given period
    //
    // Arguments--
    //
    // lastMatch:                   the last period match
    //
    // matchOnlyRealPeriod:         whether to only match real school periods
    //
    // matchOnlyPeriodWithClass:    whether to only match periods with actual classes in them
    //
    // Returns--
    //
    // The next period match
    //
    public HashMap<String, Object> getNextPeriod(HashMap<String, Object> lastMatch, boolean matchOnlyRealPeriod, boolean matchOnlyPeriodWithClass) throws Exception {
        // Check that the lastMatch object has data to use
        CalendarHelper.calendarAssert((lastMatch != null),
                "SchoolCalendar.getNextPeriod called with invalid arguments",
                String.valueOf(lastMatch));
        // Check that all the expected information in lastMatch exists
        CalendarHelper.calendarAssert((lastMatch.get(SchoolCalendar.getDaysTerm) != null) &&
                (lastMatch.get(SchoolCalendar.getPeriodTerm) != null) &&
                (lastMatch.get(SchoolCalendar.getIndexTerm) != null),
                "SchoolCalendar.getNextPeriod called with invalid arguments -- could not find lastMatch data",
                String.valueOf(lastMatch));

        // Initialize a new hashmap for the found match
        HashMap<String, Object> findMatch = new HashMap<>() {{
            put(SchoolCalendar.getDaysTerm, lastMatch.get(SchoolCalendar.getDaysTerm));
            put(SchoolCalendar.getPeriodTerm, lastMatch.get(SchoolCalendar.getPeriodTerm));
            put(SchoolCalendar.getIndexTerm, (int) lastMatch.get(SchoolCalendar.getIndexTerm) + 1); // Set the index to be the next period by default
        }};

        int runawayCounter = this.dayTagArray.size() * 50; // Set a runaway counter to stop the loop if no period match is found
        // Loop for enough time to either find a period match or expose a code logic error
        for (int i = 0; i < runawayCounter; i++) {
            // Attempt to get the match and return it if data is found
            HashMap<String, Object> match = getNextPeriodThisDay(findMatch, matchOnlyRealPeriod, matchOnlyPeriodWithClass);
            if (match != null) { return match; }

            // If the next period match was not found, then the day needs to be advanced
            SchoolDay dObj = (SchoolDay) findMatch.get(SchoolCalendar.getDaysTerm); // Update the school day object
            int nextDayIndex = dObj.getDayArrayIndex() + 1; // Increment the day
            if (nextDayIndex >= this.dayTagArray.size()) { return null; } // If this is the end of the year, return null

            // Update the findMatch data to be used if the day needs to be incremented again later
            SchoolDay dayMatch = this.schoolDayHashMap.get(this.dayTagArray.get(nextDayIndex));
            findMatch = new HashMap<>() {{
                put(SchoolCalendar.getDaysTerm, dayMatch);
                put(SchoolCalendar.getPeriodTerm, null);
                put(SchoolCalendar.getIndexTerm, 0);
            }};
        }

        // If this point was reached, then the runawayCounter ran out and there was some code logic error
        CalendarHelper.calendarAssert(false,
                "SchoolCalendar.getNextPeriod loop runaway after not finding a match");
        return null;
    }
    // end: public HashMap<String, Object> getNextPeriod


    // ====================================================================================================
    // public HashMap<String, Object> calculateTimeLeft
    //
    // Overloaded method 1 for calculateTimeLeft
    //
    // Arguments--
    //
    // startTimeMS: the start time of the portion to calculate
    //
    // endTimeMS:   the end time of the portion to calculate
    //
    // Returns--
    //
    // Call to overloaded method 2 for calculateTimeLeft
    //
    public HashMap<String, Integer> calculateTimeLeft(long startTimeMS, long endTimeMS) {
        return this.calculateTimeLeft(startTimeMS, endTimeMS, false);
    }
    // end: public HashMap<String, Object> calculateTimeLeft


    // ====================================================================================================
    // public HashMap<String, Integer> calculateTimeLeft
    //
    // Calculates and returns the time between two points in time
    //
    // Arguments--
    //
    // startTimeMS:         the start time of the portion to calculate
    //
    // endTimeMS:           the end time of the portion to calculate
    //
    // convertDaysToHours:  whether to convert any possible days into hours * 24
    //
    // Returns--
    //
    // timeLeft:            a hashmap containing the days, hours, minutes, seconds, and ms left
    //
    public HashMap<String, Integer> calculateTimeLeft(long startTimeMS, long endTimeMS, boolean convertDaysToHours) {
        HashMap<String, Integer> timeLeft = new HashMap<>(); // Initialize a new hashmap to store the time left data

        long deltaMilliseconds = endTimeMS - startTimeMS; // Get the difference in ms time between the start and end times

        // Check that there is not a negative time left
        if (deltaMilliseconds >= 0) {
            timeLeft.put(SchoolCalendar.getMillisecondsTerm, (int) deltaMilliseconds); // Put the total ms left into the hashmap

            timeLeft.put(SchoolCalendar.getDaysTerm, (int) Math.floor(deltaMilliseconds / (SchoolCalendar.msPerDay * 1.0))); // Calculate and put the number of days into the map
            deltaMilliseconds -= timeLeft.get(SchoolCalendar.getDaysTerm) * SchoolCalendar.msPerDay; // Subtract the number of days in ms from the total ms value

            timeLeft.put(SchoolCalendar.getHoursTerm, (int) Math.floor(deltaMilliseconds / (SchoolCalendar.msPerHour * 1.0))); // Calculate and put the number of hours into the map
            deltaMilliseconds -= timeLeft.get(SchoolCalendar.getHoursTerm) * SchoolCalendar.msPerHour; // Subtract the number of hours in ms from the total ms value

            timeLeft.put(SchoolCalendar.getMinutesTerm, (int) Math.floor(deltaMilliseconds / (SchoolCalendar.msPerMinute * 1.0))); // Calculate and put the number of minutes into the map
            deltaMilliseconds -= timeLeft.get(SchoolCalendar.getMinutesTerm) * SchoolCalendar.msPerMinute; // Subtract the number of minutes in ms from the total ms value

            timeLeft.put(SchoolCalendar.getSecondsTerm, (int) Math.floor(deltaMilliseconds / (SchoolCalendar.msPerSecond * 1.0))); // Calculate and put the number of seconds into the map

            // Check if days should be converted to hours
            if (convertDaysToHours) {
                timeLeft.put(
                        SchoolCalendar.getHoursTerm, // At the hours value
                        (int) (double) timeLeft.get(SchoolCalendar.getHoursTerm) + // Put the number of existing hours
                                (int) (double) timeLeft.get(SchoolCalendar.getDaysTerm) * SchoolCalendar.hoursPerDay // Plus the number of hours * number of days
                );
                // Set the number of days to 0
                timeLeft.put(SchoolCalendar.getDaysTerm, 0);
            }
        }

        // Return the time left hashmap object
        return timeLeft;
    }
    // end: public HashMap<String, Integer> calculateTimeLeft


    // ====================================================================================================
    // public HashMap<String, Integer> getTimeRemainingInPeriod
    //
    // Get the time remaining in a period
    //
    // Arguments--
    //
    // epochDate:   the date to start the calculation from
    //
    // period:      a period object which can an accessible end time the end the calculation at
    //
    // Returns--
    //
    // A hashmap containing all the units of time remaining in the period
    //
    public HashMap<String, Integer> getTimeRemainingInPeriod(Calendar epochDate, SchoolPeriod period) throws Exception {
        // Check that all the arguments are valid
        CalendarHelper.calendarAssert((epochDate != null) &&
                (period != null),
                "SchoolCalendar.getTimeRemainingInPeriod called with invalid arguments",
                String.valueOf(epochDate), String.valueOf(period));

        Calendar endTime = Calendar.getInstance(); // Create a new calendar instance to serve as the end time
        endTime.setTime(new Date(epochDate.getTime().getTime())); // Set the time to the epoch date time
        endTime.set(Calendar.HOUR_OF_DAY, period.getEndDayAdjust().get(SchoolCalendar.getHoursTerm)); // Set the hours to the period end hours
        endTime.set(Calendar.MINUTE, period.getEndDayAdjust().get(SchoolCalendar.getMinutesTerm)); // Set the minutes to the period end minutes
        endTime.set(Calendar.SECOND, period.getEndDayAdjust().get(SchoolCalendar.getSecondsTerm)); // Set the seconds to the period end seconds
        endTime.setTime(new Date(endTime.getTime().getTime() + 1)); // Add one ms to the time

        // Return the calculated difference in the start and end times
        return this.calculateTimeLeft(epochDate.getTime().getTime(), endTime.getTime().getTime());
    }
    // end: public HashMap<String, Integer> getTimeRemainingInPeriod


    // ====================================================================================================
    // public HashMap<String, Integer> getTimeRemainingUntilPeriod
    //
    // Overloaded method 1 for getTimeRemainingUntilPeriod
    //
    // Arguments--
    //
    // epochDate:   the date to use as a starting time
    //
    // day:         the day object to use
    //
    // period:      the period object that can be found within the day object
    //
    // Returns--
    //
    // A call to overloaded method 2 for getTimeRemainingUntilPeriod
    //
    public HashMap<String, Integer> getTimeRemainingUntilPeriod(Calendar epochDate, SchoolDay day, SchoolPeriod period) throws Exception {
        return this.getTimeRemainingUntilPeriod(epochDate, day, period, false);
    }
    // end: public HashMap<String, Integer> getTimeRemainingUntilPeriod


    // ====================================================================================================
    // public HashMap<String, Integer> getTimeRemainingUntilPeriod
    //
    // Get the time remaining until the next period
    //
    // Arguments--
    //
    // epochDate:           the date to use as a starting time
    //
    // day:                 the day object to use
    //
    // period:              the period object that can be found within the day object
    //
    // convertDaysToHours:  whether to convert any days in the remaining time to hours * 24
    //
    // Returns--
    //
    // A hashmap with the time units for the remaining time
    //
    public HashMap<String, Integer> getTimeRemainingUntilPeriod(Calendar epochDate, SchoolDay day, SchoolPeriod period, boolean convertDaysToHours) throws Exception {
        // Check that all arguments are valid
        CalendarHelper.calendarAssert((epochDate != null) &&
                (day != null) &&
                (period != null),
                "SchoolCalendar.getTimeRemainingUntilPeriod called with invalid arguments",
                String.valueOf(epochDate), String.valueOf(day), String.valueOf(period));

        Calendar periodStart = Calendar.getInstance(); // Create a new calendar object to serve as the start of the next period
        periodStart.setTime(new Date(day.getEpochCalendar().getTime().getTime())); // Set the time to the epoch calendar object of the day object
        periodStart.set(Calendar.HOUR_OF_DAY, period.getStartDayAdjust().get(SchoolCalendar.getHoursTerm)); // Set the hours to the start of the next period
        periodStart.set(Calendar.MINUTE, period.getStartDayAdjust().get(SchoolCalendar.getMinutesTerm)); // Set the minutes to the start of the next period
        periodStart.set(Calendar.SECOND, period.getStartDayAdjust().get(SchoolCalendar.getSecondsTerm)); // Set the seconds to the start of the next period

        // Return the remaining time
        return this.calculateTimeLeft(epochDate.getTime().getTime(), periodStart.getTime().getTime(), convertDaysToHours);
    }
    // end: HashMap<String, Integer> getTimeRemainingUntilPeriod


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
        String classDescription = "\n\tfirstPeriod:\t" + firstPeriod + "\n\tlastPeriod:\t" + lastPeriod +
                "\n\tfirstStringDate:\t" + this.firstStringDate + "\n\tlastStringDate:\t" + this.lastStringDate +
                "\n\tfirstEpochDate:\t" + this.firstEpochDate + "\n\tlastEpochDate:\t" + this.lastEpochDate +
                "\n\tfirstWeekendDate:\t" + this.firstWeekendDate + "\n\tlastWeekendDate:\t" + this.lastWeekendDate;
        return super.toString() + ": " + classDescription;
    }
    // end: public String toString

}
// end: public class Calendar
