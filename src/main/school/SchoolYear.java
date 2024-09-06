package school;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import time.Interval;
import time.UTCTime;
import time.Duration;
import os.OSPath;
import user.UserJson;


/**
 * Represents all the information in the school json file in a more accessible format.
 *
 * @author Jonathan Uhler
 */
public class SchoolYear {

    private SchoolJson schoolJson;
    private List<SchoolPeriod> year;
    private Map<String, SchoolPeriod> periodTypes;
    private int firstPeriod;
    private int lastPeriod;
    private String firstDayTag;
    private String lastDayTag;
    private String timezone;
    
    
    /**
     * Constructs a new {@code SchoolYear} object.
     *
     * @param path  a {@code Path} object that points to the school json file. If the path
     *              starts with {@code OSPath.getSchoolJsonJarPath}, the path is assumed to
     *              reference a json file packaged with the Period Countdown jar file, otherwise
     *              it is assumed to be a path on the disk (absolute paths are preferred for
     *              disk operations).
     *
     * @throws FileNotFoundException     if the json file does not exist.
     * @throws IllegalArgumentException  if any parse error occurs from the provided path.
     */
    public SchoolYear(Path path) throws FileNotFoundException {
        this.year = new ArrayList<>();
        this.periodTypes = new HashMap<>();

        // Load school json file
        InputStreamReader schoolReader;
        if (OSPath.isInJar(path)) {
            InputStream schoolStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path.toString());
            if (schoolStream == null) {
                throw new FileNotFoundException("jar resource \"" + path + "\" is null");
            }
            schoolReader = new InputStreamReader(schoolStream);
        }
        else {
            if (!Files.exists(path)) {
                throw new FileNotFoundException("no school data file set");
            }
            schoolReader = new InputStreamReader(new FileInputStream(path.toString()));
        }
        
