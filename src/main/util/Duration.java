package util;


/**
 * Represents a duration of time between a start and end point.
 *
 * @author Jonathan Uhler
 */
public class Duration {

    /** Number of days in one year. */
    public static final int DAYS_PER_YEAR = 365;
    /** Number of days in one week. */
    public static final int DAYS_PER_WEEK = 7;
    /** Number of hours in one day. */
    public static final int HOURS_PER_DAY = 24;
    /** Number of minutes in one hour. */
    public static final int MINUTES_PER_HOUR = 60;
    /** Number of seconds in one minute. */
    public static final int SECONDS_PER_MINUTE = 60;
    /** Number of milliseconds in one second. */
    public static final int MS_PER_SECOND = 1000;
    /** Number of milliseconds in one minute. */
    public static final int MS_PER_MINUTE = MS_PER_SECOND * SECONDS_PER_MINUTE;
    /** Number of milliseconds in one hour. */
    public static final int MS_PER_HOUR = MS_PER_MINUTE * MINUTES_PER_HOUR;

    /** The start time of this duration. */
    private UTCTime start;
    /** The end time of this duration. */
    private UTCTime end;

    /** The number of hours in this duration, on the interval [0, inf). */
    private int hours;
    /** The number of minutes in this duration, on the interval [0, 60). */
    private int minutes;
    /** The number of seconds in this duration, on the interval [0, 60). */
    private int seconds;
    /** The number of milliseconds in this duration, on the interval [0, 1000). */
    private int millis;


    /**
     * Constructs a new {@code Duration} object from a start and end time.
     *
     * @param start  the start time of the duration.
     * @param end    the end time of the duration.
     *
     * @throws IllegalArgumentException  if {@code start == null}.
     * @throws IllegalArgumentException  if {@code end == null}.
     */
    public Duration(UTCTime start, UTCTime end) {
        if (start == null)
            throw new IllegalArgumentException("duration start was null");
        if (end == null)
            throw new IllegalArgumentException("duration end was null");
		
        this.start = start;
        this.end = end;
		
        // Get epoch values
        long startEpoch = start.getEpoch();
        long endEpoch = end.getEpoch();
        long deltaEpoch = endEpoch - startEpoch;

        this.hours = 0;
        this.minutes = 0;
        this.seconds = 0;
        this.millis = 0;

        // Take chunks from the deltaEpoch from hours down to ms
        if (deltaEpoch > 0) {
            this.hours = (int) (deltaEpoch / Duration.MS_PER_HOUR);
            deltaEpoch -= (long) this.hours * (long) Duration.MS_PER_HOUR;

            this.minutes = (int) (deltaEpoch / Duration.MS_PER_MINUTE);
            deltaEpoch -= (long) this.minutes * (long) Duration.MS_PER_MINUTE;

            this.seconds = (int) (deltaEpoch / Duration.MS_PER_SECOND);
            deltaEpoch -= (long) this.seconds * (long) Duration.MS_PER_SECOND;

            this.millis = (int) (deltaEpoch);
        }
    }


    /**
     * Constructs a new {@code Duration} object from quantities of each unit of time.
     *
     * @param hours    the number of hours in the duration, on the interval [0, inf).
     * @param minutes  the number of minutes in the duration, on the interval [0, 60).
     * @param seconds  the number of seconds in the duration, on the interval [0, 60).
     * @param millis   the number of milliseconds in the duration, on the interval [0, 1000).
     *
     * @throws IllegalArgumentException  if {@code hours < 0}.
     * @throws IllegalArgumentException  if {@code minutes < 0 || minutes >= 60}.
     * @throws IllegalArgumentException  if {@code seconds < 0 || seconds >= 60}.
     * @throws IllegalArgumentException  if {@code millis < 0 || millis >= 10000}.
     */
    public Duration(int hours, int minutes, int seconds, int millis) {
        if (hours < 0)
            throw new IllegalArgumentException(hours + " is out of bounds for hours");
        if (minutes < 0 || minutes >= 60)
            throw new IllegalArgumentException(minutes + " is out of bounds for minutes");
        if (seconds < 0 || seconds >= 60)
            throw new IllegalArgumentException(seconds + " is out of bounds for seconds");
        if (millis < 0 || millis >= 1000)
            throw new IllegalArgumentException(millis + " is out of bounds for millis");
		
        this.start = null;
        this.end = null;
		
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.millis = millis;
    }


    /**
     * Returns the start time of the duration.
     *
     * @return the start time of the duration.
     */
    public UTCTime getStart() {
        return this.start;
    }


    /**
     * Returns the end time of the duration.
     *
     * @return the end time of the duration.
     */
    public UTCTime getEnd() {
        return this.end;
    }

	
    /**
     * Returns the number of hours in the duration.
     *
     * @return the number of hours in the duration.
     */
    public int hr() {
        return this.hours;
    }


    /**
     * Returns the number of minutes in the duration.
     *
     * @return the number of minutes in the duration.
     */
    public int min() {
        return this.minutes;
    }

	
    /**
     * Returns the number of seconds in the duration.
     *
     * @return the number of seconds in the duration.
     */
    public int sec() {
        return this.seconds;
    }


    /**
     * Returns the number of milliseconds in the duration.
     *
     * @return the number of milliseconds in the duration.
     */
    public int ms() {
        return this.millis;
    }


    /**
     * Returns a string representation of this {@code Duration}.
     *
     * @return a string representation of this {@code Duration}.
     */
    @Override
    public String toString() {
        if (this.hours == 0)
            return Tools.pad(Integer.toString(this.minutes), 2, '0') + ":" +
                Tools.pad(Integer.toString(this.seconds), 2, '0');
        return this.hours + ":" +
            Tools.pad(Integer.toString(this.minutes), 2, '0') + ":" +
            Tools.pad(Integer.toString(this.seconds), 2, '0');
    }

}
