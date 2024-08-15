package time;


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

    private UTCTime start;
    private UTCTime end;

    private int hours;
    private int minutes;
    private int seconds;
    private int millis;
    
    
    /**
     * Constructs a new {@code Duration} object from a start and end time.
     *
     * @param start  the start time of the duration.
     * @param end    the end time of the duration.
     *
     * @throws NullPointerException      if {@code start} is null.
     * @throws NullPointerException      if {@code end} is null.
     * @throws IllegalArgumentException  if {@code start} is not at or before {@code end}.
     */
    public Duration(UTCTime start, UTCTime end) {
        if (start == null) {
            throw new NullPointerException("start cannot be null");
        }
        if (end == null) {
            throw new NullPointerException("end cannot be null");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("start cannot be after end");
        }
	
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
     * @throws IllegalArgumentException  if any time unit is out of bounds.
     */
    public Duration(int hours, int minutes, int seconds, int millis) {
        if (hours < 0) {
            throw new IllegalArgumentException(hours + " is out of bounds for hours");
        }
        if (minutes < 0 || minutes >= Duration.MINUTES_PER_HOUR) {
            throw new IllegalArgumentException(minutes + " is out of bounds for minutes");
        }
        if (seconds < 0 || seconds >= Duration.SECONDS_PER_MINUTE) {
            throw new IllegalArgumentException(seconds + " is out of bounds for seconds");
        }
        if (millis < 0 || millis >= Duration.MS_PER_SECOND) {
            throw new IllegalArgumentException(millis + " is out of bounds for millis");
        }
	
        this.start = null;
        this.end = null;
	
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.millis = millis;
    }


    public double portionComplete(Duration remaining) {
        long totalMillis =
            this.hours * Duration.MS_PER_HOUR +
            this.minutes * Duration.MS_PER_MINUTE +
            this.seconds * Duration.MS_PER_SECOND +
            this.millis;
        long remainingMillis =
            remaining.hours * Duration.MS_PER_HOUR +
            remaining.minutes * Duration.MS_PER_MINUTE +
            remaining.seconds * Duration.MS_PER_SECOND +
            remaining.millis;

        return 1.0 - ((double) remainingMillis / (double) totalMillis);
    }
    
    
    /**
     * Returns the start time of the duration.
     *
     * If this {@code Duration} was not constructed from a start and end time, this will be null.
     *
     * @return the start time of the duration.
     */
    public UTCTime getStart() {
        return this.start;
    }
    
    
    /**
     * Returns the end time of the duration.
     *
     * If this {@code Duration} was not constructed from a start and end time, this will be null.
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
        String paddedHours = String.format("%02d", this.hours);
        String paddedMinutes = String.format("%02d", this.minutes);
        String paddedSeconds = String.format("%02d", this.seconds);
        return this.hours + ":" + paddedMinutes + ":" + paddedSeconds;
    }
    
}