        // Parse school json file
        Gson gson = new Gson();
        try {
            this.schoolJson = gson.fromJson(schoolReader, SchoolJson.class);
        }
        catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("json cannot be parsed: " + e);
        }
        if (this.schoolJson == null) {
            throw new IllegalArgumentException("school data file is empty");
        }
        
        // Initialize class information
        this.initInfo();
        this.initYear();
    }


    /**
     * Constructs a new {@code SchoolYear} object from a {@code SchoolJson} object.
     *
     * @param json  the {@code SchoolJson} object containing school data.
     *
     * @throws IllegalArgumentException  if any parse error occurs from the provided json data.
     */
    public SchoolYear(SchoolJson json) {
        this.year = new ArrayList<>();
        this.periodTypes = new HashMap<>();
        this.schoolJson = json;
        this.initInfo();
        this.initYear();
    }
    
    
    /**
     * Initializes this class with the "Info" section of the school json file. Some basic checks
     * are performed on this data.
     *
     * @throws IllegalArgumentException  if any required key is missing.
     * @throws IllegalArgumentException  if the first or last period is not an integer.
     * @throws IllegalArgumentException  if FirstPeriod is greater than LastPeriod.
     */
    private void initInfo() {
        if (this.schoolJson.info == null) {
            throw new IllegalArgumentException("missing Info field in school json file");
        }
        if (!this.schoolJson.info.containsKey(SchoolJson.FIRST_PERIOD)) {
            throw new IllegalArgumentException("missing " + SchoolJson.FIRST_PERIOD + " in Info");
        }
        if (!this.schoolJson.info.containsKey(SchoolJson.LAST_PERIOD)) {
            throw new IllegalArgumentException("missing " + SchoolJson.LAST_PERIOD + " in Info");
        }
        if (!this.schoolJson.info.containsKey(SchoolJson.FIRST_DAY_TAG)) {
            throw new IllegalArgumentException("missing " + SchoolJson.FIRST_DAY_TAG + " in Info");
        }
        if (!this.schoolJson.info.containsKey(SchoolJson.LAST_DAY_TAG)) {
            throw new IllegalArgumentException("missing " + SchoolJson.LAST_DAY_TAG + " in Info");
        }
        if (!this.schoolJson.info.containsKey(SchoolJson.TIMEZONE)) {
            throw new IllegalArgumentException("missing " + SchoolJson.TIMEZONE + " in Info");
        }
        
        try {
            this.firstPeriod = Integer.parseInt(this.schoolJson.info.get(SchoolJson.FIRST_PERIOD));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("first period is not an integer");
        }
        
        try {
            this.lastPeriod = Integer.parseInt(this.schoolJson.info.get(SchoolJson.LAST_PERIOD));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("last period is not an integer");
        }
	
        if (this.firstPeriod > this.lastPeriod) {
            throw new IllegalArgumentException("first period > last period");
        }
	
        this.firstDayTag = this.schoolJson.info.get(SchoolJson.FIRST_DAY_TAG);
        this.lastDayTag = this.schoolJson.info.get(SchoolJson.LAST_DAY_TAG);
        this.timezone = this.schoolJson.info.get(SchoolJson.TIMEZONE);
    }

    
    /**
     * Initializes all the periods of the year.
     *
     * @throws IllegalArgumentException  if any check fails.
     */
    private void initYear() {
        if (this.schoolJson.days == null) {
            throw new IllegalArgumentException("missing Days field in school json file");
        }
        if (this.schoolJson.weeks == null) {
            throw new IllegalArgumentException("missing Weeks field in school json file");
        }
        if (this.schoolJson.exceptions == null) {
            throw new IllegalArgumentException("missing Exceptions field in school json file");
        }
	
        UTCTime firstDay = UTCTime.of(this.firstDayTag, this.timezone);
        UTCTime lastDay = UTCTime.of(this.lastDayTag, this.timezone);
        
        UTCTime current = firstDay.shiftedToPrevious(UTCTime.SUNDAY);
        UTCTime previousEndTime = null;
        UTCTime end = lastDay.shiftedToNext(UTCTime.SATURDAY);
        
        while (current.isBefore(end)) {
            // Determine the type of week for the week occupied by `current`. Start by assuming
            // a DEFAULT week, and search for any week exception that overwrites that. This search
            // will also validate the syntax and semantics of the week exceptions.
            String weekTag = current.getWeekTag();
            String weekType = SchoolJson.DEFAULT;
            for (Map<String, String> exception : this.schoolJson.exceptions) {
                if (!exception.containsKey(SchoolJson.TYPE)) {
                    throw new IllegalArgumentException("exception missing " + SchoolJson.TYPE);
                }
                if (!exception.containsKey(SchoolJson.WEEK_TAG)) {
                    throw new IllegalArgumentException("exception missing " + SchoolJson.WEEK_TAG);
                }
                
                // At this point the exception structure is valid to index. Get the date tag of
                // the exception being analyzed and determine if its week tag aligns with the week
                // tag of the `current` time. If so, we have found the exception for this week.
                String exceptionTag = exception.get(SchoolJson.WEEK_TAG);
                String exceptionWeekTag = UTCTime.of(exceptionTag, this.timezone).getWeekTag();
                if (weekTag.equals(exceptionWeekTag)) {
                    weekType = exception.get(SchoolJson.TYPE);
                    break;
                }
            }
            
            // At this point, we have either "DEFAULT" or the name of a special week type in the
            // weekType variables. We want to search for that week type, then go through each of
            // the day types in that week, and finally each of the period definitions for those
            // days. This should create all the periods for a given week
            if (!this.schoolJson.weeks.containsKey(weekType)) {
                throw new IllegalArgumentException("missing defintion for week: " + weekType);
            }
            
            List<String> dayTypes = this.schoolJson.weeks.get(weekType);
            if (dayTypes.size() != Duration.DAYS_PER_WEEK) {
                throw new IllegalArgumentException("week '" + weekType + "' does not have 7 days");
            }
            
            for (String dayType : dayTypes) {
                if (!this.schoolJson.days.containsKey(dayType)) {
                    throw new IllegalArgumentException("missing definition for day: " + dayType);
                }
                
                // Get the list of period definitions for this day. Then loop through each of the
                // periods and create a SchoolPeriod object to add to the year
                List<Map<String, String>> periodDefs = this.schoolJson.days.get(dayType);
                if (periodDefs.size() == 0) {
                    throw new IllegalArgumentException("day '" + dayType + "' has 0 periods");
                }
                
                for (Map<String, String> periodDef : periodDefs) {
                    if (!periodDef.containsKey(SchoolJson.TYPE)) {
                        throw new IllegalArgumentException("period missing " + SchoolJson.TYPE);
                    }
                    if (!periodDef.containsKey(SchoolJson.NAME)) {
                        throw new IllegalArgumentException("period missing " + SchoolJson.NAME);
                    }
                    if (!periodDef.containsKey(SchoolJson.START)) {
                        throw new IllegalArgumentException("period missing " + SchoolJson.START);
                    }
                    if (!periodDef.containsKey(SchoolJson.END)) {
                        throw new IllegalArgumentException("period missing " + SchoolJson.END);
                    }
                    
                    String type = periodDef.get(SchoolJson.TYPE);
                    String name = periodDef.get(SchoolJson.NAME);
                    String startStr = periodDef.get(SchoolJson.START);
                    String endStr = periodDef.get(SchoolJson.END);

                    try {
                        int academicType = Integer.parseInt(type);
                        if (academicType < this.firstPeriod || academicType > this.lastPeriod) {
                            throw new IllegalArgumentException("type " + type + " is out of range");
                        }
                    }
                    catch (NumberFormatException e) { }
                    
                    // Convert the `current` time to the timezone specified by the json. Use the
                    // day tag of that local object to create the local strings for the start/end.
                    // Then construct UTCTime objects for the start/end in UTC.
                    String startTimestamp = current.getDayTag() + "T" + startStr + ":00.000";
                    UTCTime startTime = UTCTime.of(startTimestamp, this.timezone);
                    UTCTime endTime = null;
                    if (!endStr.equals(UserJson.LAST_TIME)) {
                        String endTimestamp = current.getDayTag() + "T" + endStr + ":00.999";
                        endTime = UTCTime.of(endTimestamp, this.timezone);
                        endTime = endTime.plus(-1, UTCTime.SECONDS);
                    }
                    else {
                        String endTimestamp = current.plus(1, UTCTime.DAYS).getDayTag();
                        endTime = UTCTime.of(endTimestamp, this.timezone);
                        endTime = endTime.plus(-1, UTCTime.MILLISECONDS);
                    }
                    
                    // Add 1 ms to shift from **:**:59.999 to **:**:00.000, which should be the
                    // start time of the current period. If that shift from the previous end time
                    // does not yield the current period there is a discontinuity of >1 ms which
                    // is illegal.
                    if (previousEndTime != null &&
                        !previousEndTime.plus(1, UTCTime.MILLISECONDS).equals(startTime))
                    {
                        throw new IllegalArgumentException("previous end + 1ms != next start: " +
                                                           previousEndTime + ", " + startTime +
                                                           " for dayType=" + dayType);
                    }
                    previousEndTime = endTime;
                    
                    SchoolPeriod addition = new SchoolPeriod(type, name,
                                                             startTime, endTime,
                                                             endStr.equals(UserJson.LAST_TIME));
                    this.year.add(addition);
                    this.periodTypes.put(type, addition);
                }
                
                // Go to the next day
                current = current.plus(1, UTCTime.DAYS);
            }
        }
    }
    
    
    /**
     * Binary search implementation to find the period index that occupies the target time.
     *
     * If a period at the provided time does not exist, -1 is returned.
     *
     * @param time  the time to find a period for.
     * @param min   the minimum index to search in the year list.
     * @param max   the maximum index to search in the year list.
     *
     * @return the index of the period such that {@code start <= time <= end} in the year list.
     */
    private int periodSearch(UTCTime time, int min, int max) {
        if (min > max) {
            return -1;
        }
	
        int middle = (min + max) / 2;
        SchoolPeriod period = this.year.get(middle);
        UTCTime start = period.getStart();
        UTCTime end = period.getEnd();
	
        if (start.compareTo(time) <= 0 && time.compareTo(end) <= 0) {
            return middle;
        }
        else if (start.compareTo(time) > 0) {
            return this.periodSearch(time, min, middle - 1);
        }
        else {
            return this.periodSearch(time, middle + 1, max);
        }
    }
    
    
    /**
     * Gets a period from the year structure at a given time such that {@code start <= time <= end}.
     *
     * If no such period exists, {@code null} is returned.
     *
     * @param time  the time to get a period for.
     *
     * @return the period which occurs during the provided time.
     *
     * @throws NullPointerException  if {@code time} is null.
     */
    public SchoolPeriod getPeriod(UTCTime time) {
        if (time == null) {
            throw new NullPointerException("time cannot be null");
        }
	
        int index = this.periodSearch(time, 0, this.year.size() - 1);
        if (index == -1 || index >= this.year.size()) {
            return null;
        }
        else {
            return this.year.get(index);
        }
    }
    
    
    /**
     * Returns the unix TZ identifier for the school.
     *
     * @return the unix TZ identifier for the school.
     */
    public String getTimezone() {
        return this.timezone;
    }
    
    
    /**
     * Returns the first period number possible.
     *
     * @return the first period number possible.
     */
    public int getFirstPeriod() {
        return this.firstPeriod;
    }
    
    
    /**
     * Returns the last period number possible.
     *
     * @return the last period number possible.
     */
    public int getLastPeriod() {
        return this.lastPeriod;
    }


    /**
     * Returns the {@code SchoolPeriod} object with the specified {@code Type} string.
     *
     * If no such period exists, {@code null} is returned. The time-based information of the
     * returned period (e.g. the start and end times, whether it's the last period in the day)
     * are not guaranteed. Only the type and status (name) fields will be consistent.
     *
     * @param type  the type string of the period to find.
     *
     * @return a {@code SchoolPeriod} object with the specified {@code Type} string.
     */
    public SchoolPeriod getPeriodByType(String type) {
        return this.periodTypes.get(type);
    }
    
    
    /**
     * Returns a string representation of this {@code SchoolYear}.
     *
     * @return a string representation of this {@code SchoolYear}.
     */
    @Override
    public String toString() {
        String str = "";
        
        for (SchoolPeriod period : this.year) {
            str += period + "\n";
            if (period.isLast()) {
                str += "\n";
            }
        }
        
        return str;
    }
    
}
