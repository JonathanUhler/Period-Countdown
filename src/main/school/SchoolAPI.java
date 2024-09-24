package school;


import java.util.Map;
import java.util.List;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import time.UTCTime;
import time.Duration;


/**
 * Interface for accessing information derived from the school json file. This includes things like
 * time remaining, upcoming periods, and the current period. More data is available from the other
 * classes in this package, but it is avised to use this API instead.
 *
 * @author Jonathan Uhler
 */
public class SchoolAPI {
    
    /**
     * Definition for the school year. This is an object-form of the school json file, making the
     * data more accessible and providing some simple API methods.
     */
    private SchoolYear year;
    
    
    /**
     * Constructs a new {@code SchoolAPI} object with a specified json file name.
     *
     * @param path  a {@code Path} object that points to the school json file. If the path
     *              starts with {@code OSPath.getSchoolJsonJarPath}, the path is assumed to
     *              reference a json file packaged with the Period Countdown jar file, otherwise
     *              it is assumed to be a path on the disk (absolute paths are preferred for
     *              disk operations).
     *
     * @throws FileNotFoundException     if the json file does not exist.
     * @throws IllegalArgumentException  if any json parse error occurs.
     *
     * @see SchoolYear
     */
    public SchoolAPI(Path path) throws FileNotFoundException {
        this.year = new SchoolYear(path);
    }
    
    
    /**
     * Constructs a new {@code SchoolAPI} object from a {@code SchoolJson} object.
     *
     * @param json  the {@code SchoolJson} object containg school data.
     *
     * @throws IllegalArgumentException  if any json parse error occurs.
     *
     * @see SchoolYear
     */
    public SchoolAPI(SchoolJson json) throws FileNotFoundException {
        this.year = new SchoolYear(json);
    }
    
    
    /**
     * Returns the timezone of the loaded school json file as a Unix timezone identifier (e.g. 
     * {@code "America/Los_Angeles"} for much of the west coast of the United States).
     *
     * This method is a wrapper for accessing {@code SchoolYear::getTimezone}.
     *
     * @return the timezone of the loaded school json file as a Unix timezone identifier.
     */
    public String getTimezone() {
        return this.year.getTimezone();
    }
    
    
    /**
     * Returns the first possible period number.
     *
     * This method is a wrapper for accessing {@code SchoolYear::getFirstPeriod}.
     *
     * @return the first possible period number
     */
    public int getFirstPeriod() {
        return this.year.getFirstPeriod();
    }
    
    
    /**
     * Returns the last possible period number.
     *
     * This method is a wrapper for accessing {@code SchoolYear::getLastPeriod}.
     *
     * @return the last possible period number
     */
    public int getLastPeriod() {
        return this.year.getLastPeriod();
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
        return this.year.getPeriodByType(type);
    }
    
    
    /**
     * Returns the {@code SchoolPeriod} object such that {@code start <= time <= end}. Note that
     * the definition of a period runs from the start to the end, inclusive on both sides. This is
     * because the end time is 1 millisecond before the period trasition (e.g. 13:59:59.999 for a
     * period where the bell rings at 14:00).
     *
     * @param time  the time to get the current period for.
     *
     * @return the current period object, if one exists, otherwise {@code null}.
     *
     * @throws NullPointerException  if {@code time} is null.
     */
    public SchoolPeriod getCurrentPeriod(UTCTime time) {
        if (time == null) {
            throw new NullPointerException("time cannot be null");
        }

        return this.year.getPeriod(time);
    }
    
    
    /**
     * Gets the period immediately after the period returned by {@code getCurrentPeriod}. This is
     * achieved by taking the end time of the current period and adding 1 millisecond, thus causing
     * the time to overflow to the start of the next period whose time is at least 1 millisecond.
     *
     * If the current period is {@code null}, then the next calendar year is searched for the first
     * period of the school year. If nothing is found after this search, {@code null} is returned.
     *
     * @param time  the time to get the next period for.
     *
     * @return the next period object, if one exists.
     *
     * @throws NullPointerException  if {@code time} is null.
     *
     * @see getCurrentPeriod
     */
    public SchoolPeriod getNextPeriod(UTCTime time) {
        if (time == null) {
            throw new NullPointerException("time cannot be null");
        }

        SchoolPeriod currentPeriod = this.getCurrentPeriod(time);
        if (currentPeriod != null) {
            UTCTime currentPeriodEnd = currentPeriod.getEnd();
            // Go from AA:BB:59.999 -> CC:DD:00.000. Periods should be defined in the json file to
            // the second precision, and milliseconds are added as either 000 or 999 depending on
            // if the time is the start or the end of the period
            UTCTime nextPeriodStart = currentPeriodEnd.plus(1, UTCTime.MILLISECONDS);
            
            return this.getCurrentPeriod(nextPeriodStart);
        }

        // If the current period is null, then "walk" the time pointer for the next calendar year
        // from the time specified by `time` to search for a period.
        UTCTime walk = time;
        UTCTime end = walk.plus(1, UTCTime.YEARS);
        while (walk.isBefore(end)) {
            SchoolPeriod nextPeriod = this.getCurrentPeriod(walk);
            if (nextPeriod != null) {
                return nextPeriod;
            }
            walk = walk.plus(1, UTCTime.DAYS);
            walk = walk.toMidnight(this.year.getTimezone());
        }
            
        return null;
    }


