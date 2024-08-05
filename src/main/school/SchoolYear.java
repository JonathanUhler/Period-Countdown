package school;


import jnet.Log;
import util.Interval;
import util.UTCTime;
import util.Duration;
import util.OSPath;
import user.UserJson;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


/**
 * Represents all the information in the school json file in a more accessible format.
 *
 * @author Jonathan Uhler
 */
public class SchoolYear {

    /** Whether the information in this class has been initialized. */
    private boolean isInitialized;
    /** The json information. */
    private SchoolJson schoolJson;
    /** All periods in the year. */
    private List<SchoolPeriod> year;

    /** First period number possible. */
    private int firstPeriod;
    /** Last period number possible. */
    private int lastPeriod;
    /** First date of the school year. */
    private String firstDayTag;
    /** Last date of the school year. */
    private String lastDayTag;
    /** The unix TZ identifier for the school's location. */
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
     * @throws IllegalArgumentException  if any parse error occurs.
     */
    public SchoolYear(Path path) throws FileNotFoundException {
        // Init instance variables
        this.year = new ArrayList<>();

        InputStreamReader schoolReader;
        if (OSPath.isInJar(path)) {
            // Read the json file as a jar resource stream
            InputStream schoolStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path.toString());
            if (schoolStream == null)
                throw new FileNotFoundException(Log.format(Log.ERROR, "SchoolYear",
                                                           "resource \"" + path + "\" is null"));
            schoolReader = new InputStreamReader(schoolStream);
        }
        else {
            if (path.toString().length() == 0)
                throw new FileNotFoundException("no school data file set. " +
                                                "select a file in Settings > School Information");
            schoolReader = new InputStreamReader(new FileInputStream(path.toString()));
        }

