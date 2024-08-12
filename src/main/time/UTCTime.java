package time;


import java.util.Arrays;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.DateTimeException;


/**
 * Represents a coordinated moment in time, in UTC.
 *
 * @author Jonathan Uhler
 */
public class UTCTime implements Comparable<UTCTime> {

    /** A list of all Unix TZ identifiers. */
    public static final String[] TIMEZONES =
        ZoneId.getAvailableZoneIds().stream().sorted().toArray(String[]::new);
    
    /** The concept of a year. */
    public static final ChronoField YEAR = ChronoField.YEAR;
    /** The concept of a month. */
    public static final ChronoField MONTH = ChronoField.MONTH_OF_YEAR;
    /** The concept of a day in a month. */
    public static final ChronoField DAY = ChronoField.DAY_OF_MONTH;
    /** The concept of a day in a week. */
    public static final ChronoField DAY_OF_WEEK = ChronoField.DAY_OF_WEEK;
    /** The concept of an hour. */
    public static final ChronoField HOUR = ChronoField.HOUR_OF_DAY;
    /** The concept of a minute. */
    public static final ChronoField MINUTE = ChronoField.MINUTE_OF_HOUR;
    /** The concept of a second. */
    public static final ChronoField SECOND = ChronoField.SECOND_OF_MINUTE;
    /** The concept of a millisecond. */
    public static final ChronoField MILLISECOND = ChronoField.MILLI_OF_SECOND;
    
    /** The unit of year. */
    public static final ChronoUnit YEARS = ChronoUnit.YEARS;
    /** The unit of month. */
    public static final ChronoUnit MONTHS = ChronoUnit.MONTHS;
    /** The unit of day. */
    public static final ChronoUnit DAYS = ChronoUnit.DAYS;
    /** The unit of hour. */
    public static final ChronoUnit HOURS = ChronoUnit.HOURS;
    /** The unit of minute. */
    public static final ChronoUnit MINUTES = ChronoUnit.MINUTES;
    /** The unit of second. */
    public static final ChronoUnit SECONDS = ChronoUnit.SECONDS;
    /** The unit of millisecond. */
    public static final ChronoUnit MILLISECONDS = ChronoUnit.MILLIS;
    