    /**
     * Gets the period immediately before the period returned by {@code getCurrentPeriod}. This is
     * achieved by taking the start time of the current period and subtracting 1 millisecond, thus
     * causing the time to underflow to the end of the previous period whose time is at least 1
     * millisecond.
     *
     * If the current period is {@code null}, then the previous calendar year is searched for the
     * last period of the school year. If nothing is found after this search, {@code null} is
     * returned.
     *
     * @param time  the time to get the next period for.
     *
     * @return the previous period object, if one exists.
     *
     * @throws NullPointerException  if {@code time} is null.
     *
     * @see getCurrentPeriod
     */
    public SchoolPeriod getPreviousPeriod(UTCTime time) {
        if (time == null) {
            throw new NullPointerException("time cannot be null");
        }

        SchoolPeriod currentPeriod = this.getCurrentPeriod(time);
        if (currentPeriod != null) {
            UTCTime currentPeriodStart = currentPeriod.getStart();
            UTCTime previousPeriodEnd = currentPeriodStart.plus(-1, UTCTime.MILLISECONDS);

            return this.getCurrentPeriod(previousPeriodEnd);
        }

        UTCTime walk = time;
        UTCTime end = walk.plus(-1, UTCTime.YEARS);
        while (walk.isAfter(end)) {
            SchoolPeriod previousPeriod = this.getCurrentPeriod(walk);
            if (previousPeriod != null) {
                return previousPeriod;
            }
            walk = walk.toMidnight(this.year.getTimezone());
            walk = walk.plus(-1, UTCTime.MILLISECONDS);
        }

        return null;
    }
    
    
    /**
     * Returns the period immediately after the period returned by {@code getCurrentPeriod} if the
     * current period is not the last period in the current day.
     *
     * @param time  the time to get the next period in the current day specified by {@code time}.
     *
     * @return the next period in the current day specified by the argument {@code time}. If
     *         the current period is {@code null}, or the current period is the last period in the
     *         day, {@code null} is returned.
     *
     * @throws NullPointerException  if {@code time} is null.
     */
    public SchoolPeriod getNextPeriodToday(UTCTime time) {
        if (time == null) {
            throw new NullPointerException("time cannot be null");
        }

        SchoolPeriod currentPeriod = this.getCurrentPeriod(time);
        if (currentPeriod == null || currentPeriod.isLast()) {
            return null;
        }
        
        UTCTime currentPeriodEnd = currentPeriod.getEnd();
        UTCTime nextPeriodStart = currentPeriodEnd.plus(1, UTCTime.MILLISECONDS);
        return this.getCurrentPeriod(nextPeriodStart);
    }