        // Load json file with GSON as a SchoolJson object
        Gson gson = new Gson();
        try {
            this.schoolJson = gson.fromJson(schoolReader, SchoolJson.class);
        }
        catch (JsonSyntaxException e) {
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                          "json cannot be parsed: " + e));
        }

        // If no school file is defined, this as the most that can be set up.
        if (this.schoolJson == null) {
            this.isInitialized = false;
            return;
        }

        // Initialize class information
        this.initInfo();
        this.initYear();
        this.isInitialized = true;
    }


    /**
     * Initializes this class with the "Info" section of the school json file. Some basic checks
     * are performed on this data.
     *
     * @throws IllegalArgumentException  if any required key is missing.
     * @throws IllegalArgumentException  if the first or last period is not an integer.
     * @throws IllegalArgumentException  if {@code FirstPeriod < LastPeriod}.
     */
    private void initInfo() {
        if (!this.schoolJson.info.containsKey(SchoolJson.FIRST_PERIOD))
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "missing " +
                                                          SchoolJson.FIRST_PERIOD + " in Info"));
        if (!this.schoolJson.info.containsKey(SchoolJson.LAST_PERIOD))
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "missing " +
                                                          SchoolJson.LAST_PERIOD + " in Info"));
        if (!this.schoolJson.info.containsKey(SchoolJson.FIRST_DAY_TAG))
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "missing " +
                                                          SchoolJson.FIRST_DAY_TAG + " in Info"));
        if (!this.schoolJson.info.containsKey(SchoolJson.LAST_DAY_TAG))
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "missing " +
                                                          SchoolJson.LAST_DAY_TAG + " in Info"));
        if (!this.schoolJson.info.containsKey(SchoolJson.TIMEZONE))
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "missing " +
                                                          SchoolJson.TIMEZONE + " in Info"));

        try {
            this.firstPeriod = Integer.parseInt(this.schoolJson.info.get(SchoolJson.FIRST_PERIOD));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                          "first period is not an integer"));
        }

        try {
            this.lastPeriod = Integer.parseInt(this.schoolJson.info.get(SchoolJson.LAST_PERIOD));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                          "last period is not an integer"));
        }
		
        if (this.firstPeriod > this.lastPeriod) {
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                          "first period > last period"));
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
        if (this.schoolJson.days == null)
            throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear", "missing Days"));
		
        UTCTime firstDay = UTCTime.of(this.firstDayTag, this.timezone);
        UTCTime lastDay = UTCTime.of(this.lastDayTag, this.timezone);

        UTCTime current = firstDay.shiftedToClosest(UTCTime.SUNDAY);
        UTCTime previousEndTime = null; // Running previous end time, to check continuity
        UTCTime end = lastDay.shiftedToClosest(UTCTime.SATURDAY);

        while (current.isBefore(end)) {
            // Search for a week exception in order to get the correct week structure
            String weekTag = current.getWeekTag();
            String weekType = SchoolJson.DEFAULT;
            for (Map<String, String> exception : this.schoolJson.exceptions) {
                if (!exception.containsKey(SchoolJson.TYPE))
                    throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                                  "week exception missing " +
                                                                  SchoolJson.TYPE));
                if (!exception.containsKey(SchoolJson.WEEK_TAG))
                    throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                                  "week exception missing " +
                                                                  SchoolJson.WEEK_TAG));

                // At this point the exception structure is valid to index
                String exceptionTag = exception.get(SchoolJson.WEEK_TAG);
                // Week tags are preferred to be used for the week exception list, but a day tag
                // within that week can also be used. The tag is converted to be a week tag
                // with this line
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
            if (!this.schoolJson.weeks.containsKey(weekType))
                throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                              "missing defintion for week: " +
                                                              weekType));

            List<String> dayTypes = this.schoolJson.weeks.get(weekType);

            if (dayTypes.size() != Duration.DAYS_PER_WEEK)
                throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                              "week \"" + weekType +
                                                              "\" does not have 7 days"));

            for (String dayType : dayTypes) {
                if (!this.schoolJson.days.containsKey(dayType))
                    throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                                  "missing definition for day: " +
                                                                  dayType));

                // Get the list of period definitions for this day. Then loop through each of the
                // periods and create a SchoolPeriod object to add to the year
                List<Map<String, String>> periodDefs = this.schoolJson.days.get(dayType);

                if (periodDefs.size() == 0)
                    throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                                  "day \"" + dayType +
                                                                  "\" has 0 periods"));

                for (Map<String, String> periodDef : periodDefs) {
                    if (!periodDef.containsKey(SchoolJson.TYPE))
                        throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                                      "period definition missing: "
                                                                      + SchoolJson.TYPE));
                    if (!periodDef.containsKey(SchoolJson.NAME))
                        throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                                      "period definition missing: "
                                                                      + SchoolJson.NAME));
                    if (!periodDef.containsKey(SchoolJson.START))
                        throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                                      "period definition missing: "
                                                                      + SchoolJson.START));
                    if (!periodDef.containsKey(SchoolJson.END))
                        throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                                      "period definition missing: "
                                                                      + SchoolJson.END));

                    String type = periodDef.get(SchoolJson.TYPE);
                    String name = periodDef.get(SchoolJson.NAME);
                    String startStr = periodDef.get(SchoolJson.START);
                    String endStr = periodDef.get(SchoolJson.END);

                    // Convert the "current" time to the timezone specified by the json. Use the
                    // day tag of that local object to create the local strings for the start/end.
                    // Then construct UTCTime objects for the start/end in UTC.
                    UTCTime startTime = UTCTime.of(current.getDayTag() + "T" + startStr + ":00.000",
                                                   this.timezone);
                    UTCTime endTime = null;
                    if (!endStr.equals(UserJson.LAST_TIME)) {
                        endTime = UTCTime.of(current.getDayTag() + "T" + endStr + ":00.999",
                                             this.timezone);
                        endTime = endTime.plus(-1, UTCTime.SECONDS);
                    }
                    else {
                        endTime = UTCTime.of(current.plus(1, UTCTime.DAYS).getDayTag() +
                                             "T00:00:00.000", this.timezone);
                        endTime = endTime.plus(-1, UTCTime.MILLISECONDS);
                    }

                    // Add 1 ms to shift from **:**:59.999 to **:**:00.000, which should be the
                    // start time of the current period. If that shift from the previous end time
                    // does not yield the current period there is a discontinuity of >1 ms which
                    // is illegal.
                    if (previousEndTime != null &&
                        !(previousEndTime.plus(1, UTCTime.MILLISECONDS)).isEqual(startTime))
                        throw new IllegalArgumentException(Log.format(Log.ERROR, "SchoolYear",
                                                                      "previous end + 1ms != next start: " +
                                                                      previousEndTime + ", " + startTime +
                                                                      " for dayType=" + dayType));
                    previousEndTime = endTime;

                    SchoolPeriod addition = new SchoolPeriod(type, name,
                                                             startTime, endTime,
                                                             endStr.equals(UserJson.LAST_TIME));
                    this.year.add(addition);
                }

                // Go to the next day
                current = current.plus(1, UTCTime.DAYS);
            }
        }
    }


    /**
     * Binary search implementation to find the period that occupies the target time.
     *
     * @param now  the time to find a period for.
     * @param min  the minimum index to search in the year list.
     * @param max  the maximum index to search in the year list.
     *
     * @return the index of the period such that {@code start <= now <= end} in the year list,
     *         or {@code -1} is no such period exists.
     */
    private int periodSearch(UTCTime now, int min, int max) {
        if (!this.isInitialized)
            return -1;
		
        if (min > max)
            return -1;
		
        int middle = (min + max) / 2;
        SchoolPeriod period = this.year.get(middle);
        UTCTime start = period.getStart();
        UTCTime end = period.getEnd();
		
        if (start.compareTo(now) <= 0 && now.compareTo(end) <= 0) // period is during now
            return middle;
        else if (start.compareTo(now) > 0) // period is after now
            return this.periodSearch(now, min, middle - 1);
        else
            return this.periodSearch(now, middle + 1, max); // period is before now
    }


    /**
     * Gets a period from the year structure at a given time such that {@code start <= now <= end}.
     *
     * @param now  the time to get a period for.
     *
     * @return the period if one exists. If {@code now == null}, then {@code null} is returned.
     */
    public SchoolPeriod getPeriod(UTCTime now) {
        if (!this.isInitialized)
            return null;
        if (now == null)
            return null;
		
        int index = this.periodSearch(now, 0, this.year.size() - 1);
        if (index == -1 || index >= this.year.size())
            return null;
        else
            return this.year.get(index);
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
     * Returns a string representation of this {@code SchoolYear}.
     *
     * @return a string representation of this {@code SchoolYear}.
     */
    @Override
    public String toString() {
        String str = "";

        for (SchoolPeriod period : this.year) {
            str += period + "\n";

            if (period.isLast())
                str += "\n";
        }

        return str;
    }

}