    /** The concept of sunday. */
    public static final DayOfWeek SUNDAY = DayOfWeek.SUNDAY;
    /** The concept of monday. */
    public static final DayOfWeek MONDAY = DayOfWeek.MONDAY;
    /** The concept of tuesday. */
    public static final DayOfWeek TUESDAY = DayOfWeek.TUESDAY;
    /** The concept of wednesday. */
    public static final DayOfWeek WEDNESDAY = DayOfWeek.WEDNESDAY;
    /** The concept of thrusday. */
    public static final DayOfWeek THURSDAY = DayOfWeek.THURSDAY;
    /** The concept of friday. */
    public static final DayOfWeek FRIDAY = DayOfWeek.FRIDAY;
    /** The concept of saturday. */
    public static final DayOfWeek SATURDAY = DayOfWeek.SATURDAY;
    
    
    /** The format of a full timestamp. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS VV");
    
    /** Internal representation of the time held by this UTCTime. */
    private ZonedDateTime datetime;
    
    
    /**
     * Constructs a new {@code UTCTime} object from a {@code ZonedDateTime} object.
     *
     * @param datetime  a {@code ZonedDateTime} object.
     */
    private UTCTime(ZonedDateTime datetime) {
        this.datetime = datetime;
    }
    
    
    /**
     * Ensures that a {@code ZonedDateTime} object is in UTC.
     *
     * @param local  a {@code ZonedDateTime} that may or may not be in UTC.
     *
     * @return a new {@code UTCTime} object representing the instant of the argument object in UTC.
     */
    private static UTCTime ensureUTC(ZonedDateTime local) {
        ZonedDateTime utc = local.withZoneSameInstant(ZoneOffset.UTC);
        return new UTCTime(utc);
    }
    
    
    /**
     * Gets the current time as a {@code UTCTime} object.
     *
     * @return the current time as a {@code UTCTime} object.
     */
    public static UTCTime now() {
        ZonedDateTime localNow = ZonedDateTime.now();
        return UTCTime.ensureUTC(localNow);
    }
    
    
    /**
     * Creates a new {@code UTCTime} object from a datetime string and unix TZ identifier.
     *
     * @param datetime  a datetime string in the format {@code yyyy-MM-dd'T'HH:mm:ss.SSS} or
     *                  a date-only string in the format {@code yyyy-MM-dd}.
     * @param timezone  the unix TZ identifier of a timezone.
     *
     * @return a new {@code UTCTime} object from a datetime string and unix TZ identifier.
     *
     * @throws NullPointerException      if {@code datetime} is null.
     * @throws NullPointerException      if {@code timezone} is null.
     * @throws IllegalArgumentException  if the arguments do not represent a valid time.
     */
    public static UTCTime of(String datetime, String timezone) {
        if (datetime == null) {
            throw new NullPointerException("datetime cannot be null");
        }
        if (timezone == null) {
            throw new NullPointerException("timezone cannot be null");
        }

        ZonedDateTime local;
        try {
            local = ZonedDateTime.parse(datetime + " " + timezone, UTCTime.DATE_TIME_FORMAT);
        }
        catch (DateTimeException e) {
            try {
                // Allow construction from just a date yyyy-MM-dd
                local = ZonedDateTime.parse(datetime + "T00:00:00.000 " + timezone,
                                            UTCTime.DATE_TIME_FORMAT);
            }
            catch (DateTimeException e2) {
                throw new IllegalArgumentException("invalid datetime format for " + datetime);
            }
        }
        return UTCTime.ensureUTC(local);
    }
    
    
    /**
     * Creates a new {@code UTCTime} object representing the same instant in time as this object
     * in the specified timezone.
     *
     * @param timezone  the unix TZ identifier to convert this {@code UTCTime} to.
     *
     * @return a new {@code UTCTime} representing the same instant in time with the specified
     *         timezone.
     *
     * @throws NullPointerException      if {@code timezone} is null.
     * @throws IllegalArgumentException  if {@code timezone} is an invalid TZ identifier.
     */
    public UTCTime to(String timezone) {
        if (timezone == null) {
            throw new NullPointerException("timezone cannot be null");
        }

        ZoneId zone;
        try {
            zone = ZoneId.of(timezone);
        }
        catch (DateTimeException e) {
            throw new IllegalArgumentException("invalid timezone id: " + timezone + ", " + e);
        }
	
        ZonedDateTime local = this.datetime.withZoneSameInstant(zone);
        return new UTCTime(local);
    }
    
    
    /**
     * Returns this {@code UTCTime} object as a {@code ZonedDateTime} object.
     *
     * @return this {@code UTCTime} object as a {@code ZonedDateTime} object.
     */
    public ZonedDateTime asZonedDateTime() {
        return this.datetime;
    }
    
    
    /**
     * Returns whether this {@code UTCTime} is chronologically before the argument.
     *
     * @param other  a {@code UTCTime} to compare to.
     *
     * @return whether this {@code UTCTime} is chronologically before the argument.
     *
     * @throws NullPointerException  if {@code other} is null.
     */
    public boolean isBefore(UTCTime other) {
        if (other == null) {
            throw new NullPointerException("other cannot be null");
        }

        return this.datetime.isBefore(other.asZonedDateTime());
    }
    
    
    /**
     * Returns whether this {@code UTCTime} represents the same chronological instant as the 
     * argument.
     *
     * @param other  a {@code UTCTime} to compare to.
     *
     * @return whether this {@code UTCTime} represents the same chronological instant as the 
     *         argument.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        UTCTime other;
        try {
            other = (UTCTime) o;
        }
        catch (ClassCastException e) {
            return false;
        }

        return this.datetime.isEqual(other.asZonedDateTime());
    }
    
    
    /**
     * Compares this {@code UTCTime} object to another {@code UTCTime} object.
     *
     * @param other  the {@code UTCTime} to compare to.
     *
     * @return {@code 0} if the times are equal, {@code -1} if this object is before the argument,
     *         and {@code 1} if this object is after the argument.
     *
     * @throws NullPointerException  if {@code other} is null.
     *
     * @see isBefore
     * @see isEqual
     */
    @Override
    public int compareTo(UTCTime other) {
        if (other == null) {
            throw new NullPointerException("other cannot be null");
        }

        if (this.equals(other)) {
            return 0;
        }
        else if (this.isBefore(other)) {
            return -1;
        }
        else {
            return 1;
        }
    }
    
    
    /**
     * Gets the value of a field in this {@code UTCTime}.
     *
     * @param field  the time-related field to get the value of. This should be one of the
     *               {@code ChronoField}s of {@code UTCTime}.
     *
     * @return the value of a field in this {@code UTCTime}.
     *
     * @throws NullPointerException  if {@code field} is null.
     */
    public int get(ChronoField field) {
        if (field == null) {
            throw new NullPointerException("field cannot be null");
        }

        return this.datetime.get(field);
    }
    
    
    /**
     * Returns a new {@code UTCTime} object where the specified time-related unit has the
     * specified amount added to the original value.
     *
     * @param amount  the amount to add to the specified unit.
     * @param unit    the unit to add the specified amount to.
     *
     * @return a new {@code UTCTime} object where the specified time-related unit has the
     *         specified amount added to the original value.
     *
     * @throws NullPointerException  if {@code unit} is null.
     */
    public UTCTime plus(long amount, ChronoUnit unit) {
        if (unit == null) {
            throw new NullPointerException("unit cannot be null");
        }
	
        return new UTCTime(this.datetime.plus(amount, unit));
    }
    
    
    /**
     * Gets the millisecond offset from the unix epoch of this {@code UTCTime}.
     *
     * @return the millisecond offset from the unix epoch of this {@code UTCTime}.
     */
    public long getEpoch() {
        return this.datetime.toInstant().toEpochMilli();
    }
    
    
    /**
     * Returns a new {@code UTCTime} object with the day value set to the closest instance of the
     * specified day of the week that is on or before the this time.
     *
     * @param day  the day of the week to shift backwards to.
     *
     * @return a new {@code UTCTime} object with the day value set to the closest instance of the
     *         specified day of the week that is on or before this time.
     *
     * @throws NullPointerException  if {@code day} is null.
     */
    public UTCTime shiftedToPrevious(DayOfWeek day) {
        if (day == null) {
            throw new NullPointerException("day cannot be null");
        }
        
        ZonedDateTime closestDateTime = this.datetime.with(TemporalAdjusters.previousOrSame(day));
        UTCTime closestUTC = UTCTime.ensureUTC(closestDateTime);
        return closestUTC;
    }
    
    
    /**
     * Returns a new {@code UTCTime} object with the day value set to the closest instance of the
     * specified day of the week that is on or after the this time.
     *
     * @param day  the day of the week to shift forwards to.
     *
     * @return a new {@code UTCTime} object with the day value set to the closest instance of the
     *         specified day of the week that is on or after this time.
     *
     * @throws NullPointerException  if {@code day} is null.
     */
    public UTCTime shiftedToNext(DayOfWeek day) {
        if (day == null) {
            throw new NullPointerException("day cannot be null");
        }
        
        ZonedDateTime closestDateTime = this.datetime.with(TemporalAdjusters.nextOrSame(day));
        UTCTime closestUTC = UTCTime.ensureUTC(closestDateTime);
        return closestUTC;
    }
    
    
    /**
     * Returns a new {@code UTCTime} object with the same date as this object and a time of 
     * {@code 00:00:00.000}.
     *
     * @return a new {@code UTCTime} object with the same date as this object and a time of 
     *         {@code 00:00:00.000}.
     */
    public UTCTime toMidnight() {
        ZonedDateTime midnightDateTime = this.datetime.truncatedTo(UTCTime.DAYS);
        UTCTime midnightUTC = UTCTime.ensureUTC(midnightDateTime);
        return midnightUTC;
    }
    
    
    /**
     * Returns a new {@code UTCTime} object representing midnight in the specified timezone.
     *
     * The returned {@code UTCTime} object is still in UTC time. The operation is order is:
     *
     * - Convert this object to the specified timezone.
     * - Convert the new object (in the specified local timezone) to midnight in that tz.
     * - Convert the local-midnight object back to UTC time. The result may or may not have a
     *   final time of {@code 00:00:00.000}.
     *
     * @param timezone  the unix TZ identifier to get a UTC-aligned midnight time for.
     *
     * @return a new {@code UTCTime} object representing midnight in the specified timezone.
     *
     * @throws NullPointerException  if {@code timezone} is null.
     */
    public UTCTime toMidnight(String timezone) {
        if (timezone == null) {
            throw new NullPointerException("timezone cannot be null");
        }

        UTCTime localTime = this.to(timezone);
        localTime = localTime.toMidnight();
        return localTime.to("Z");
    }
    
    
    /**
     * Returns the day tag of this {@code UTCTime}. The day tag is in the format {@code yyyy-MM-dd}.
     *
     * @return the day tag of this {@code UTCTime}.
     */
    public String getDayTag() {
        return String.format("%04d", this.datetime.get(UTCTime.YEAR)) + "-" +
               String.format("%02d", this.datetime.get(UTCTime.MONTH)) + "-" +
               String.format("%02d", this.datetime.get(UTCTime.DAY));
    }
    
    
    /**
     * Returns the day tag of the closest sunday to the current time. This method is identical
     * to {@code getTagForClosest(UTCTime.SUNDAY)}.
     *
     * @return the day tag of the closest sunday to the current time.
     */
    public String getWeekTag() {
        return this.shiftedToPrevious(UTCTime.SUNDAY).getDayTag();
    }
    
    
    /**
     * Returns a string representation of this {@code UTCTime} object.
     *
     * @return a string representation of this {@code UTCTime} object.
     */
    @Override
    public String toString() {
        return String.format("%04d", this.datetime.get(UTCTime.YEAR)) + "-" +
               String.format("%02d", this.datetime.get(UTCTime.MONTH)) + "-" +
               String.format("%02d", this.datetime.get(UTCTime.DAY)) + "T" +
               String.format("%02d", this.datetime.get(UTCTime.HOUR)) + ":" +
               String.format("%02d", this.datetime.get(UTCTime.MINUTE)) + ":" +
               String.format("%02d", this.datetime.get(UTCTime.SECOND)) + "." +
               String.format("%03d", this.datetime.get(UTCTime.MILLISECOND)) + " " +
               this.datetime.getZone();
    }
    
}