    /**
     * Gets the period chronologically at or after the period returned by {@code getCurrentPeriod}
     * such that {@code getNextCountedPeriod(...).isCounted()} is true.
     *
     * @param time  the time to get the next counted period for.
     *
     * @return the next counted period object, if one exists. If no such counted period exists,
     *         then {@code null} is returned.
     *
     * @throws NullPointerException  if {@code time} is null.
     *
     * @see getCurrentPeriod
     */
    public SchoolPeriod getNextCountedPeriod(UTCTime time) {
        if (time == null) {
            throw new NullPointerException("time cannot be null");
        }

        SchoolPeriod currentPeriod = this.getCurrentPeriod(time);
        if (currentPeriod != null && currentPeriod.isCounted()) {
            return currentPeriod;
        }
        UTCTime walk = time;
        UTCTime end = time.plus(1, UTCTime.YEARS);
        while (walk.isBefore(end)) {
            SchoolPeriod nextPeriod = this.getNextPeriod(walk);
            if (nextPeriod == null) {
                return null;
            }
            if (nextPeriod.isCounted()) {
                return nextPeriod;
            }
                
            walk = nextPeriod.getEnd();
        }
	
        return null;
    }


    /**
     * Gets the period chronologically at or before the period returned by {@code getCurrentPeriod}
     * such that {@code getPreviousCountedPeriod(...).isCounted()} is true.
     *
     * @param time  the time to get the previous counted period for.
     *
     * @return the previous counted period object, if one exists. If no such counted period exists,
     *         then {@code null} is returned.
     *
     * @throws NullPointerException  if {@code time} is null.
     *
     * @see getCurrentPeriod
     */
    public SchoolPeriod getPreviousCountedPeriod(UTCTime time) {
        if (time == null) {
            throw new NullPointerException("time cannot be null");
        }

        SchoolPeriod currentPeriod = this.getCurrentPeriod(time);
        if (currentPeriod != null && currentPeriod.isCounted()) {
            return currentPeriod;
        }

        UTCTime walk = time;
        UTCTime end = time.plus(-1, UTCTime.YEARS);
        while (walk.isAfter(end)) {
            SchoolPeriod previousPeriod = this.getPreviousPeriod(walk);
            if (previousPeriod == null) {
                return null;
            }
            if (previousPeriod.isCounted()) {
                return previousPeriod;
            }

            walk = previousPeriod.getStart();
        }

        return null;
    }
    
    
    /**
     * Gets the time remaining. The range of the "remaining time" is started by the argument
     * {@code time} and terminated by the end of the current period if its counted, else the
     * start of the next period that is counted. At most, the next calendar year is searched for
     * a valid period before the method is exited with a {@code null} return value.
     *
     * @param time  the time that starts the "remaining time" interval.
     *
     * @return the amount of remaining time.
     *
     * @throws NullPointerException  if {@code time} is null.
     */
    public Duration getTimeRemaining(UTCTime time) {
        if (time == null) {
            throw new NullPointerException("time cannot be null");
        }

        SchoolPeriod currentPeriod = this.getCurrentPeriod(time);
        SchoolPeriod nextCountedPeriod = this.getNextCountedPeriod(time);
        if (currentPeriod == null || nextCountedPeriod == null) {
            return null;
        }

        if (currentPeriod.isCounted()) {
            return new Duration(time, currentPeriod.getEnd());
        }
        return new Duration(time, nextCountedPeriod.getStart());
    }


    public Duration getTotalTime(UTCTime time) {
        if (time == null) {
            throw new NullPointerException("time cannot be null");
        }

        SchoolPeriod currentPeriod = this.getCurrentPeriod(time);
        SchoolPeriod previousCountedPeriod = this.getPreviousCountedPeriod(time);
        SchoolPeriod nextCountedPeriod = this.getNextCountedPeriod(time);
        if (currentPeriod == null || previousCountedPeriod == null || nextCountedPeriod == null) {
            return null;
        }

        if (currentPeriod.isCounted()) {
            return new Duration(currentPeriod.getStart(), currentPeriod.getEnd());
        }
        return new Duration(previousCountedPeriod.getEnd(), nextCountedPeriod.getStart());
    }
    
}
